package org.example.mealprepgenerator.controller;

import org.example.mealprepgenerator.dto.OpenAIResponse;
import org.example.mealprepgenerator.service.OpenAIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class OpenAIController {

    private final OpenAIService openAIService;

    public OpenAIController(OpenAIService openAIService) {
        this.openAIService = openAIService;
    }

    @GetMapping("/api/openai/test")
    public Mono<String> testOpenAI() {
        return openAIService.testConnection().map(openAIService::extractText);
    }
}