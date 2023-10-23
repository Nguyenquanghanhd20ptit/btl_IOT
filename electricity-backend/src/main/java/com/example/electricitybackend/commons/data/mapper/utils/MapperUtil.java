package com.example.electricitybackend.commons.data.mapper.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static java.time.temporal.ChronoField.INSTANT_SECONDS;

public class MapperUtil {
    public static Long mapLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime == null ? null :
                localDateTime.atZone(ZoneId.systemDefault()).getLong(INSTANT_SECONDS) * 1000;
    }

    public static LocalDateTime longToLocalDateTime(Long localDate) {
        return longToLocalDateTimeOrNow(localDate);
    }
    public static LocalDateTime longToLocalDateTimeOrNow(Long time) {
        if (time == null) time = System.currentTimeMillis();
        return Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
