package com.example.stock_brain.service;

import com.example.stock_brain.dto.SimulationDtos.SimulationRequest;
import com.example.stock_brain.dto.SimulationDtos.SimulationResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SimulationService {

    public SimulationResponse simulate(SimulationRequest request) {

        double annualRateDecimal = request.yearlyInterestRate().doubleValue() / 100.0;
        double monthlyRate = Math.pow(1 + annualRateDecimal, 1.0 / 12.0) - 1;

        BigDecimal r = BigDecimal.valueOf(monthlyRate);
        BigDecimal currentBalance = request.initialAmount();
        BigDecimal pmt = request.monthlyContribution();
        int totalMonths = request.years() * 12;


        for (int i = 0; i < totalMonths; i++) {
            BigDecimal interest = currentBalance.multiply(r);
            currentBalance = currentBalance.add(interest).add(pmt);
        }

        BigDecimal finalAmount = currentBalance.setScale(2, RoundingMode.HALF_UP);


        BigDecimal totalInvested = request.initialAmount()
                .add(pmt.multiply(new BigDecimal(totalMonths)))
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalInterest = finalAmount.subtract(totalInvested)
                .setScale(2, RoundingMode.HALF_UP);


        BigDecimal monthlyIncome = finalAmount.multiply(new BigDecimal("0.006"))
                .setScale(2, RoundingMode.HALF_UP);

        return new SimulationResponse(totalInvested, totalInterest, finalAmount, monthlyIncome);
    }
}