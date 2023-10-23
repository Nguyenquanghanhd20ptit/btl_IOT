package com.example.electricitybackend.db.postgre.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.Predicate;

public class YourEntitySpecifications {
        public static Specification<?> buildSearchSpecifications(String keyword, List<String> searchColumns) {
            return (root, query, criteriaBuilder) -> {
                List<Predicate> predicates = new ArrayList<>();
                if (!StringUtils.isEmpty(keyword) && searchColumns != null && !searchColumns.isEmpty()) {
                    for (String column : searchColumns) {
                        Path<String> path = root.get(column);
                        if (path.getJavaType().equals(String.class)) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), "%" + keyword.toLowerCase() + "%"));
                        }
                    }
                }
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            };
        }


        public static Specification<?> buildSortAndPageSpecifications(String keyword, List<String> searchColumns, Pageable pageable) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(keyword) && searchColumns != null && !searchColumns.isEmpty()) {
                for (String column : searchColumns) {
                    Path<String> path = root.get(column);
                    if (path.getJavaType().equals(String.class)) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), "%" + keyword.toLowerCase() + "%"));
                    }
                }
            }
            query.where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));
            if (pageable.getSort().isSorted()) {
                List<Order> orders = new ArrayList<>();
                for (Sort.Order order : pageable.getSort()) {
                    String property = order.getProperty();
                    if (root.get(property) != null) {
                        if (order.isAscending()) {
                            orders.add(criteriaBuilder.asc(root.get(property)));
                        } else {
                            orders.add(criteriaBuilder.desc(root.get(property)));
                        }
                    }
                }
                query.orderBy(orders);
            }
            return query.getRestriction();
        };
    }
}
