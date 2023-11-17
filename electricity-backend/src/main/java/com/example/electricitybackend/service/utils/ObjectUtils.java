package com.example.electricitybackend.service.utils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class ObjectUtils {
    public static <T> T updateNotNull(T objDb, T objSave, List<String> notUpdateField) {
        Field[] fields = objSave.getClass().getDeclaredFields();
        Arrays.stream(fields)
                .forEach(field -> {
                    if(!notUpdateField.contains(field.getName())){
                        field.setAccessible(true);
                        try {
                            if (field.get(objSave) != null) {
                                field.set(objDb, field.get(objSave));
                            }
                        } catch (IllegalAccessException e) {
                            e.fillInStackTrace();
                        }
                    }
                });
        return objDb;
    }
}
