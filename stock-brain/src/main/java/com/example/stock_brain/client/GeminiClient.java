package com.example.stock_brain.client;

import com.example.stock_brain.dto.GeminiDtos.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class GeminiClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String apiUrl;

    public GeminiClient(RestClient.Builder builder,
                        @Value("${stock-brain.gemini.api-key}") String apiKey,
                        @Value("${stock-brain.gemini.url}") String apiUrl) {
        this.restClient = builder.build();
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
    }

    public String getAnalysis(String prompt) {
        GeminiRequest request = new GeminiRequest(
                List.of(new Content(List.of(new Part(prompt))))
        );

        try {

            System.out.println("Chamando Gemini URL: " + apiUrl);

            GeminiResponse response = restClient.post()
                    .uri(apiUrl + "?key=" + apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response != null && !response.candidates().isEmpty()) {
                return response.candidates().get(0).content().parts().get(0).text();
            }
        } catch (Exception e) {
            System.err.println("ERRO CRÍTICO GEMINI: " + e.getMessage());
            return "Erro técnico na IA: " + e.getMessage();
        }

        return "Sem análise disponível (Resposta vazia).";
    }
}