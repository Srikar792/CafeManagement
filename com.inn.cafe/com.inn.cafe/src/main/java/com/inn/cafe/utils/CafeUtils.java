package com.inn.cafe.utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CafeUtils {

    private CafeUtils(){

    }

    public static ResponseEntity<String> getResponse(String responsemessage, HttpStatus httpStatus){
        return new ResponseEntity<>("\"message\":\""+responsemessage+"\"", httpStatus);
    }
}
