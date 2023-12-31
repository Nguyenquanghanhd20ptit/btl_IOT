package com.example.electricitybackend.commons.data.request;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ConsumptionRequest {
    private String meterSerialNumber;
    private Double currentReading;
    private Double electricityRate;
    private String imageUrl;
    private Long electricityMonth;

}
