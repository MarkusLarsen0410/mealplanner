
package org.example.mealprepgenerator.service;

import org.example.mealprepgenerator.dto.FoodWasteResponse;
import org.example.mealprepgenerator.dto.RecipeResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class RecipeService {

    private final SallingFoodWasteService sallingFoodWasteService;
    private final OpenAIService openAIService;
    private final tools.jackson.databind.ObjectMapper objectMapper;

    public RecipeService(
            SallingFoodWasteService sallingFoodWasteService,
            OpenAIService openAIService,
            tools.jackson.databind.ObjectMapper objectMapper) {

        this.sallingFoodWasteService = sallingFoodWasteService;
        this.openAIService = openAIService;
        this.objectMapper = objectMapper;
    }

    public Mono<RecipeResponse> generateRecipes(String zipCode) {

        return sallingFoodWasteService.getFoodWaste(zipCode)
                .flatMap(foodWaste -> {
                    String prompt = createPrompt(foodWaste);

                    return openAIService.generateResponse(prompt)
                            .map(openAIService::extractText)
                            .map(this::parseRecipeResponse);
                });
    }

    private String createPrompt(List<FoodWasteResponse> foodWaste) {

        StringBuilder products = new StringBuilder();

        foodWaste.stream()
                .flatMap(store -> store.clearances().stream()
                        .map(clearance -> new Object[]{store, clearance}))
                .limit(10)
                .forEach(item -> {

                    FoodWasteResponse store =
                            (FoodWasteResponse) item[0];

                    FoodWasteResponse.Clearance clearance =
                            (FoodWasteResponse.Clearance) item[1];

                    products.append("\nProdukt: ")
                            .append(clearance.product().description())
                            .append("\nPris: ")
                            .append(clearance.offer().newPrice())
                            .append(" kr.")
                            .append("\nButik: ")
                            .append(store.store().name())
                            .append("\nBy: ")
                            .append(store.store().address().city())
                            .append("\n");
                });

        return """
                                Du er en madlavningsassistent.
                
                                Lav 3 forskellige opskrifter ud fra produkterne nedenfor.
                                Brug så mange af tilbudsvarerne som muligt.
                                Du må gerne foreslå almindelige basisvarer som salt, peber og olie.
                
                                Returner KUN gyldig JSON i følgende format:
                
                                {
                                  "recipes": [
                                    {
                                      "name": "Opskriftens navn",
                                      "description": "Kort beskrivelse",
                                      "stores": ["føtex Rødovre"],
                                      "preparationTime": 30,
                                      "servings": 2,
                                      "ingredients": [
                                        {"name": "Nakkefilet", "amount": 300, "unit": "g"},
                                        {"name": "Pasta", "amount": 200, "unit": "g"}
                                      ],
                                      "instructions": ["Trin 1", "Trin 2"],
                                      "calories": 500,
                                      "protein": 30,
                                      "carbohydrates": 50,
                                      "fat": 20,
                                      "totalPrice": 45.0,
                                      "pricePerServing": 22.5
                                    }
                                  ]
                                }
                
                                Næringsindhold skal angives pr. portion.
                                Estimer kalorier, protein, kulhydrat og fedt ud fra ingredienserne og mængderne.
                                Vær realistisk med estimaterne.
                                Brug kun tal i næringsfelterne.
                
                                Tag højde for priserne på produkterne.
                                Prioritér produkter med gode tilbud.
                                Angiv den samlede estimerede pris for hver opskrift
                                og den estimerede pris pr. portion.
                
                                Angiv butikkerne, hvor tilbudsvarerne kan købes.
                                Brug kun butiksnavne fra produktlisten.
                                Hvis flere butikker leverer ingredienser,
                                skal alle relevante butikker angives i feltet "stores".
                
                                For hver ingrediens skal du angive:
                                - name: ingrediensens navn
                                - amount: den anvendte mængde
                                - unit: enheden (g, kg, ml, l eller stk.)
                
                                 Angiv altid konkrete mængder.
                                Brug gram eller milliliter, når det er muligt.
                
                VIGTIGT OM PRODUKTNAVNE:
                
                - Læs altid hele produktnavnet som én samlet vare.
                - Du må aldrig udlede en ingrediens ud fra et enkelt ord i produktnavnet.
                - Sammensatte produktnavne kan have en helt anden betydning end de enkelte ord.
                - Eksempel: "Kartoffelspegepølse" er en type spegepølse og må IKKE fortolkes som kartofler.
                - Eksempel: "Kyllingepålæg" er pålæg og ikke rå kylling.
                - Hvis du ikke med rimelig sikkerhed kan identificere produktet, skal du undlade at bruge det i opskriften.
                - Opfind aldrig en anden ingrediens ud fra produktets navn.
                
                                Tilgængelige produkter:
                """ + products;
    }

    private RecipeResponse parseRecipeResponse(String response) {

        try {
            return objectMapper.readValue(response, RecipeResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Kunne ikke læse opskrifter fra OpenAI", e
            );
        }
    }
}
