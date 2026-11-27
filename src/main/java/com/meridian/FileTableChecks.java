package com.meridian; 

import tech.tablesaw.api.*;
import tech.tablesaw.io.csv.CsvReadOptions;
import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.io.Reader;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import java.io.Reader;


public class FileTableChecks {  

    //CONSTANTS
    private String[] FIELDS = {
        "trade_id",
        "instrument_id",
        "instrument_type",
        "trade_date",
        "buyer",
        "seller",
        "quantity",
        "price",
        "trade_status"
    };


    //FILE PATH CHECKS 
    public void fileErrorChecks(Path csv_path){
        if (csv_path == null) {
        throw new IllegalArgumentException("CSV path is null");
        }
        if (!Files.exists(csv_path)) {
            throw new IllegalArgumentException("File not found: " + csv_path);
        }
        if (!Files.isRegularFile(csv_path)) {
            throw new IllegalArgumentException("Path is not a file: " + csv_path);
        }
        if (!Files.isReadable(csv_path)) {
            throw new IllegalArgumentException("File is not readable: " + csv_path);
        }
        try {
            if (Files.size(csv_path) == 0) {
                throw new IllegalArgumentException("File is empty: " + csv_path);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot determine size of " + csv_path, e);
        }
    }

    //FILE ROW CHECK
    public void checkFileRows(Path csv_path){

        List<Long> badRows = new ArrayList<>();
        try (Reader reader = Files.newBufferedReader(csv_path);
            CSVParser parser = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader)) {

            int headerCount = parser.getHeaderMap().size();

        for (CSVRecord record : parser) {
            if (record.size() != headerCount) { 
                badRows.add(record.getRecordNumber());
            }
        }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + csv_path, e);
        }

        if (!badRows.isEmpty()) {
            throw new IllegalStateException("Rows with wrong field count (record numbers): " + badRows);
        }
    }

    //TABLE STRUCTURE CHECKS
    public void tableStructureChecks(Table table, Path csv_path){

        if(table.rowCount() == 0){
            throw new IllegalStateException("CSV has a header but no data rows: " + csv_path);
        }

        List<String> missingCols = new ArrayList<>();
        for (String curField : FIELDS) {
            if (!table.columnNames().contains(curField)) {
                missingCols.add(curField);
            }
        }
        if (!missingCols.isEmpty()) {
            throw new IllegalStateException("Missing required columns: " + missingCols);
        }

        List<String> names = table.columnNames();
        if (new HashSet<>(names).size() != names.size()) {
            throw new IllegalStateException("Duplicate column names: " + names);
        }
    }

}