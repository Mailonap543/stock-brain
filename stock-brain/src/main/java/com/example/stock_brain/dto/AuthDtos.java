package com.example.stock_brain.dto;

public record AuthDtos() {
    public record RegisterRequest(String username, String password) {}
    public record LoginRequest(String username, String password) {}
    public record AuthResponse(String message, String username) {}
}