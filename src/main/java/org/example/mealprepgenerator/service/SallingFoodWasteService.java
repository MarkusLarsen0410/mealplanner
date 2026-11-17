
package org.example.mealprepgenerator.service;

import org.example.mealprepgenerator.dto.FoodWasteResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SallingFoodWasteService {

    private final WebClient webClient;


    public SallingFoodWasteService(
           //Bruges til at oprette vores Http-klient
            WebClient.Builder webClientBuilder,
            @Value("${salling.api.key}") String apiKey) {

        this.webClient = webClientBuilder
                .baseUrl("https://api.sallinggroup.com")//Angiver SallingGroups api-adresse
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    //Sender et GET-request med brugerens postnummer
    public Mono<List<FoodWasteResponse>> getFoodWaste(String zipCode) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/food-waste/")
                        .queryParam("zip", zipCode)
                        .build())
                .retrieve()
                .bodyToFlux(FoodWasteResponse.class)//Konverterer JSON-elementerne til DTO'en
                .collectList();//Samler resultaterne i en liste
    }
}
