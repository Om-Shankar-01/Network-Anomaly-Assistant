package com.example.networkanomalyassistant.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.networkanomalyassistant.common.ApiResponse;
import com.example.networkanomalyassistant.security.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for obtaining authentication tokens")
public class AuthController {
    
    private final JwtTokenProvider tokenProvider;

    public AuthController(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT Bearer Token")
    public ApiResponse<Map<String, String>> login(@RequestBody(required = false) LoginRequest request) {

        String username = (request != null && request.getUsername() != null) ? request.getUsername() : "admin";
        String token = tokenProvider.generateToken(username);

        Map<String, String> response = Map.of(
            "token", token,
            "tokenType", "Bearer"
        );

        return ApiResponse.ok(response, "Authentication Successful");
    }
}
