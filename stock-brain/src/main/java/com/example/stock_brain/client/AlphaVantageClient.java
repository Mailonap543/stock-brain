package com.example.stock_brain.client;

import com.example.stock_brain.dto.AlphaVantageDtos.CompanyOverviewResponse;
import com.example.stock_brain.dto.AlphaVantageDtos.GlobalQuoteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;

@Service
public class AlphaVantageClient {

    private final RestClient restClient;
    private final String apiKey;

    public AlphaVantageClient(RestClient.Builder builder,
                              @Value("${stock-brain.alpha-vantage.api-key}") String apiKey) {
        this.restClient = builder
                .baseUrl("https://www.alphavantage.co")
                .build();
        this.apiKey = apiKey;
    }

    public BigDecimal getStockPrice(String ticker) {
        String uri = UriComponentsBuilder.fromPath("/query")
                .queryParam("function", "GLOBAL_QUOTE")
                .queryParam("symbol", ticker)
                .queryParam("apikey", apiKey)
                .build().toUriString();

        var response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(GlobalQuoteResponse.class);

        if (response == null || response.globalQuote() == null) {
            throw new RuntimeException("Não foi possível buscar o preço da ação: " + ticker);
        }

        return response.globalQuote().price();
    }

    public CompanyOverviewResponse getCompanyOverview(String ticker) {
        String uri = UriComponentsBuilder.fromPath("/query")
                .queryParam("function", "OVERVIEW")
                .queryParam("symbol", ticker)
                .queryParam("apikey", apiKey)
                .build().toUriString();

        var response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(CompanyOverviewResponse.class);

        if (response == null || response.symbol() == null) {
            throw new RuntimeException("Não foi possível buscar os dados fundamentais da ação: " + ticker);
        }

        return response;
    }
}