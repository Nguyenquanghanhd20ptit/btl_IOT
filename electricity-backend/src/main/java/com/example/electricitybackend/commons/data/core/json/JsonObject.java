package com.example.electricitybackend.commons.data.core.json;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.example.electricitybackend.commons.data.core.json.Json.decodeValue;
import static com.example.electricitybackend.commons.data.core.json.JsonMapper.getObjectMapper;


public class JsonObject {
    private Map<String, Object> map;
    private String json;

    public JsonObject(String json) {
        this.json = json;
        fromJson(json);
    }



    public JsonObject() {
        map = new LinkedHashMap<>();
    }

    public JsonObject put(String key, Object value) {
        map.put(key, value);
        return this;
    }

    public JsonObject(Map<String, Object> map) {
        this.map = map;
    }


    public static JsonObject from(Object o) throws JsonProcessingException {
        try {
            String value = getObjectMapper().writeValueAsString(o);
            return new JsonObject(value);
        } catch (JsonProcessingException e) {
            throw e;
        }
    }


    public static JsonObject mapFrom(Object obj) {
        return new JsonObject((Map<String, Object>) getObjectMapper().convertValue(obj, Map.class));
    }

    public <T> T mapTo(Class<T> type) {
        if (map.isEmpty()) return null;
        return getObjectMapper().convertValue(map, type);
    }

    private void fromJson(String json) {
        try {
            map = decodeValue(json, Map.class);
        } catch (Exception exception) {
        }
    }
}
