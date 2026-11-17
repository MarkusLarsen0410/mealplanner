package org.example.mealprepgenerator.controller;

import org.example.mealprepgenerator.dto.RecipeResponse;
import org.example.mealprepgenerator.service.RecipeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public Mono<RecipeResponse> generateRecipes(
            @RequestParam String zip) {

        return recipeService.generateRecipes(zip);
    }
}