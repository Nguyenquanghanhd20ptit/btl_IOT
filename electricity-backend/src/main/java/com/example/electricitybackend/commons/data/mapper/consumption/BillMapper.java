package com.example.electricitybackend.commons.data.mapper.consumption;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import com.example.electricitybackend.commons.data.mapper.household.ShortHouseholdMapper;
import com.example.electricitybackend.commons.data.response.consumption.BillResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class BillMapper {
    @Autowired
    protected ShortHouseholdMapper householdMapper;

    @Mapping(target = "household", expression = "java(householdMapper.toResponse(entity.getHousehold()))")
    public abstract BillResponse toResponse(ConsumptionEntity entity);
}
