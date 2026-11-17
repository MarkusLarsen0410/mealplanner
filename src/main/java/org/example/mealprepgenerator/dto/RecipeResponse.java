
package org.example.mealprepgenerator.dto;

import java.util.List;

public record RecipeResponse(
        List<Recipe> recipes
) {
    public record Recipe(
            String name,
            String description,
            int preparationTime,
            int servings,
            List<String> stores,
            List<Ingredient> ingredients,
            List<String> instructions,
            double calories,
            double protein,
            double carbohydrates,
            double fat,
            double totalPrice,
            double pricePerServing
    ) {}

    public record Ingredient(
            String name,
            double amount,
            String unit
    ) {}
}
