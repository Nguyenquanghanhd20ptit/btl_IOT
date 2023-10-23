package com.example.electricitybackend.db.postgre.repository;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HouseholdRepository extends JpaRepository<HouseholdEntity, Integer> {
}
