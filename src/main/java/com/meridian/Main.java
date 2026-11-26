package com.meridian;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.nio.file.Path;
import java.util.Map;

@Command(name = "meridian", description = "Clean and validate trade data.")
public class Main implements Runnable{

    @Option(names = "--validate", description = "Validate the data and write clean output files.")
    private boolean validate;


    @Override 
    public void run(){

        if(validate){

            Validation v = new Validation();
            v.validate(Path.of("meridian_trades_small.csv"));

            for (Map.Entry<String, Integer> entry : v.errors.errorCount.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

        }
    }

    //logging object instantiation
    //private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[]args){
            
        int exitCode = new CommandLine(new Main()).execute(args);
        System.out.print("exitcode: " + exitCode);
    }
}