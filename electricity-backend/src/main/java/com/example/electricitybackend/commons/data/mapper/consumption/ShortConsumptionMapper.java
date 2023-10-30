package com.example.electricitybackend.commons.data.mapper.consumption;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import com.example.electricitybackend.commons.data.response.consumption.ShortConsumptionResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ShortConsumptionMapper {
    public abstract ShortConsumptionResponse toResponse(ConsumptionEntity entity);
    public abstract List<ShortConsumptionResponse> toResponses(List<ConsumptionEntity> entity);
}
