package com.example.electricitybackend.commons.data.mapper.household;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.mapper.AbsMapper;
import com.example.electricitybackend.commons.data.mapper.consumption.ShortConsumptionMapper;
import com.example.electricitybackend.commons.data.request.HouseholdRequest;
import com.example.electricitybackend.commons.data.response.household.HouseholdResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class HouseholdMapper extends AbsMapper<HouseholdRequest, HouseholdResponse, HouseholdEntity> {

    @Autowired
    protected ShortConsumptionMapper consumpitonMapper;

    @Override
//    @Mapping(target = "consumptions", expression = "java(consumpitonMapper.toResponses(entity.getConsumptions()))")
    public abstract HouseholdResponse toResponse(HouseholdEntity entity);
}
