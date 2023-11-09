package com.example.electricitybackend.db.postgre.config;

import com.example.electricitybackend.commons.data.core.json.JsonArray;
import com.example.electricitybackend.commons.data.model.Filter;
import com.example.electricitybackend.commons.data.model.paging.OrderCustom;
import com.example.electricitybackend.commons.data.model.paging.PageableCustom;
import com.example.electricitybackend.commons.data.model.query.Operator;
import com.example.electricitybackend.commons.data.request.SearchRequest;
import com.google.common.base.CaseFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
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

    public <T> Specification<T> buildSearch(SearchRequest request, Class<T> tClass) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (request.getKeyword() == null) predicates.add(alwaysTruePredicate(criteriaBuilder));
            List<String> allColums = getAllColumnNames(root);
            List<String> searchColums = request.getSearchColumns() != null ?
                    request.getSearchColumns() : allColums;
            if (!StringUtils.isEmpty(request.getKeyword())) {
                searchColums.forEach(colum -> {
                    Path<String> path = root.get(colum);
                    if (path.getJavaType().equals(String.class)) {
                        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(path), convertStringPostgre(request.getKeyword().toLowerCase())));
                    }
                });
            }
            // buildFilter
            List<Predicate> predicatesFilters = new ArrayList<>();
            predicatesFilters.add(alwaysTruePredicate(criteriaBuilder));
            List<Filter> filters = request.getFilters();
            if (isEmpty(filters)) {
                return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            }
            Map<String, Field> fieldFroingerKeyMap = getFieldFroingerKey(tClass, root);
            Map<String, Path<?>> pathFroingerKeyMap = getRootFroingerKey(tClass, root);
            Collection<String> froingerKeyName = fieldFroingerKeyMap.keySet();
            filters.stream().forEach(filter -> {
                try {
                    if (allColums.contains(filter.getName())) {
                        Field field = tClass.getDeclaredField(filter.getName());
                        Object valueByClass = castValueByClass(filter.getOperation(), filter.getValue(), field.getType());
                        if (valueByClass != null) {
                            predicatesFilters.add(buildCondition(filter, criteriaBuilder, root.get(field.getName()), valueByClass, field.getType()));
                        }
                    } else if (froingerKeyName.contains(filter.getName())) {
                        Field fieldFroingerKey = fieldFroingerKeyMap.get(filter.getName());
                        Path path = pathFroingerKeyMap.get(filter.getName());
                        Object valueByClass = castValueByClass(filter.getOperation(), filter.getValue(), fieldFroingerKey.getType());
                        if (valueByClass != null) {
                            predicatesFilters.add(buildCondition(filter, criteriaBuilder, path, valueByClass, fieldFroingerKey.getType()));
                        }
                    }

                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            });
            query.where(criteriaBuilder.and(criteriaBuilder.or(predicates.toArray(new Predicate[0])),
                    criteriaBuilder.and(predicatesFilters.toArray(new Predicate[0]))));
            return query.getRestriction();
        };
    }

    private Predicate buildCondition(Filter filter,
                                     CriteriaBuilder criteriaBuilder,
                                     Path path, Object valueByClass, Class<?> type) {
        String operation = filter.getOperation();
        var operator = Operator.from(operation);
        String nameField = filter.getName();
        switch (operator) {
            case IN: {
                return path.in((Collection<?>) valueByClass);
            }
            case NIN: {
                Predicate predicate = path.in((Collection<?>) valueByClass);
                return criteriaBuilder.not(predicate);
            }
            case EQUAL: {
                return criteriaBuilder.equal(path, valueByClass);
            }
            case NOT_EQUAL: {
                return criteriaBuilder.notEqual(path, valueByClass);
            }
            case GREATER_THAN: {
                if (type.isAssignableFrom(LocalDateTime.class)) {
                    return criteriaBuilder.greaterThan(path, (LocalDateTime) valueByClass);
                }
                Number number = (Number) valueByClass;
                return criteriaBuilder.gt(path, number);
            }
            case LESS_THAN: {
                if (type.isAssignableFrom(LocalDateTime.class)) {
                    return criteriaBuilder.lessThan(path, (LocalDateTime) valueByClass);
                }
                Number number = (Number) valueByClass;
                return criteriaBuilder.lt(path, number);
            }
            case LESS_THAN_OR_EQUAL: {
                if (type.isAssignableFrom(LocalDateTime.class)) {
                    return criteriaBuilder.lessThanOrEqualTo(path, (LocalDateTime) valueByClass);
                }
                Number number = (Number) valueByClass;
                return criteriaBuilder.le(path, number);
            }
            case GREATER_THAN_OR_EQUAL: {
                if (type.isAssignableFrom(LocalDateTime.class)) {
                    return criteriaBuilder.greaterThanOrEqualTo(path, (LocalDateTime) valueByClass);
                }
                Number number = (Number) valueByClass;
                return criteriaBuilder.ge(path, number);
            }
            case LIKE: {
                if (path.getJavaType().equals(String.class)) {
                    return criteriaBuilder.like(path, convertStringPostgre(valueByClass.toString()));
                }
                return alwaysTruePredicate(criteriaBuilder);
            }
            case LIKE_IGNORE: {
                if (path.getJavaType().equals(String.class)) {
                    return criteriaBuilder.like(criteriaBuilder.lower(path), convertStringPostgre(nameField.toLowerCase()));
                }
                return alwaysTruePredicate(criteriaBuilder);
            }
            default:
                return criteriaBuilder.equal(path, valueByClass);
        }
    }


    public Pageable buildPageable(SearchRequest request, Class<?> tClass) {
        PageableCustom pageableCustom = request.getPageable();
        List<Sort.Order> orders = new ArrayList<>();
        if (pageableCustom != null) {
            List<OrderCustom> sorts = pageableCustom.getSort();
            List<String> allColums = Arrays.stream(tClass.getDeclaredFields())
                    .map(Field::getName)
                    .collect(Collectors.toList());
            Optional.ofNullable(sorts)
                    .orElse(new ArrayList<>())
                    .stream().forEach(orderCustom -> {
                        if (allColums.contains(orderCustom.getProperty())) {
                            String direction = orderCustom.getDirection();
                            if (direction.equals(OrderCustom.Direction.asc.toString())) {
                                orders.add(Sort.Order.asc(orderCustom.getProperty()));
                            } else if (direction.equals(OrderCustom.Direction.desc.toString())) {
                                orders.add(Sort.Order.desc(orderCustom.getProperty()));
                            }
                        }
                    });
            Sort sort = Sort.by(orders);
            Pageable pageable = PageRequest.of(request.getPageable().getPage() - 1,
                    request.getPageable().getPageSize(), sort);
            return pageable;
        }
        return PageRequest.of(PageableCustom.DEFAULT_PAGE - 1,
                PageableCustom.DEFAULT_PAGE_SIZE);
    }

    private Object castValueByClass(String operation, Object value, Class<?> classValue) {
        if (operation != null && Arrays.asList(IN.getOperator(), NIN.getOperator()).contains(operation)) {
            final JsonArray array = new JsonArray(value.toString());
            return array.stream()
                    .map(object -> castValueByClass(null, object, classValue))
                    .collect(Collectors.toList());
        }
        if (classValue.getSimpleName().equalsIgnoreCase(LocalDateTime.class.getSimpleName()))
            return longToLocalDateTime(createLong(value.toString()));
        if (classValue.getSimpleName().equalsIgnoreCase(Timestamp.class.getSimpleName()))
            return new Timestamp(createLong(value.toString()));
        if (classValue.isAssignableFrom(String.class)) return value;
        if (classValue.isAssignableFrom(Double.class)) return Double.valueOf(value.toString());
        if (classValue.isAssignableFrom(Long.class)) return Long.valueOf(value.toString());
        if (classValue.isAssignableFrom(Integer.class)) return Integer.valueOf(value.toString());
        return getObjectMapper().convertValue(value, classValue);
    }

    public List<String> getAllColumnNames(Root<?> root) {
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

    private Map<String, Field> getFieldFroingerKey(Class<?> clazz, Root<?> root) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Field> res = new HashMap<>();
        Arrays.stream(fields)
                .forEach(field -> {
                    if (field.isAnnotationPresent(ManyToOne.class)) {
                        String froingerKeyName = "";
                        String className = field.getType().getAnnotation(Table.class).name();
                        if (field.isAnnotationPresent(JoinColumn.class)) {
                            froingerKeyName = field.getAnnotation(JoinColumn.class).name();
                            froingerKeyName = froingerKeyName.equals("") ? className + "_id" : froingerKeyName;
                        } else {
                            froingerKeyName = className + "_id";
                        }
                        Field[] fieldFroingerkeys = field.getType().getDeclaredFields();
                        String finalFroingerKeyName = froingerKeyName;
                        for (Field fieldFroingerkey : fieldFroingerkeys) {
                            if (fieldFroingerkey.isAnnotationPresent(Id.class)) {
                                res.put(snakeToCamelCase(finalFroingerKeyName), fieldFroingerkey);
                                break;
                            }
                        }
                    }
                });
        return res;
    }

    private Map<String, Path<?>> getRootFroingerKey(Class<?> clazz, Root<?> root) {
        Field[] fields = clazz.getDeclaredFields();
        Map<String, Path<?>> res = new HashMap<>();
        Arrays.stream(fields)
                .forEach(field -> {
                    if (field.isAnnotationPresent(ManyToOne.class)) {
                        String froingerKeyName = "";
                        String className = field.getType().getAnnotation(Table.class).name();
                        if (field.isAnnotationPresent(JoinColumn.class)) {
                            froingerKeyName = field.getAnnotation(JoinColumn.class).name();
                            froingerKeyName = froingerKeyName.equals("") ? className + "_id" : froingerKeyName;
                        } else {
                            froingerKeyName = className + "_id";
                        }
                        Field[] fieldFroingerkeys = field.getType().getDeclaredFields();
                        String finalFroingerKeyName = froingerKeyName;
                        Path<?> froingerKeyPath = root.get(field.getName());
                        for (Field fieldFroingerkey : fieldFroingerkeys) {
                            if (fieldFroingerkey.isAnnotationPresent(Id.class)) {
                                res.put(snakeToCamelCase(finalFroingerKeyName), froingerKeyPath.get(fieldFroingerkey.getName()));
                                break;
                            }
                        }
                    }
                });
        return res;
    }

    private Predicate alwaysTruePredicate(CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isTrue(criteriaBuilder.literal(true));
    }

    private static String snakeToCamelCase(String input) {
        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, input);
    }

    private static String snakeCase(String input) {
        return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, input);
    }


}
