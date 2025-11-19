package com.example.stock_brain.dto;

import java.math.BigDecimal;

public class SimulationDtos {

    public record SimulationRequest(
            BigDecimal initialAmount,
            BigDecimal monthlyContribution,
            int years,
            BigDecimal yearlyInterestRate
    ) {}

    public record SimulationResponse(
            BigDecimal totalInvested,
            BigDecimal totalInterest,
            BigDecimal finalAmount,
            BigDecimal monthlyIncomeProjected
    ) {}
}