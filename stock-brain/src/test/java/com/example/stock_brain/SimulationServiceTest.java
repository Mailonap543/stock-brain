package com.example.stock_brain;

import com.example.stock_brain.dto.SimulationDtos.SimulationRequest;
import com.example.stock_brain.dto.SimulationDtos.SimulationResponse;
import com.example.stock_brain.service.SimulationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

class SimulationServiceTest {

    private final SimulationService simulationService = new SimulationService();

    @Test
    @DisplayName("Deve calcular corretamente juros compostos simples sem aportes")
    void shouldCalculateSimpleCompoundInterestCorrectly() {

        SimulationRequest request = new SimulationRequest(
                new BigDecimal("1000.00"),
                BigDecimal.ZERO,
                1, // 1 ano
                new BigDecimal("10.00") // 10%
        );

        SimulationResponse response = simulationService.simulate(request);

        assertNotNull(response);

        assertEquals(new BigDecimal("1000.00"), response.totalInvested());


        assertTrue(response.finalAmount().compareTo(new BigDecimal("1000.00")) > 0);

        assertTrue(response.totalInterest().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Deve calcular corretamente com aportes mensais")
    void shouldCalculateWithMonthlyContributions() {

        SimulationRequest request = new SimulationRequest(
                BigDecimal.ZERO,
                new BigDecimal("100.00"),
                1,
                BigDecimal.ZERO // Sem juros para facilitar a conta de cabeça
        );

        SimulationResponse response = simulationService.simulate(request);


        assertEquals(new BigDecimal("1200.00"), response.totalInvested());
        assertEquals(new BigDecimal("1200.00"), response.finalAmount());

        assertTrue(response.totalInterest().abs().compareTo(new BigDecimal("0.01")) < 0);
    }

    @Test
    @DisplayName("Deve calcular renda passiva estimada (Regra dos 0.6%)")
    void shouldCalculatePassiveIncome() {
        SimulationRequest request = new SimulationRequest(
                new BigDecimal("10000.00"), // R$ 10 mil
                BigDecimal.ZERO,
                1,
                BigDecimal.ZERO
        );

        SimulationResponse response = simulationService.simulate(request);


        BigDecimal expectedIncome = new BigDecimal("60.00");

        assertEquals(expectedIncome.setScale(2, RoundingMode.HALF_UP), response.monthlyIncomeProjected());
    }
}