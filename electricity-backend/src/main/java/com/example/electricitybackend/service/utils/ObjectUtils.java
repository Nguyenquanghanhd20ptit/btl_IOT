package com.example.electricitybackend.service.utils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ObjectUtils {
    public static <T> T updateNotNull(T objDb, T objSave) {
        Field[] fields = objSave.getClass().getDeclaredFields();
        Arrays.stream(fields)
                .forEach(field -> {
                    field.setAccessible(true);
                    try {
                        if (field.get(objSave) != null) {
                            field.set(objDb, field.get(objSave));
                        }
                    } catch (IllegalAccessException e) {
                        e.fillInStackTrace();
                    }
                });
        return objDb;
    }
}
