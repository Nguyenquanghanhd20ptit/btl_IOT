package com.example.electricitybackend.commons.data.response.consumption;

import com.example.electricitybackend.commons.data.core.CustomLocalDateTimeSerializer;
import com.example.electricitybackend.commons.data.response.household.ShortHouseholdResponse;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class BillResponse {
    private Integer id;
    private String meterSerialNumber;
    private Double currentReading;
    private Double previousReading;
    private Double totalConsumption;
    private Double electricityRate;
    private Double monthlyCost;
    private String imageUrl;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime electricityMonth;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
    private ShortHouseholdResponse household;
}
