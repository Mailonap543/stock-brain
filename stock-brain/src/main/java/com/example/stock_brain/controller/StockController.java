package com.example.stock_brain.controller;

import com.example.stock_brain.dto.AnalysisDtos.AnalysisResponse;
import com.example.stock_brain.service.StockAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockAnalysisService stockAnalysisService;

    @GetMapping("/analyze/{ticker}")
    public ResponseEntity<AnalysisResponse> analyzeStock(@PathVariable String ticker) {

        AnalysisResponse response = stockAnalysisService.analyzeStock(ticker);
        return ResponseEntity.ok(response);
    }
}