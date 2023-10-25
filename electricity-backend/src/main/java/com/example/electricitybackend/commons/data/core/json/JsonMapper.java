package com.example.electricitybackend.commons.data.core.json;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE;

@Configuration
public class JsonMapper {
    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper(){
        if(objectMapper == null) new JsonMapper().resetJsonConfig();
        return  objectMapper;
    }
    @Bean
    @Primary
    public  ObjectMapper objectMapper(){
        if(objectMapper == null) new JsonMapper().resetJsonConfig();
        return  objectMapper;
    }
    public void  resetJsonConfig(){
        objectMapper = new ObjectMapper();
        objectMapper.setPropertyNamingStrategy(SNAKE_CASE)
                .registerModule(new JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
