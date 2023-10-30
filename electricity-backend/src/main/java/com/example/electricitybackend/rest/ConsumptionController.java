package com.example.electricitybackend.rest;

import com.example.electricitybackend.commons.data.entity.ConsumptionEntity;
import com.example.electricitybackend.commons.data.request.ConsumptionRequest;
import com.example.electricitybackend.commons.data.response.MessageResponse;
import com.example.electricitybackend.commons.data.response.consumption.BillResponse;
import com.example.electricitybackend.commons.data.response.consumption.ConsumptionResponse;
import com.example.electricitybackend.service.consumption.ConsumptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/consumption")
public class ConsumptionController {
    @Autowired
    private ConsumptionService consumptionService;

    @Operation(summary = "thêm thông tin tiêu thụ cho 1 hộ gia đình")
    @ApiResponse(responseCode = "200", description = "thêm thông tin tiêu thụ cho 1 hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BillResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("")
    public ResponseEntity<?> addConsumption(@RequestBody ConsumptionRequest request){
        return consumptionService.addConsumption(request);
    }

    @Operation(summary = "xuất hóa đơn tháng vừa rồi cho hộ gia đình")
    @ApiResponse(responseCode = "200", description = "xuất hóa đơn tháng vừa rồi cho hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = BillResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @GetMapping("/bill/{id}")
    public ResponseEntity<?>extractBill(@PathVariable("id") Integer id){
        return consumptionService.extractBill(id);
    }
}
