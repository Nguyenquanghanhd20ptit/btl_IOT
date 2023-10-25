package com.example.electricitybackend.commons.data.core.json;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.example.electricitybackend.commons.data.core.json.JsonMapper.getObjectMapper;

public class JsonArray implements Iterable<Object> {
    private List<Object> list;

    public JsonArray(String json) {
        fromJson(json);
    }

    public JsonArray() {
        list = new ArrayList<>();
    }


    public JsonArray(List list) {
        this.list = list;
    }

    public <T> List<T> mapTo(Class<T> tClass) {
        return list.stream()
                .filter(Objects::nonNull)
                .map(o -> getObjectMapper().convertValue(o, tClass))
                .collect(Collectors.toList());
    }

    private void fromJson(String json) {
        list = Json.decodeValue(json, List.class);
    }

    public Stream<Object> stream() {
        return Json.asStream(iterator());
    }

    @Override
    public Iterator<Object> iterator() {
        return new Iter(list.iterator());
    }

    private class Iter implements Iterator<Object> {

        final Iterator<Object> listIter;

        Iter(Iterator<Object> listIter) {
            this.listIter = listIter;
        }

        @Override
        public boolean hasNext() {
            return listIter.hasNext();
        }

        @Override
        public Object next() {
            Object val = listIter.next();
            if (val instanceof Map) {
                val = new JsonObject((Map) val);
            } else if (val instanceof List) {
                val = new JsonArray((List) val);
            }
            return val;
        }

        @Override
        public void remove() {
            listIter.remove();
        }
    }

}
