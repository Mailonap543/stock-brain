package com.example.stock_brain.dto;

import java.math.BigDecimal;

public class AnalysisDtos {

    public record AnalysisResponse(
            String symbol,
            BigDecimal currentPrice,
            BigDecimal dividendPerShare,
            BigDecimal dividendYield,
            BigDecimal fairPriceBazin,
            String verdict,
            String message
    ) {}
}