package com.example.electricitybackend.commons.data.mapper.household;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.mapper.AbsMapper;
import com.example.electricitybackend.commons.data.mapper.consumption.ShortConsumptionMapper;
import com.example.electricitybackend.commons.data.request.HouseholdRequest;
import com.example.electricitybackend.commons.data.response.household.HouseholdResponse;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class HouseholdMapper extends AbsMapper<HouseholdRequest, HouseholdResponse, HouseholdEntity> {

    @Autowired
    protected ShortConsumptionMapper consumpitonMapper;

    @AfterMapping
    public void aftermapping(@MappingTarget  HouseholdEntity entity, HouseholdRequest request){
        String passwordHash = BCrypt.hashpw(request.getPassword(),BCrypt.gensalt());
        entity.setPassword(passwordHash);
    }

    @Override
//    @Mapping(target = "consumptions", expression = "java(consumpitonMapper.toResponses(entity.getConsumptions()))")
    public abstract HouseholdResponse toResponse(HouseholdEntity entity);
}
