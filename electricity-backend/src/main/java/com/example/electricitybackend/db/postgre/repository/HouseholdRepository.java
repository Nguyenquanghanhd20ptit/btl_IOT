package com.example.electricitybackend.db.postgre.repository;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Repository
public interface HouseholdRepository extends JpaRepository<HouseholdEntity, Integer> , JpaSpecificationExecutor<HouseholdEntity> {

    @Transactional
    @Modifying
    @Query("update HouseholdEntity h set h.meterSerialNumber = ?2 WHERE h.id = ?1")
      Integer updateMeterSerialNumber(Integer id,String meterSerialNumber);

    @Query("select h from  HouseholdEntity  h where h.meterSerialNumber = ?1")
    Optional<HouseholdEntity> getHouseholdByMeterSerial(String meterSerial);

    @Query("select h from  HouseholdEntity  h where h.username = ?1")
    Optional<HouseholdEntity> getHouseholdByUsername(String username);
}
