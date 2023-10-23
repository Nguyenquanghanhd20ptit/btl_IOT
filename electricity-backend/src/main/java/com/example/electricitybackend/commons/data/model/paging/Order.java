package com.example.electricitybackend.commons.data.model.paging;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Order {
    private String property;
    private String direction;

    public Order() {
    }

    public Order(String property, String direction) {
        this.property = property;
        this.direction = direction;
    }

    public enum Direction {
        asc, desc;
    }
}
