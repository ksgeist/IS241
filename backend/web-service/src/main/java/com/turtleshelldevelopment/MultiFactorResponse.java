package com.turtleshelldevelopment;

public record MultiFactorResponse(String secret, String qr_code, String qr_data) {}
