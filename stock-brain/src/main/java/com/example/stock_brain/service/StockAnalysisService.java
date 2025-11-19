package com.example.stock_brain.service;

import com.example.stock_brain.client.AlphaVantageClient;
import com.example.stock_brain.client.BrapiClient;
import com.example.stock_brain.dto.AlphaVantageDtos.CompanyOverviewResponse;
import com.example.stock_brain.dto.AnalysisDtos.AnalysisResponse;
import com.example.stock_brain.dto.BrapiDtos.BrapiStock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class StockAnalysisService {

    private final AlphaVantageClient alphaVantageClient;
    private final BrapiClient brapiClient;
    private static final BigDecimal MINIMUM_YIELD = new BigDecimal("0.06");

    public AnalysisResponse analyzeStock(String ticker) {

        if (ticker.toUpperCase().endsWith(".SA")) {
            return analyzeBrazilianStock(ticker);
        } else {
            return analyzeUsStock(ticker);
        }
    }

    private AnalysisResponse analyzeBrazilianStock(String ticker) {
        System.out.println("--- Iniciando Análise Brasil para " + ticker + " ---");


        BigDecimal currentPrice = BigDecimal.ZERO;
        BigDecimal dividendPerShare = BigDecimal.ZERO;

        try {
            BrapiStock stock = brapiClient.getQuoteWithDividends(ticker);
            if (stock != null) {
                currentPrice = stock.currentPrice() != null ? stock.currentPrice() : BigDecimal.ZERO;
                dividendPerShare = brapiClient.calculateLastYearDividends(stock);
                System.out.println("Brapi retornou Preço: " + currentPrice + " | Dividendos: " + dividendPerShare);
            }
        } catch (Exception e) {
            System.out.println("Erro ao consultar Brapi: " + e.getMessage());
        }


        if (currentPrice.compareTo(BigDecimal.ZERO) == 0 || dividendPerShare.compareTo(BigDecimal.ZERO) == 0) {
            System.out.println("Dados insuficientes na Brapi. Acionando Alpha Vantage para complementar...");
            try {

                if (currentPrice.compareTo(BigDecimal.ZERO) == 0) {
                    currentPrice = alphaVantageClient.getStockPrice(ticker);
                }


                if (dividendPerShare.compareTo(BigDecimal.ZERO) == 0) {
                    CompanyOverviewResponse overview = alphaVantageClient.getCompanyOverview(ticker);
                    if (overview.dividendPerShare() != null) {
                        dividendPerShare = overview.dividendPerShare();
                        System.out.println("Dividendos recuperados via Alpha Vantage: " + dividendPerShare);
                    }
                }
            } catch (Exception ex) {
                System.out.println("Alpha Vantage também não conseguiu salvar: " + ex.getMessage());
            }
        }

        return buildVerdict(ticker, currentPrice, dividendPerShare);
    }

    private AnalysisResponse analyzeUsStock(String ticker) {
        BigDecimal currentPrice = BigDecimal.ZERO;
        BigDecimal dividendPerShare = BigDecimal.ZERO;

        try {
            currentPrice = alphaVantageClient.getStockPrice(ticker);
            CompanyOverviewResponse overview = alphaVantageClient.getCompanyOverview(ticker);
            dividendPerShare = overview.dividendPerShare() != null ? overview.dividendPerShare() : BigDecimal.ZERO;
        } catch (Exception e) {
            System.out.println("Erro ao analisar ação EUA: " + e.getMessage());
        }

        return buildVerdict(ticker, currentPrice, dividendPerShare);
    }

    private AnalysisResponse buildVerdict(String ticker, BigDecimal currentPrice, BigDecimal dividendPerShare) {
        // Se depois de tudo o preço ainda for zero, é falha total
        if (currentPrice == null || currentPrice.compareTo(BigDecimal.ZERO) <= 0) {
            return new AnalysisResponse(
                    ticker.toUpperCase(),
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    "ERRO",
                    "Ação não encontrada ou sem preço em nenhuma API."
            );
        }

        BigDecimal dividendYield = BigDecimal.ZERO;
        if (currentPrice.compareTo(BigDecimal.ZERO) > 0) {
            dividendYield = dividendPerShare.divide(currentPrice, 4, RoundingMode.HALF_UP);
        }

        BigDecimal fairPriceBazin = BigDecimal.ZERO;
        if (dividendPerShare.compareTo(BigDecimal.ZERO) > 0) {
            fairPriceBazin = dividendPerShare.divide(MINIMUM_YIELD, 2, RoundingMode.HALF_UP);
        }

        String verdict;
        String message;

        if (fairPriceBazin.compareTo(BigDecimal.ZERO) == 0) {
            verdict = "NEUTRO";
            message = "Empresa não pagou dividendos suficientes nos últimos 12 meses ou dados indisponíveis nas APIs.";
        } else if (currentPrice.compareTo(fairPriceBazin) <= 0) {
            verdict = "COMPRAR";
            message = "O preço atual está abaixo do preço teto de Bazin (6%).";
        } else {
            verdict = "AGUARDAR";
            message = "O preço atual está acima do preço justo baseado em dividendos.";
        }

        return new AnalysisResponse(
                ticker.toUpperCase(),
                currentPrice,
                dividendPerShare,
                dividendYield.multiply(new BigDecimal("100")),
                fairPriceBazin,
                verdict,
                message
        );
    }
}