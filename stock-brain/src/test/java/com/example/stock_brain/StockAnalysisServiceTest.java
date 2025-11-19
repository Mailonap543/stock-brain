package com.example.stock_brain;

import com.example.stock_brain.client.AlphaVantageClient;
import com.example.stock_brain.client.BrapiClient;
import com.example.stock_brain.dto.AlphaVantageDtos.CompanyOverviewResponse;
import com.example.stock_brain.dto.AnalysisDtos.AnalysisResponse;
import com.example.stock_brain.dto.BrapiDtos.BrapiStock;
import com.example.stock_brain.service.StockAnalysisService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockAnalysisServiceTest {

    @Mock
    private AlphaVantageClient alphaVantageClient;

    @Mock
    private BrapiClient brapiClient;

    @InjectMocks
    private StockAnalysisService stockAnalysisService;

    @Test
    void shouldPrioritizeBrapiWhenItHasDividends() {
        String ticker = "PETR4.SA";
        BigDecimal preco = new BigDecimal("30.00");
        BigDecimal dividendo = new BigDecimal("3.00");

        BrapiStock brapiStock = new BrapiStock(ticker, "Petrobras", preco, null);
        when(brapiClient.getQuoteWithDividends(ticker)).thenReturn(brapiStock);
        when(brapiClient.calculateLastYearDividends(brapiStock)).thenReturn(dividendo);

        AnalysisResponse resultado = stockAnalysisService.analyzeStock(ticker);

        assertNotNull(resultado);
        assertEquals(dividendo, resultado.dividendPerShare());
        assertEquals(preco, resultado.currentPrice());
        assertEquals("COMPRAR", resultado.verdict());

        verify(brapiClient, times(1)).getQuoteWithDividends(ticker);
        verify(alphaVantageClient, never()).getCompanyOverview(anyString());
    }

    @Test
    void shouldFallbackToAlphaVantageWhenBrapiHasNoDividends() {
        String ticker = "TEST4.SA";
        BigDecimal precoAtual = new BigDecimal("10.00");

        BrapiStock brapiSemDividendo = new BrapiStock(ticker, "Empresa Teste", precoAtual, null);
        when(brapiClient.getQuoteWithDividends(ticker)).thenReturn(brapiSemDividendo);
        when(brapiClient.calculateLastYearDividends(brapiSemDividendo)).thenReturn(BigDecimal.ZERO);

        CompanyOverviewResponse alphaComDividendo = new CompanyOverviewResponse(
                ticker, "Desc", BigDecimal.TEN, new BigDecimal("1.00"), new BigDecimal("0.10"), BigDecimal.ONE
        );
        when(alphaVantageClient.getCompanyOverview(ticker)).thenReturn(alphaComDividendo);

        AnalysisResponse resultado = stockAnalysisService.analyzeStock(ticker);

        assertNotNull(resultado);
        assertEquals(new BigDecimal("1.00"), resultado.dividendPerShare());
        assertEquals("COMPRAR", resultado.verdict());

        verify(brapiClient, times(1)).getQuoteWithDividends(ticker);
        verify(alphaVantageClient, times(1)).getCompanyOverview(ticker);
    }

    @Test
    void shouldReturnNeutralIfBothFail() {
        String ticker = "RUIM3.SA";
        BigDecimal preco = new BigDecimal("50.00");

        BrapiStock brapiMock = new BrapiStock(ticker, "Ruim SA", preco, null);
        when(brapiClient.getQuoteWithDividends(ticker)).thenReturn(brapiMock);
        when(brapiClient.calculateLastYearDividends(brapiMock)).thenReturn(BigDecimal.ZERO);

        when(alphaVantageClient.getCompanyOverview(ticker)).thenReturn(new CompanyOverviewResponse(null, null, null, null, null, null));

        AnalysisResponse resultado = stockAnalysisService.analyzeStock(ticker);

        assertEquals("NEUTRO", resultado.verdict());
        assertEquals(BigDecimal.ZERO, resultado.dividendPerShare());
    }
    @Test
    void shouldRecoverFromBrapiExceptionAndUseAlphaVantage() {
        String ticker = "CRASH3.SA";


        when(brapiClient.getQuoteWithDividends(ticker)).thenThrow(new RuntimeException("Brapi fora do ar!"));


        CompanyOverviewResponse alphaResponse = new CompanyOverviewResponse(
                ticker, "Crash Test Dummy", BigDecimal.TEN, new BigDecimal("0.60"), new BigDecimal("0.06"), BigDecimal.ONE
        );
        when(alphaVantageClient.getStockPrice(ticker)).thenReturn(BigDecimal.TEN);
        when(alphaVantageClient.getCompanyOverview(ticker)).thenReturn(alphaResponse);


        AnalysisResponse resultado = stockAnalysisService.analyzeStock(ticker);


        assertNotNull(resultado);
        assertEquals("COMPRAR", resultado.verdict());
        assertEquals(new BigDecimal("0.60"), resultado.dividendPerShare());


        verify(alphaVantageClient, times(1)).getStockPrice(ticker);
    }
    @Test
    void shouldReturnErrorStatusWhenPriceIsZero() {
        String ticker = "MICO3.SA";


        BrapiStock stockZerado = new BrapiStock(ticker, "Mico SA", BigDecimal.ZERO, null);
        when(brapiClient.getQuoteWithDividends(ticker)).thenReturn(stockZerado);
        when(brapiClient.calculateLastYearDividends(stockZerado)).thenReturn(BigDecimal.ZERO);


        when(alphaVantageClient.getStockPrice(ticker)).thenReturn(BigDecimal.ZERO);


        AnalysisResponse resultado = stockAnalysisService.analyzeStock(ticker);


        assertEquals("ERRO", resultado.verdict());
        assertEquals("Ação não encontrada ou sem preço em nenhuma API.", resultado.message());
    }
}