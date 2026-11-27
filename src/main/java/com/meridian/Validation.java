package com.meridian; 

import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.*;
import tech.tablesaw.selection.Selection; 
import java.util.*;
import java.util.regex.Pattern;             
import java.io.*;                           
import java.nio.charset.StandardCharsets;   
import java.nio.file.*;      

public class Validation {

    //CSV PATH
    private Path csvPath = Path.of("meridian_trades_small.csv");

    //FILE-TABLE-CHECKS-OBJECT INSTANTIATION
    private FileTableChecks ftCheck = new FileTableChecks(); 

    //REGEX PATTERNS 
    private String TRADE_PATTERN = "^TRD-[0-9A-Fa-f]{10}$";
    private String DATE_PATTERN = "^(\\d{4}-\\d{2}-\\d{2}|\\d{4}\\.\\d{2}\\.\\d{2}|\\d{2}/\\d{2}/\\d{4}|\\d{2}-\\d{2}-\\d{4})$";
    private String IS_STRING = "^-?\\d+(\\.\\d+)?$";

    
    //ERROR HASHMAP
    public Errors errors = new Errors(); 

    public void validate(Path csv_path) throws IOException{

        //CHECK FILE STRUCTURE
        ftCheck.fileErrorChecks(csv_path);
        ftCheck.checkFileRows(csv_path);

        //READ FILE + CREATE TABLESAW OBJ TABLE
        Table table;
        try{
            CsvReadOptions options = CsvReadOptions.builder(csv_path.toFile())
                .columnTypes(name -> ColumnType.STRING) //Load columns as string or text first to avoid an error since rows within coumns are of different types
                .build();

            table = Table.read().usingOptions(options);
        } catch (RuntimeException e) {
            throw new IllegalStateException("Malformed CSV (check quotes, delimiter, row lengths): " + csv_path, e);
        }


        //CHECK TABLE STRUCTURE
        ftCheck.tableStructureChecks(table, csv_path);


        //VALIDATION CHECKS
        int rows = table.rowCount();

        //VARIABLES 
        StringColumn buyer      = table.stringColumn("buyer");
        StringColumn seller     = table.stringColumn("seller");
        StringColumn tradeId    = table.stringColumn("trade_id");
        StringColumn qtyStr     = table.stringColumn("quantity");
        StringColumn priceStr   = table.stringColumn("price");
        StringColumn tradeDate  = table.stringColumn("trade_date");

        Selection validDateFormat = tradeDate.matchesRegex(DATE_PATTERN);
        Selection validTradeId = tradeId.matchesRegex(TRADE_PATTERN);
        Selection validQuantity = qtyStr.matchesRegex(IS_STRING);
        Selection validPrice = priceStr.matchesRegex(IS_STRING);

        DoubleColumn price = table.where(validPrice).stringColumn("price").parseDouble();
        DoubleColumn qty   = table.where(validQuantity).stringColumn("quantity").parseDouble();

       //CLEAN TRADES 
        Selection priceNotNeg  = price.isGreaterThanOrEqualTo(0);
        Selection priceInRange = price.isLessThanOrEqualTo(499);
        Selection qtyInRange1  = qty.isGreaterThanOrEqualTo(100);
        Selection qtyInRange2  = qty.isLessThanOrEqualTo(50000);

        //MISSING VALIDATION CHECKS
        errors.errorCount.put("missing_buyer", buyer.countMissing());
        errors.errorCount.put("missing_seller", seller.countMissing());
        errors.errorCount.put("missing_trade_id", tradeId.countMissing());
        errors.errorCount.put("missing_quantity", qtyStr.countMissing());
        errors.errorCount.put("missing_trade_status", table.stringColumn("trade_status").countMissing());
        errors.errorCount.put("missing_instrument_type", table.stringColumn("instrument_type").countMissing());
        errors.errorCount.put("missing_instrument_id", table.stringColumn("instrument_id").countMissing());
        errors.errorCount.put("missing_trade_date", tradeDate.countMissing());
        errors.errorCount.put("missing_price", priceStr.countMissing());

        //INVALID CHECKS (shape of the text)
        //Selection = wrapper around a bitmap, 1s are rows that match the regex, so size() counts the VALID rows.
        //Invalid = all rows - missing rows - matching rows (blanks are already counted as missing)
        errors.errorCount.put("invalid_date_format", rows - validDateFormat.size() - tradeDate.countMissing());
        errors.errorCount.put("invalid_trade_id_format", rows - validTradeId.size() - tradeId.countMissing());
        errors.errorCount.put("invalid_quantity", rows -  validQuantity.size() - qtyStr.countMissing());
        errors.errorCount.put("invalid_price", rows - validPrice.size() - priceStr.countMissing());
        

        //RANGE VALIDATION CHECKS (need numeric columns, built only from rows that match regex IS_STRING
        errors.errorCount.put("negative_price", price.isLessThan(0).size());
        errors.errorCount.put("price_outOf_range", price.isLessThan(10).size() + price.isGreaterThan(500).size());
        errors.errorCount.put("quantity_outOf_range", qty.isLessThan(100).size() + qty.isGreaterThan(50000).size());

        //SELF TRADE CHECKS
        errors.errorCount.put("self_trades", buyer.isEqualTo(seller).size()); 

        //GATHERING CLEAN TRADES
         Selection valid = validDateFormat
        .and(validTradeId)
        .and(validQuantity)
        .and(validPrice)
        .and(priceNotNeg)
        .and(priceInRange)
        .and(qtyInRange1)
        .and(qtyInRange2);

        Table cleanTrades = table.where(valid);

        //CALL WRITE CLEAN OUTPUT
        WriteCleanOutput w = new WriteCleanOutput();
        w.writeOutput(cleanTrades);

    }
}









