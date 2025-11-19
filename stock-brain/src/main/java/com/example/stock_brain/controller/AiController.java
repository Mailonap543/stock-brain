package com.example.stock_brain.controller;

import com.example.stock_brain.service.AiAdvisorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAdvisorService aiAdvisorService;

    @GetMapping("/advice/{ticker}")
    public ResponseEntity<Map<String, String>> getAdvice(@PathVariable String ticker) {
        String advice = aiAdvisorService.getInvestmentAdvice(ticker);

        return ResponseEntity.ok(Map.of("content", advice));
    }
}