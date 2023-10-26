package com.example.electricitybackend.db.postgre.repository;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsumptionRepository extends JpaRepository<ConsumptionEntity, Integer>, JpaSpecificationExecutor<ConsumptionEntity> {
    @Query("SELECT MAX(e.id) FROM ConsumptionEntity e where e.household.id = ?1")
    Optional<Integer> findMaxConsumptionId(Integer householdId);

}
