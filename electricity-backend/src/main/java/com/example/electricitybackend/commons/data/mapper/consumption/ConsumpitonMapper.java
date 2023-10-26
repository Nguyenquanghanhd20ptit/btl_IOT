package com.example.electricitybackend.commons.data.mapper.consumption;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import com.example.electricitybackend.commons.data.mapper.AbsMapper;
import com.example.electricitybackend.commons.data.mapper.household.HouseholdMapper;
import com.example.electricitybackend.commons.data.request.ConsumptionRequest;
import com.example.electricitybackend.commons.data.response.consumption.ConsumptionResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import static com.example.electricitybackend.commons.data.mapper.utils.MapperUtil.longToLocalDateTime;

@Mapper(componentModel = "spring")
public abstract class ConsumpitonMapper extends AbsMapper<ConsumptionRequest, ConsumptionResponse, ConsumptionEntity> {

    @Override
    @Mapping(target = "electricityMonth", ignore = true)
    public abstract ConsumptionEntity toEntity(ConsumptionRequest req);

    @Override
    @Mapping(target = "householdId", source = "household.id")
    public abstract ConsumptionResponse toResponse(ConsumptionEntity entity);


    public void toConsumptionEntity(ConsumptionEntity entity,
                                    ConsumptionEntity consumptionPre,
                                    ConsumptionRequest request) {
        if (consumptionPre == null) {
            entity.setElectricityMonth(longToLocalDateTime(request.getElectricityMonth()));
            entity.setPreviousReading(0.0);
            entity.setTotalConsumption(request.getCurrentReading());
            entity.setMonthlyCost(entity.getElectricityRate() * entity.getTotalConsumption());
        } else {
            entity.setElectricityMonth(longToLocalDateTime(request.getElectricityMonth()));
            entity.setPreviousReading(consumptionPre.getCurrentReading());
            entity.setTotalConsumption(request.getCurrentReading() - consumptionPre.getCurrentReading());
            entity.setMonthlyCost(entity.getElectricityRate() * entity.getTotalConsumption());
        }
    }
}
