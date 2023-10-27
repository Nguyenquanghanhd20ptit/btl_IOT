package com.example.electricitybackend.commons.data.response.household;

import com.example.electricitybackend.commons.data.response.consumption.ShortConsumptionResponse;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ChartResponse {
    HouseholdResponse household;
    List<ShortConsumptionResponse> consumptions;
}
