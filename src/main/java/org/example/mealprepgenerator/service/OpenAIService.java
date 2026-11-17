package org.example.mealprepgenerator.service;

import org.example.mealprepgenerator.dto.OpenAIResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class OpenAIService {

    private final WebClient webClient;

    public OpenAIService(
            WebClient.Builder webClientBuilder,
            @Value("${OPENAI_API_KEY}") String apiKey) {


        this.webClient = webClientBuilder
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public Mono<OpenAIResponse> generateResponse(String prompt) {
        return webClient.post()
                .uri("/responses")
                .bodyValue(Map.of(
                        "model", "gpt-5-mini",
                        "input", prompt
                ))
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.out.println("OpenAI HTTP-fejl: "
                                            + response.statusCode());
                                    System.out.println("Fejlbesked: " + errorBody);

                                    return Mono.error(
                                            new RuntimeException(errorBody));
                                })
                )
                .bodyToMono(OpenAIResponse.class);
    }

    public Mono<OpenAIResponse> testConnection() {
        return generateResponse("Svar kun med teksten: Forbindelsen virker!");
    }

    public String extractText(OpenAIResponse response) {
        return response.output().stream()
                .filter(output -> "message".equals(output.type()))
                .flatMap(output -> output.content().stream())
                .filter(content -> "output_text".equals(content.type()))
                .map(OpenAIResponse.Content::text)
                .findFirst()
                .orElse("Ingen tekst modtaget fra OpenAI");
    }

}