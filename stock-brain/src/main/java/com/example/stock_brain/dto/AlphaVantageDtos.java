package com.example.stock_brain.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class AlphaVantageDtos {

    public record GlobalQuoteResponse(
            @JsonProperty("Global Quote") GlobalQuote globalQuote
    ) {}

    public record GlobalQuote(
            @JsonProperty("01. symbol") String symbol,
            @JsonProperty("05. price") BigDecimal price,
            @JsonProperty("09. change") BigDecimal change,
            @JsonProperty("10. change percent") String changePercent
    ) {}


    public record CompanyOverviewResponse(
            @JsonProperty("Symbol") String symbol,
            @JsonProperty("Description") String description,
            @JsonProperty("PERatio") BigDecimal peRatio,
            @JsonProperty("DividendPerShare") BigDecimal dividendPerShare,
            @JsonProperty("DividendYield") BigDecimal dividendYield,
            @JsonProperty("EPS") BigDecimal eps
    ) {}
}