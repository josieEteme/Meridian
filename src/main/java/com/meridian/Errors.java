package com.meridian;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

//This is the Error class. Creates a HashMap of Errors that will be imported and accessed in the Validation file

public class Errors {
        
        public Map<String, Integer> errorCount = new HashMap<>();

        private static final List<String> ERROR_TYPES = List.of(
        "missing_trade_id",
        "invalid_trade_id_format",
        "duplicate_trade_id",
        "missing_instrument_id",
        "invalid_date_format",
        "missing_trade_status",
        "missing_buyer",
        "missing_seller",
        "missing_quantity",
        "invalid_quantity",
        "invalid_price",
        "negative_price",
        "price_out_of_range",
        "quantity_outOf_range",
        "self_trades"
        );

        public Errors(){
                for( String type : ERROR_TYPES){
                        this.errorCount.put(type, 0);
                }
        }


}

