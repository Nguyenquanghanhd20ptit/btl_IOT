package com.example.electricitybackend.commons.data.mapper.household;

import com.example.electricitybackend.commons.data.entity.HouseholdEntity;
import com.example.electricitybackend.commons.data.response.household.ShortHouseholdResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public  abstract class ShortHouseholdMapper {
    public abstract ShortHouseholdResponse toResponse(HouseholdEntity household);
}
