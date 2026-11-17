
const searchForm = document.getElementById("search-form");
const zipCodeInput = document.getElementById("zip-code");

const recipesContainer = document.getElementById("recipes-container");
const loadingElement = document.getElementById("loading");
const errorMessage = document.getElementById("error-message");

searchForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const zipCode = zipCodeInput.value.trim();

    if (!/^\d{4}$/.test(zipCode)) {
        showError("Indtast venligst et gyldigt postnummer.");
        return;
    }

    await getRecipes(zipCode);
});

async function getRecipes(zipCode) {
    recipesContainer.innerHTML = "";
    errorMessage.hidden = true;
    loadingElement.hidden = false;

    try {
        const response = await fetch(
            `/api/recipes?zip=${encodeURIComponent(zipCode)}`
        );

        if (!response.ok) {
            throw new Error("Kunne ikke hente opskrifter.");
        }

        const data = await response.json();

        if (!data.recipes || data.recipes.length === 0) {
            showError("Vi fandt desværre ingen opskrifter.");
            return;
        }

        displayRecipes(data.recipes);

    } catch (error) {
        console.error("Fejl:", error);
        showError("Der opstod en fejl. Prøv igen senere.");
    } finally {
        loadingElement.hidden = true;
    }
}

function displayRecipes(recipes) {
    recipesContainer.innerHTML = "";

    recipes.forEach(recipe => {
        const card = document.createElement("article");
        card.className = "recipe-card";

        const content = document.createElement("div");
        content.className = "recipe-content";

        // Titel
        const title = document.createElement("h3");
        title.textContent = recipe.name;

        // Beskrivelse
        const description = document.createElement("p");
        description.className = "recipe-description";
        description.textContent = recipe.description;

        // Butikker
        const stores = document.createElement("p");
        stores.className = "recipe-stores";

        const storeNames = Array.isArray(recipe.stores)
            ? recipe.stores
            : [];

        stores.textContent = storeNames.length > 0
            ? "Butikker: " + storeNames.join(", ")
            : "Butikker: Ikke angivet";

        // Tilberedningstid og portioner
        const meta = document.createElement("div");
        meta.className = "recipe-meta";
        meta.innerHTML = `
            <span>⏱ ${recipe.preparationTime} min.</span>
            <span>🍽 ${recipe.servings} portioner</span>
        `;

        // Pris
        const price = document.createElement("div");
        price.className = "recipe-price";

        const pricePerServing = Number(recipe.pricePerServing);
        const totalPrice = Number(recipe.totalPrice);

        price.innerHTML = `
            <span class="price-label">Pris pr. portion</span>
            <strong>
                ${Number.isFinite(pricePerServing)
            ? pricePerServing.toFixed(2) + " kr."
            : "Ikke angivet"}
            </strong>
            <span class="total-price">
                Samlet pris:
                ${Number.isFinite(totalPrice)
            ? totalPrice.toFixed(2) + " kr."
            : "Ikke angivet"}
            </span>
        `;

        // Opskriftsdetaljer
        const details = document.createElement("details");
        details.className = "recipe-details";

        const summary = document.createElement("summary");
        summary.textContent = "Se opskrift";

        const detailsContent = document.createElement("div");
        detailsContent.className = "recipe-details-content";

        // Ingredienser
        const ingredientsTitle = document.createElement("h4");
        ingredientsTitle.textContent = "Ingredienser";

        const ingredientsList = document.createElement("ul");

        (recipe.ingredients || []).forEach(ingredient => {
            const item = document.createElement("li");

            if (typeof ingredient === "string") {
                item.textContent = ingredient;
            } else {
                item.textContent =
                    `${ingredient.name} – ${ingredient.amount} ${ingredient.unit}`;
            }

            ingredientsList.appendChild(item);
        });

        // Fremgangsmåde
        const instructionsTitle = document.createElement("h4");
        instructionsTitle.textContent = "Fremgangsmåde";

        const instructionsList = document.createElement("ol");

        (recipe.instructions || []).forEach(instruction => {
            const item = document.createElement("li");
            item.textContent = instruction;
            instructionsList.appendChild(item);
        });

        // Næringsindhold
        const nutrition = document.createElement("p");
        nutrition.className = "nutrition";

        nutrition.textContent =
            `Pr. portion (estimat): ${recipe.calories ?? "-"} kcal | ` +
            `${recipe.protein ?? "-"} g protein | ` +
            `${recipe.carbohydrates ?? "-"} g kulhydrat | ` +
            `${recipe.fat ?? "-"} g fedt`;

        // Saml opskriftsdetaljer
        detailsContent.append(
            ingredientsTitle,
            ingredientsList,
            instructionsTitle,
            instructionsList,
            nutrition
        );

        details.append(summary, detailsContent);

        // Saml opskriftskort
        content.append(
            title,
            description,
            stores,
            meta,
            price,
            details
        );

        card.appendChild(content);
        recipesContainer.appendChild(card);
    });
}

function showError(message) {
    errorMessage.textContent = message;
    errorMessage.hidden = false;
}
