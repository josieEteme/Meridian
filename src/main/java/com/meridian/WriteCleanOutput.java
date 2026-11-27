package com.meridian; 

import tech.tablesaw.api.*;
import tech.tablesaw.selection.Selection; 
import tech.tablesaw.io.csv.*;                
import java.nio.file.*;                        
import java.util.*;
import java.io.*;

public class WriteCleanOutput{

    //need to also make the flatten() method for the .json file

    public void writeOutput(Table cleanTrades) throws IOException {

        //WRITE CLEAN TRADES TO OUTPUT FILE
        Files.createDirectories(Path.of("output"));

        try (Writer writer = Files.newBufferedWriter(Path.of("output/clean_trades.csv"))) {
            CsvWriteOptions options = CsvWriteOptions.builder(writer)
                .header(true)
                .separator(',')
                .quoteChar('"')
                .escapeChar('"')   
                .build();

            cleanTrades.write().usingOptions(options);
        }
    }
}



