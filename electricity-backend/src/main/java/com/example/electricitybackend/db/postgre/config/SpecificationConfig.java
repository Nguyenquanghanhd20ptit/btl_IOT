package com.example.electricitybackend.db.postgre.config;

import com.example.electricitybackend.commons.data.core.json.JsonArray;
import com.example.electricitybackend.commons.data.model.Filter;
import com.example.electricitybackend.commons.data.model.paging.OrderCustom;
import com.example.electricitybackend.commons.data.model.paging.PageableCustom;
import com.example.electricitybackend.commons.data.model.query.Operator;
import com.example.electricitybackend.commons.data.request.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.example.electricitybackend.commons.data.core.json.JsonMapper.getObjectMapper;
import static com.example.electricitybackend.commons.data.mapper.utils.MapperUtil.longToLocalDateTime;
import static com.example.electricitybackend.commons.data.model.query.Operator.IN;
import static com.example.electricitybackend.commons.data.model.query.Operator.NIN;
import static com.example.electricitybackend.commons.utils.StringUtil.convertStringPostgre;
import static org.apache.commons.lang3.math.NumberUtils.createLong;
import static org.springframework.util.CollectionUtils.isEmpty;

@Component
public class SpecificationConfig {
    @Autowired
    private EntityManager entityManager;
    public  <T> Specification<T> buildSearch(SearchRequest request,Class<T> tClass){
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if(request.getKeyword() == null) predicates.add(alwaysTruePredicate(criteriaBuilder));
            List<String> allColums = getAllColumnNames(root);
            List<String> searchColums = request.getSearchColumns() != null ?
                    request.getSearchColumns() : allColums;
            if(!StringUtils.isEmpty( request.getKeyword()) ){
                searchColums.forEach(colum -> {
                    Path<String> path = root.get(colum);
                        if (path.getJavaType().equals(String.class)) {
                            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), convertStringPostgre(request.getKeyword().toLowerCase())));
                        }
                });
            }
            // buildFilter
            AtomicReference<Predicate> predicateFilter = new AtomicReference<>(alwaysTruePredicate(criteriaBuilder));
            List<Filter> filters = request.getFilters();
            if(isEmpty(filters)) return  query.getRestriction();
            filters.stream().forEach(filter -> {
                try {
                    Field field = tClass.getDeclaredField(filter.getName());
                    if(field != null){
                        Object valueByClass = castValueByClass(filter.getOperation(),filter.getValue(),field.getType());
                        if(valueByClass != null){
                            Path<Object> path = root.get(filter.getName());
                            predicateFilter.set(buildCondition(filter, criteriaBuilder, root, valueByClass));
                        }
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            });
            query.where( criteriaBuilder.and( criteriaBuilder
                    .or(predicates.toArray(new Predicate[0])),predicateFilter.get()));
            return query.getRestriction();
        };
    }

    private Predicate buildCondition(Filter filter,
                                     CriteriaBuilder criteriaBuilder,
                                     Root root, Object valueByClass) {
        String operation = filter.getOperation();
        var operator = Operator.from(operation);
        String nameField = filter.getName();
        switch (operator){
            case IN:{
                Expression<?> expression = root.get(nameField);
                return expression.in((Collection<?>) valueByClass);
            }
            case NIN:{
                Expression<?> expression = root.get(nameField);
                Predicate predicate = expression.in((Collection<?>) valueByClass);
                return criteriaBuilder.not(predicate);
            }
            case EQUAL:{
                return criteriaBuilder.equal(root.get(nameField),valueByClass);
            }
            case NOT_EQUAL:{
                return criteriaBuilder.notEqual(root.get(nameField),valueByClass);
            }
            case GREATER_THAN:{
                Number number = (Number) valueByClass;
                return criteriaBuilder.gt(root.get(nameField),number);
            }
            case  LESS_THAN:{
                Number number = (Number) valueByClass;
                return criteriaBuilder.lt(root.get(nameField),number);
            }
            case LESS_THAN_OR_EQUAL:{
                Number number = (Number) valueByClass;
                return criteriaBuilder.le(root.get(nameField), number);
            }
            case GREATER_THAN_OR_EQUAL:{
                Number number = (Number) valueByClass;
                return criteriaBuilder.ge(root.get(nameField), number);
            }
            case LIKE: {
                Path<String> path = root.get(nameField);
                if(path.getJavaType().equals(String.class)) {
                    return criteriaBuilder.like(path, convertStringPostgre(valueByClass.toString()));
                }
                return alwaysTruePredicate(criteriaBuilder);
            }
            case LIKE_IGNORE: {
                Path<String> path = root.get(nameField);
                if(path.getJavaType().equals(String.class)){
                    return criteriaBuilder.like(criteriaBuilder.lower(path),convertStringPostgre(nameField.toLowerCase()));
                }
                return alwaysTruePredicate(criteriaBuilder);
            }
            default:
            return criteriaBuilder.equal(root.get(nameField),valueByClass);
        }
    }


    public Pageable  buildPageable(SearchRequest request,Class<?> tClass){
        PageableCustom pageableCustom = request.getPageable();
        List<Sort.Order> orders = new ArrayList<>();
        if(pageableCustom != null) {
            List<OrderCustom> sorts = pageableCustom.getSort();
            List<String> allColums = Arrays.stream(tClass.getDeclaredFields())
                            .map(Field::getName)
                            .collect(Collectors.toList());
            Optional.ofNullable(sorts)
                    .orElse(new ArrayList<>())
                    .stream().forEach(orderCustom -> {
                        if(allColums.contains(orderCustom.getProperty())){
                            String direction = orderCustom.getDirection();
                            if(direction.equals(OrderCustom.Direction.asc.toString())){
                                orders.add(Sort.Order.asc(orderCustom.getProperty()));
                            }else if(direction.equals(OrderCustom.Direction.desc.toString())){
                                orders.add(Sort.Order.desc(orderCustom.getProperty()));
                            }
                        }
                    });
            Sort sort = Sort.by(orders);
            Pageable pageable =  PageRequest.of(request.getPageable().getPage()-1,
                    request.getPageable().getPageSize(),sort);
            return pageable;
        }
        return PageRequest.of(PageableCustom.DEFAULT_PAGE-1,
                PageableCustom.DEFAULT_PAGE_SIZE);
    }

    private Object castValueByClass(String operation, Object value, Class<?> classValue) {
        if(operation != null && Arrays.asList(IN.getOperator(),NIN.getOperator()).contains(operation)){
            final JsonArray array = new JsonArray(value.toString());
            return array.stream()
                    .map(object -> castValueByClass(null, object, classValue))
                    .collect(Collectors.toList());
        }
        if (classValue.getSimpleName().equalsIgnoreCase(LocalDateTime.class.getSimpleName()))
            return longToLocalDateTime(createLong(value.toString()));
        if (classValue.getSimpleName().equalsIgnoreCase(Timestamp.class.getSimpleName()))
            return new Timestamp(createLong(value.toString()));
        if(classValue.isAssignableFrom(String.class)) return value;
        if(classValue.isAssignableFrom(Double.class))  return Double.valueOf(value.toString());
        if(classValue.isAssignableFrom(Long.class)) return Long.valueOf(value.toString());
        if(classValue.isAssignableFrom(Integer.class)) return Integer.valueOf(value.toString());
        return getObjectMapper().convertValue(value, classValue);
    }
    public  List<String> getAllColumnNames(Root<?> root) {
        List<String> columnNames = new ArrayList<>();

        Metamodel metamodel = entityManager.getMetamodel();

        if (root.getModel() != null) {
            EntityType<?> entityType = metamodel.entity(root.getModel().getJavaType());

            for (SingularAttribute<?, ?> attribute : entityType.getSingularAttributes()) {
                columnNames.add(attribute.getName());
            }
        }

        return columnNames;
    }

    public Predicate alwaysTruePredicate(CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }
}
