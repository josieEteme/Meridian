package com.meridian;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import org.tinylog.Logger;

import java.nio.file.Path;
import java.util.*;
import java.io.*;  

//Main class: Meridian/--validation command creation
@Command(name = "meridian", description = "Clean and validate trade data.")
public class Main implements Runnable{

    @Option(names = "--validate", description = "Validate the data and write clean output files.")
    private boolean validate;

    @Override 
    public void run(){

        if(validate){

            Logger.info("Application Started");
            
            Validation v = new Validation();
            try {
                v.validate(Path.of("meridian_trades_small.csv"));
            } catch (IOException e) {
                Logger.error(e, "Failed to validate the CSV file");
                return;
            }

            for (Map.Entry<String, Integer> entry : v.errors.errorCount.entrySet()) {
                    
                if(entry.getValue() > 0){
                    StringBuilder errorMessage = new StringBuilder(); 
                    errorMessage.append(entry.getKey().toString());
                    errorMessage.append(": "); 
                    errorMessage.append(entry.getValue().toString());

                    Logger.error(errorMessage.toString());
                }
            }

        }
    }

    public static void main(String[]args){
            
        int exitCode = new CommandLine(new Main()).execute(args);
        //System.out.print("exitcode: " + exitCode);
    }
}