package com.example.stock_brain.client;

import com.example.stock_brain.dto.BrapiDtos.BrapiResponse;
import com.example.stock_brain.dto.BrapiDtos.BrapiStock;
import com.example.stock_brain.dto.BrapiDtos.BrapiDividend;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BrapiClient {

    private final RestClient restClient;
    private final String token;

    public BrapiClient(RestClient.Builder builder, @Value("${stock-brain.brapi.token}") String token) {
        this.restClient = builder.baseUrl("https://brapi.dev").build();
        this.token = token;
    }

    public BrapiStock getQuoteWithDividends(String ticker) {
        String cleanTicker = ticker.toUpperCase().replace(".SA", "").trim();


        BrapiStock stock = fetchFromBrapi(cleanTicker, true);

        if (stock != null) {
            return stock;
        }


        System.out.println("Plano B ativado para " + cleanTicker + ": Buscando apenas cotação atual (range=1d)...");
        stock = fetchFromBrapi(cleanTicker, false);

        if (stock == null) {
            throw new RuntimeException("Não foi possível buscar a ação na Brapi: " + cleanTicker);
        }

        return stock;
    }

    private BrapiStock fetchFromBrapi(String ticker, boolean includeDividends) {
        try {

            String range = includeDividends ? "1y" : "1d";

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromPath("/api/quote/{ticker}")
                    .queryParam("token", token)
                    .queryParam("range", range)
                    .queryParam("interval", "1d");

            if (includeDividends) {
                uriBuilder.queryParam("dividends", "true");
            }

            String uri = uriBuilder.build(ticker).toString();

            BrapiResponse response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, resp) -> {
                        System.out.println("Brapi retornou erro " + resp.getStatusCode() + " para " + ticker);
                    })
                    .body(BrapiResponse.class);

            if (response == null || response.results() == null || response.results().isEmpty()) {
                return null;
            }

            return response.results().get(0);

        } catch (Exception e) {
            System.out.println("Aviso: Falha ao buscar na Brapi (" + ticker + "): " + e.getMessage());
            return null;
        }
    }

    public BigDecimal calculateLastYearDividends(BrapiStock stock) {
        if (stock == null || stock.dividends() == null || stock.dividends().cashDividends() == null) {
            return BigDecimal.ZERO;
        }

        List<BrapiDividend> dividendList = stock.dividends().cashDividends();
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        return dividendList.stream()
                .filter(d -> d.paymentDate() != null)
                .filter(d -> {
                    try {
                        String dateStr = d.paymentDate().length() >= 10 ? d.paymentDate().substring(0, 10) : d.paymentDate();
                        LocalDate payDate = LocalDate.parse(dateStr);
                        return payDate.isAfter(oneYearAgo);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .map(d -> d.rate() != null ? d.rate() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}