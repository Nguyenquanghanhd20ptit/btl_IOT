package com.example.electricitybackend.commons.data.model.paging;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OrderCustom {
    private String property;
    private String direction;

    public OrderCustom() {
    }

    public OrderCustom(String property, String direction) {
        this.property = property;
        this.direction = direction;
    }

    public enum Direction {
        asc, desc;
    }
}
