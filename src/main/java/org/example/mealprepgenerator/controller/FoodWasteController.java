
package org.example.mealprepgenerator.controller;

import org.example.mealprepgenerator.dto.FoodWasteResponse;
import org.example.mealprepgenerator.service.SallingFoodWasteService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/food-waste")
public class FoodWasteController {

    private final SallingFoodWasteService sallingFoodWasteService;

    public FoodWasteController(SallingFoodWasteService sallingFoodWasteService) {
        this.sallingFoodWasteService = sallingFoodWasteService;
    }

    @GetMapping
    public Mono<List<FoodWasteResponse>> getFoodWaste(
            @RequestParam String zip) {

        return sallingFoodWasteService.getFoodWaste(zip);
    }
}
