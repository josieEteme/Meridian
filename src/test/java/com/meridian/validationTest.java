package com.meridian;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import tech.tablesaw.api.Table;
import java.io.InputStream;
import java.nio.file.Path;
import javafx.util.Pair;
import java.util.*;

//Test Class: Reads mock data - ALL tests reference the same mock data csv file
public class validationTest {

    //private variables
    private Validation v;
    private Path missing_trades_path = Path.of("src/test/java/com/meridian/resources/missing_trades.csv");
    private Path outOfRange_trades_path = Path.of("src/test/java/com/meridian/resources/outOfRange_trades.csv");
    private Path invalid_trades_path = Path.of("src/test/java/com/meridian/resources/invalid_trades.csv");
    private Path self_trades_path = Path.of("src/test/java/com/meridian/resources/self_trades.csv");
    private List<Pair<String,Integer>> expected; 
    
    //SETUP: To avoid redundancy
    @BeforeEach
    void setUp(){
        v = new Validation();
        expected = new ArrayList<>();
    }

    //TESTS:
    @Test
    void givenMissingFields_whenValidate_thenReturnRowsWithMissing() {
        
        //GIVEN MISSING TRADES 
        //WHEN CALL VALIDATE()
        v.validate(missing_trades_path);

        //CREATING EXPECTED RESULT
        expected.add(new Pair<>("missing_trade_id", 1));
        expected.add(new Pair<>("missing_instrument_id", 1));
        expected.add(new Pair<>("missing_instrument_type", 1));
        expected.add(new Pair<>("missing_trade_date", 1));
        expected.add(new Pair<>("missing_buyer", 1));
        expected.add(new Pair<>("missing_seller", 1));
        expected.add(new Pair<>("missing_quantity", 1));
        expected.add(new Pair<>("missing_price", 1));
        expected.add(new Pair<>("missing_trade_status", 1));


        //ACTUAL RESULT
        List<Pair<String,Integer>> actual = new ArrayList<>(); 

        for(Map.Entry<String, Integer> entry : v.errors.errorCount.entrySet()){
            Pair<String, Integer> curP = new Pair(entry.getKey(), entry.getValue());   
            if(curP.getValue() > 0) { 
                actual.add(curP); 
            }
        }

        //SORT TO COMPARE ACTUAL TO EXPECTED
        actual.sort(Comparator.comparing(Pair::getKey));
        expected.sort(Comparator.comparing(Pair::getKey));

        //THEN ACTUAL AND EXPECTED LISTS SHOULD BE EQUAL
        assertEquals(expected, actual, "Assertion Failed - lists are not equal");
    }

    @Test
    void givenOutOfRangeQuantityPrice_whenValidate_thenReturnRowsOutRange(){

        //GIVEN 
        //THEN CALL VALIDATE()
        v.validate(outOfRange_trades_path);
        expected.add(new Pair<>("price_outOf_range", 2));     // rows 3, 4
        expected.add(new Pair<>("negative_price", 1));         // row 5
        expected.add(new Pair<>("quantity_outOf_range", 4));   // rows 6,7,8,9

        //ACTUAL RESULT
        List<Pair<String,Integer>> actual = new ArrayList<>(); 

        for(Map.Entry<String, Integer> entry : v.errors.errorCount.entrySet()){
            Pair<String, Integer> curP = new Pair(entry.getKey(), entry.getValue());   
            if(curP.getValue() > 0) { 
                actual.add(curP); 
            }
        }

        //SORT TO COMPARE ACTUAL TO EXPECTED
        actual.sort(Comparator.comparing(Pair::getKey));
        expected.sort(Comparator.comparing(Pair::getKey));

        //THEN ACTUAL AND EXPECTED LISTS SHOULD BE EQUAL
        assertEquals(expected, actual, "Assertion Failed - lists are not equal");
    }

    @Test
    void givenInvalidRows_whenValidate_returnInvalidRowCounts(){

        //GIVEN
        //THEN CALL VALIDATE
        v.validate(invalid_trades_path);

        //CREATING EXPECTED RESULT
        expected.add(new Pair<>("invalid_trade_id_format", 6));   // rows 1-5, 22
        expected.add(new Pair<>("invalid_date_format", 6));       // rows 6-9, 22, 23
        expected.add(new Pair<>("invalid_quantity", 7));          // rows 10-15, 22
        expected.add(new Pair<>("invalid_price", 7));             // rows 16-21, 23

        //ACTUAL RESULT
        List<Pair<String,Integer>> actual = new ArrayList<>(); 

        for(Map.Entry<String, Integer> entry : v.errors.errorCount.entrySet()){
            Pair<String, Integer> curP = new Pair(entry.getKey(), entry.getValue());   
            if(curP.getValue() > 0) { 
                actual.add(curP); 
            }
        }

        //SORT TO COMPARE ACTUAL TO EXPECTED
        actual.sort(Comparator.comparing(Pair::getKey));
        expected.sort(Comparator.comparing(Pair::getKey));

        //THEN ACTUAL AND EXPECTED LISTS SHOULD BE EQUAL
        assertEquals(expected, actual, "Assertion Failed - lists are not equal");
    }

    @Test
    void givenSelfTrades_whenValidate_thenReturnFiveSelfTradeCounts(){

        //GIVEN
        //THEN CALL VALIDATE
        v.validate(self_trades_path);

        //CREATED EXPECTED RESULT
        expected.add(new Pair<>("self_trades", 5));   // rows 1-5

        //ACTUAL RESULT
        List<Pair<String,Integer>> actual = new ArrayList<>(); 

        for(Map.Entry<String, Integer> entry : v.errors.errorCount.entrySet()){
            Pair<String, Integer> curP = new Pair(entry.getKey(), entry.getValue());   
            if(curP.getValue() > 0) { 
                actual.add(curP); 
            }
        }

        //SORT TO COMPARE ACTUAL TO EXPECTED
        actual.sort(Comparator.comparing(Pair::getKey));
        expected.sort(Comparator.comparing(Pair::getKey));

        //THEN ACTUAL AND EXPECTED LISTS SHOULD BE EQUAL
        assertEquals(expected, actual, "Assertion Failed - lists are not equal");


    }
}