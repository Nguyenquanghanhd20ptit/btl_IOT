package com.example.electricitybackend.commons.utils;

import java.util.Arrays;

public class StringUtil {

    public static String convertStringPostgre(String value){
        String a[] = value.split("\\s+");
        String kq = "%" + String.join("%",a) +"%";
        return kq;
    }
}
