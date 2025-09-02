package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import br.com.marllonbruno.fitnesstracker.android.model.MealType

sealed class RecipeCreateEvent {

    data class NameChanged(val name: String) : RecipeCreateEvent()
    data class DescriptionChanged(val description: String) : RecipeCreateEvent()
    data class PrepTimeChanged(val prepTimeMinutes: String) : RecipeCreateEvent()
    data class ServingsChanged(val servings: String) : RecipeCreateEvent()
    data class ImageChanged(val image: String) : RecipeCreateEvent()
    data class MealTypeToggled(val mealType: MealType) : RecipeCreateEvent()
    data class InstructionAdded(val instruction: String) : RecipeCreateEvent()
    data class InstructionRemoved(val index: Int) : RecipeCreateEvent()
    data class IngredientAdded(val ingredient: IngredientInForm) : RecipeCreateEvent()
    data class IngredientRemoved(val index: Int) : RecipeCreateEvent()
    object SaveRecipe : RecipeCreateEvent()

}