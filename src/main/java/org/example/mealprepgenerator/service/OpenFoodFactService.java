
package org.example.mealprepgenerator.service;

import org.example.mealprepgenerator.dto.OpenFoodFactsProduct;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

@Service
public class OpenFoodFactService {

    private final WebClient webClient;

    public OpenFoodFactService() {
        this.webClient = WebClient.builder()
                .baseUrl("https://world.openfoodfacts.org")
                .defaultHeader(
                        "User-Agent",
                        "MealPrepGenerator/1.0 (contact: bachlarsen@live.com)"
                )
                .build();
    }

    public Mono<OpenFoodFactsProduct> getProductByEan(String ean) {

        return webClient.get()
                .uri("/api/v2/product/{ean}.json", ean)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(body -> new RuntimeException(
                                        "Open Food Facts fejl: " + response.statusCode() + " - " + body
                                ))
                )
                .bodyToMono(JsonNode.class)
                .map(response -> {

                    JsonNode product = response.path("product");

                    return new OpenFoodFactsProduct(
                            product.path("product_name").asString("Ukendt produkt"),
                            product.path("code").asString(ean),
                            product.path("quantity").asString("Ukendt størrelse")

                    );
                });
    }


}
