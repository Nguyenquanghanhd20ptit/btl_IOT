package com.example.electricitybackend.commons.data.response.household;

import com.example.electricitybackend.commons.data.core.CustomLocalDateTimeSerializer;
import com.example.electricitybackend.commons.data.response.consumption.ConsumptionResponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Accessors(chain = true)
public class HouseholdResponse {
    private Integer id;
    private String householdName;
    private String address;
    private String phoneNumber;
    private String meterSerialNumber;
    private List<ConsumptionResponse> consumptions;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
}
