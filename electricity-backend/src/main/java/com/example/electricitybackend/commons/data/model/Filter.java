package com.example.electricitybackend.commons.data.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Filter {
    private String name;
    private Object value;
    private String operation;
}
