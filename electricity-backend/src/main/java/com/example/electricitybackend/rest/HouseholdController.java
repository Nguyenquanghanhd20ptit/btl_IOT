package com.example.electricitybackend.rest;

import com.example.electricitybackend.commons.data.request.HouseholdRequest;
import com.example.electricitybackend.commons.data.response.household.HouseholdResponse;
import com.example.electricitybackend.service.household.HouseholdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/household")
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    @Operation(summary = "thêm một hộ gia đình")
    @ApiResponse(responseCode = "200", description = "thêm một hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = HouseholdResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("/add")
    public ResponseEntity<?> addHousehold(@RequestBody HouseholdRequest request){
        return householdService.addHouseHold(request);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateHousehold(@PathVariable("id") Integer id,
                                             @RequestBody HouseholdRequest request){
        try {
            return ResponseEntity.ok(householdService.updateHoseHold(id,request));
        }catch (RuntimeException ex){
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
        }
    }
}
