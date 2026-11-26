package com.meridian;

//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.nio.file.Path;
import java.util.Map;

public class Main {

    //logging object instantiation
    //private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[]args){
        Validation v = new Validation();
        v.validate(Path.of("meridian_trades_small.csv"));

        for (Map.Entry<String, Integer> entry : v.errors.errorCount.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}