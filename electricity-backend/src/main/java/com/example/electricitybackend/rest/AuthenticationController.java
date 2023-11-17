package com.example.electricitybackend.rest;

import com.example.electricitybackend.commons.data.request.LoginRequest;
import com.example.electricitybackend.commons.data.response.household.ShortHouseholdResponse;
import com.example.electricitybackend.service.authentication.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    @Operation(summary = "đăng nhập tài khoản")
    @ApiResponse(responseCode = "200", description =  "đăng nhập tài khoản",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ShortHouseholdResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("/login")
    public ResponseEntity<?> Login(@RequestBody LoginRequest request){
        return authenticationService.login(request,false);
    }
    @Operation(summary = "đăng nhập tài khoản admin")
    @ApiResponse(responseCode = "200", description =  "đăng nhập tài khoản admin",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ShortHouseholdResponse.class))})
    @ApiResponse(responseCode = "400", description = "bad-request", content = @Content)
    @PostMapping("/admin/login")
    public ResponseEntity<?> LoginAdmin( @RequestBody LoginRequest request){
        return authenticationService.login(request,true);
    }
}
