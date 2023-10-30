package com.example.electricitybackend.commons.data.core.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Json {

    public static <T> T decodeValue(String str, Class<T> clazz) {
        try {
            return JsonMapper.getObjectMapper().readValue(str, clazz);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to decode: " + e.getMessage());
        }
    }
    static <T> Stream<T> asStream(Iterator<T> sourceIterator) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), false);
    }
}
