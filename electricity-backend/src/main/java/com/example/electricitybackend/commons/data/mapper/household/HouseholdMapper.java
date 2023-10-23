package com.example.electricitybackend.commons.data.mapper.household;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.mapper.AbsMapper;
import com.example.electricitybackend.commons.data.request.HouseholdRequest;
import com.example.electricitybackend.commons.data.response.household.HouseholdResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class HouseholdMapper extends AbsMapper<HouseholdRequest, HouseholdResponse, HouseholdEntity> {
}
