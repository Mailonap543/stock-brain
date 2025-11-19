package com.example.stock_brain.service;

import com.example.stock_brain.client.GeminiClient;
import com.example.stock_brain.dto.AnalysisDtos.AnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiAdvisorService {

    private final GeminiClient geminiClient;
    private final StockAnalysisService stockAnalysisService;

    public String getInvestmentAdvice(String ticker) {

        AnalysisResponse data = stockAnalysisService.analyzeStock(ticker);


        String prompt = String.format(
                "Atue como um analista financeiro Sênior conservador (estilo Décio Bazin e Graham). " +
                        "Analise a ação %s com base nestes dados que coletei: " +
                        "- Preço Atual: R$ %s " +
                        "- Dividend Yield (12m): %s%% " +
                        "- Preço Teto Bazin (6%%): R$ %s " +
                        "- Veredito do Algoritmo: %s. " +
                        "Responda em 3 parágrafos curtos: " +
                        "1) Análise dos fundamentos baseada no Yield. " +
                        "2) Riscos do setor (use seu conhecimento geral). " +
                        "3) Conclusão final se vale a pena para longo prazo. " +
                        "Use formatação Markdown.",
                data.symbol(), data.currentPrice(), data.dividendYield(), data.fairPriceBazin(), data.verdict()
        );

        // 3. Chama o Gemini
        return geminiClient.getAnalysis(prompt);
    }
}