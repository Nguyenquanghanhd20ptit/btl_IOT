package com.example.electricitybackend.commons.data.response;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class PageResponse<T> {
    private Long total = 0L;
    private List<T> items;
}
