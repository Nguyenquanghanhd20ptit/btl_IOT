package com.example.electricitybackend.db.postgre.repository;

import com.example.electricitybackend.commons.data.entity.HouseholdRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdRoleRepository extends JpaRepository<HouseholdRoleEntity, Integer> {
    @Query("select hr from HouseholdRoleEntity as hr where hr.household.id = ?1")
    HouseholdRoleEntity getByHouseholdId(Integer householdId);
}
