package com.example.stock_brain.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;

public class BrapiDtos {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BrapiResponse(
            List<BrapiStock> results
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BrapiStock(
            String symbol,
            String longName,
            @JsonAlias("regularMarketPrice") BigDecimal currentPrice,
            @JsonAlias("dividendsData") BrapiDividendsWrapper dividends
    ) {}


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BrapiDividendsWrapper(
            List<BrapiDividend> cashDividends,
            List<BrapiDividend> stockDividends
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BrapiDividend(
            @JsonAlias("rate") BigDecimal rate,
            @JsonAlias("dateApproved") String dateApproved,
            @JsonAlias("paymentDate") String paymentDate
    ) {}
}