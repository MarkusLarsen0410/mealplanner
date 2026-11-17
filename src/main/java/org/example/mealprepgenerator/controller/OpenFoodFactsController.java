
package org.example.mealprepgenerator.controller;

import org.example.mealprepgenerator.dto.OpenFoodFactsProduct;
import org.example.mealprepgenerator.service.OpenFoodFactService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/open-food-facts")
public class OpenFoodFactsController {

    private final OpenFoodFactService openFoodFactService;

    public OpenFoodFactsController(OpenFoodFactService openFoodFactService) {
        this.openFoodFactService = openFoodFactService;
    }

    @GetMapping("/{ean}")
    public Mono<OpenFoodFactsProduct> getProduct(@PathVariable String ean) {
        return openFoodFactService.getProductByEan(ean);
    }
}
