package br.com.marllonbruno.fitnesstracker.android.data.remote

import br.com.marllonbruno.fitnesstracker.android.model.MealType
import br.com.marllonbruno.fitnesstracker.android.model.RecipeIngredientMeasurementUnit

data class RecipeSummaryResponse(
    val id: Long,
    val name: String,
    val image: String,
    val prepTimeMinutes: Int,
    val servings: Int,
    val totalCalories: Int
)

data class RecipeDetailsResponse(
    val authorName: String,

    val id: Long,
    val name: String,
    val description: String,
    val instructions: List<String>,
    val image: String,
    val prepTimeMinutes: Int,
    val servings: Int,

    val totalCalories: Int,
    val totalProtein: Int,
    val totalCarbohydrate: Int,
    val totalLipids: Int,

    val ingredients: List<IngredientResponse>
)

data class IngredientResponse(
    val name: String,
    val displayQuantity: Double,
    val displayUnit: RecipeIngredientMeasurementUnit
)

data class RecipeCreateRequest (
    val name: String,
    val description: String,
    val instructions: List<String>,
    val image: String,
    val prepTimeMinutes: Int,
    val servings: Int,
    val ingredients: List<IngredientRequest>,
    val mealTypes: List<MealType>
)

data class IngredientRequest (
    val ingredientId: Long,
    val ingredientName: String,
    val quantityInGrams: Double,
    val displayUnit: RecipeIngredientMeasurementUnit
)