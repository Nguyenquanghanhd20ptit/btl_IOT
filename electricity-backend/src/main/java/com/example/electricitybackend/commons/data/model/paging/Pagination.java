package com.example.electricitybackend.commons.data.model.paging;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.createLong;

@Data
@Accessors(chain = true)
public class Pagination {
    public static final Integer DEFAULT_LIMIT = 10;
    public static final Integer DEFAULT_OFFSET = 0;

    private List<Order> sort;
    private Integer offset;
    private Integer limit;
    private String maxId;
    private String minId;
    private Long total;

    public Pagination() {
        limit = DEFAULT_LIMIT;
        offset = 0;
    }


    public Long getMaxIdLong() {
        if (maxId == null) return 0L;
        return createLong(maxId);
    }
}
