package com.example.electricitybackend.rest;

import com.example.electricitybackend.commons.data.request.HouseholdRequest;
import com.example.electricitybackend.commons.data.request.SearchRequest;
import com.example.electricitybackend.commons.data.response.MessageResponse;
import com.example.electricitybackend.commons.data.response.household.HouseholdResponse;
import com.example.electricitybackend.service.household.HouseholdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/household")
public class HouseholdController {

    @Autowired
    private HouseholdService householdService;

    @Operation(summary = "thêm một hộ gia đình")
    @ApiResponse(responseCode = "200", description = "thêm một hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = HouseholdResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("")
    public ResponseEntity<?> addHousehold(@RequestBody HouseholdRequest request){
        return householdService.addHouseHold(request);
    }

    @Operation(summary = "update một hộ gia đình")
    @ApiResponse(responseCode = "200", description = "update một hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = HouseholdResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateHousehold(@PathVariable("id") Integer id,
                                             @RequestBody HouseholdRequest request){
        return householdService.updateHoseHold(id,request);
    }

    @Operation(summary = "lấy một hộ gia đình theo id")
    @ApiResponse(responseCode = "200", description ="lấy một hộ gia đình theo id",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = HouseholdResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @GetMapping("/{id}")
    public ResponseEntity<?> getHouseholdById(@PathVariable("id") Integer id){
        return householdService.getById(id);
    }

    @Operation(summary = "xóa một hộ gia đình")
    @ApiResponse(responseCode = "200", description = "xóa một hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MessageResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHouseholdById(@PathVariable("id") Integer id){
        return householdService.deleteById(id);
    }

    @Operation(summary = "tìm kiếm hộ gia đình")
    @ApiResponse(responseCode = "200", description = "tìm kiếm hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody SearchRequest request){
        return householdService.search(request);
    }

    @Operation(summary = "vẽ biểu đồ cho 1 hộ gia đình")
    @ApiResponse(responseCode = "200", description = "vẽ biểu đồ cho 1 hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("/chart/{id}")
    public ResponseEntity<?> drawChartById(@PathVariable("id") Integer id ,
                                       @RequestBody SearchRequest request){
        return householdService.drawChartById(id,request);
    }

    @Operation(summary = "vẽ biểu đồ cho tất cả các hộ gia đình")
    @ApiResponse(responseCode = "200", description = "vẽ biểu đồ cho tất cả các hộ gia đình",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = List.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("/chart")
    public ResponseEntity<?> drawChart(@RequestBody SearchRequest request){
        return householdService.drawChart(request);
    }
}
