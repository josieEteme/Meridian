package com.meridian; 

import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;
import java.util.*;

import java.util.regex.Pattern;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Validation {

    //csv path
    private Path csvPath = Path.of("meridian_trades_small.csv");

    //regex patterns for validation checks
    private String TRADE_PATTERN = "^TRD-[0-9A-Fa-f]{10}$";
    private String DATE_PATTERN = "^(\\d{4}-\\d{2}-\\d{2}|\\d{4}\\.\\d{2}\\.\\d{2}|\\d{2}/\\d{2}/\\d{4}|\\d{2}-\\d{2}-\\d{4})$";
    private String IS_STRING = "^-?\\d+(\\.\\d+)?$";

    
    //errorCount hashmap
    public Errors errors = new Errors(); 

    public void validate(Path csv_path){

        CsvReadOptions options = CsvReadOptions.builder(csv_path.toFile())
            .columnTypes(name -> ColumnType.STRING) //Load columns as string or text first to avoid an error since rows within coumns are of different types
            .build();

        Table table = Table.read().usingOptions(options);

        int rows = table.rowCount();

        StringColumn buyer      = table.stringColumn("buyer");
        StringColumn seller     = table.stringColumn("seller");
        StringColumn tradeId    = table.stringColumn("trade_id");
        StringColumn qtyStr     = table.stringColumn("quantity");
        StringColumn priceStr   = table.stringColumn("price");
        StringColumn tradeDate  = table.stringColumn("trade_date");

        //MISSING VALIDATION CHECKS
        errors.errorCount.put("missing_buyer", buyer.countMissing());
        errors.errorCount.put("missing_seller", seller.countMissing());
        errors.errorCount.put("missing_trade_id", tradeId.countMissing());
        errors.errorCount.put("missing_quantity", qtyStr.countMissing());
        errors.errorCount.put("missing_trade_status", table.stringColumn("trade_status").countMissing());
        errors.errorCount.put("missing_instrument_type", table.stringColumn("instrument_type").countMissing());
        errors.errorCount.put("missing_instrument_id", table.stringColumn("instrument_id").countMissing());

        //INVALID CHECKS (shape of the text)
        //Selection = bitmap, 1s are rows that match the regex, so size() counts the VALID rows.
        //Invalid = all rows - missing rows - matching rows (blanks are already counted as missing).
        errors.errorCount.put("invalid_date_format", rows - tradeDate.matchesRegex(DATE_PATTERN).size() - tradeDate.countMissing());
        errors.errorCount.put("invalid_trade_id_format", rows - tradeId.matchesRegex(TRADE_PATTERN).size() - tradeId.countMissing());
        errors.errorCount.put("invalid_quantity", rows -  qtyStr.matchesRegex(IS_STRING).size() - qtyStr.countMissing());
        errors.errorCount.put("invalid_price", rows - priceStr.matchesRegex(IS_STRING).size() - priceStr.countMissing());

        //RANGE VALIDATION CHECKS (need numeric columns, built only from rows that look like numbers)
        DoubleColumn price = table.where(priceStr.matchesRegex(IS_STRING)).stringColumn("price").parseDouble();
        DoubleColumn qty   = table.where(qtyStr.matchesRegex(IS_STRING)).stringColumn("quantity").parseDouble();

        errors.errorCount.put("negative_price", price.isLessThan(0).size());
        errors.errorCount.put("price_outOf_range", price.isGreaterThan(500).size());
        errors.errorCount.put("quantity_outOf_range", qty.isLessThan(100).size() + qty.isGreaterThan(50_000).size());
    }
}









