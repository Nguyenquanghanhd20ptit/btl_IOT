package com.example.electricitybackend.commons.data.request;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HouseholdRequest {
    private String householdName;
    private String address;
    private String phoneNumber;
}
