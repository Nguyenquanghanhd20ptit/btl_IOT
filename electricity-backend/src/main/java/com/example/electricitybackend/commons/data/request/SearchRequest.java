package com.example.electricitybackend.commons.data.request;

import com.example.electricitybackend.commons.data.model.Filter;
import com.example.electricitybackend.commons.data.model.paging.Pageable;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class SearchRequest {
    private String keyword;
    private List<Filter> filters;
    private Pageable pageable;
    List<String> searchColumns;
}
