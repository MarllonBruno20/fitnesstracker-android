package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientDetailsResponse
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
    data class IngredientSelected(val ingredient: IngredientDetailsResponse) : RecipeCreateEvent() // Quando um ingrediente é retornado da tela de busca
    data class QuantityChanged(val quantity: String) : RecipeCreateEvent() // Quando o usuário digita a quantidade
    object AddIngredientConfirmed : RecipeCreateEvent() // Quando o botão "Adicionar" do diálogo é clicado
    object DismissAddIngredientDialog : RecipeCreateEvent() // Quando o diálogo é fechado/cancelado
    object SaveRecipe : RecipeCreateEvent()

}