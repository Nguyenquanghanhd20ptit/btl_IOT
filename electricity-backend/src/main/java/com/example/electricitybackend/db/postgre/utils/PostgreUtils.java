package com.example.electricitybackend.db.postgre.utils;

import com.example.electricitybackend.commons.data.request.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;
import java.util.ArrayList;
import java.util.List;

@Component
public class PostgreUtils {
    @Autowired
    private EntityManager entityManager;
    public  <T> Specification<T> buildSearch(SearchRequest request){
        return (root, query, criteriaBuilder) -> {
            List<String> searchColums = request.getSearchColumns();
            List<String> allColums = getAllColumnNames(root);
            if(searchColums == null)
            searchColums.forEach(colume -> {

            });
        }
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
}
