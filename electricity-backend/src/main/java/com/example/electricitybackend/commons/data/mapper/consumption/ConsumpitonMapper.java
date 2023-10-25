package com.example.electricitybackend.commons.data.mapper.consumption;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import com.example.electricitybackend.commons.data.mapper.AbsMapper;
import com.example.electricitybackend.commons.data.request.ConsumptionRequest;
import com.example.electricitybackend.commons.data.response.consumption.ConsumptionResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class  ConsumpitonMapper extends AbsMapper<ConsumptionRequest, ConsumptionResponse, ConsumptionEntity> {
}
