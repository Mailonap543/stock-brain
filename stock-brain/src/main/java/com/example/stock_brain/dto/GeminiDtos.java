package com.example.stock_brain.dto;

import java.util.List;

public class GeminiDtos {


    public record GeminiRequest(List<Content> contents) {}

    public record Content(List<Part> parts) {}

    public record Part(String text) {}


    public record GeminiResponse(List<Candidate> candidates) {}

    public record Candidate(Content content) {}
}