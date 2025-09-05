package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientDetailsResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientRequest
import br.com.marllonbruno.fitnesstracker.android.data.remote.RecipeCreateRequest
import br.com.marllonbruno.fitnesstracker.android.data.repository.RecipeRepository
import br.com.marllonbruno.fitnesstracker.android.model.MealType
import br.com.marllonbruno.fitnesstracker.android.model.RecipeIngredientMeasurementUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize

class RecipeCreateViewModel(private val recipeRepository: RecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeCreateUiState())
    val uiState = _uiState.asStateFlow()

    fun onEvent(event: RecipeCreateEvent) {
        when(event) {
            is RecipeCreateEvent.NameChanged -> _uiState.update { it.copy(name = event.name) }
            is RecipeCreateEvent.DescriptionChanged -> _uiState.update { it.copy(description = event.description) }
            is RecipeCreateEvent.PrepTimeChanged -> _uiState.update { it.copy(prepTimeMinutes = event.prepTimeMinutes) }
            is RecipeCreateEvent.ServingsChanged -> _uiState.update { it.copy(servings = event.servings) }
            is RecipeCreateEvent.ImageChanged -> _uiState.update { it.copy(image = event.image) }

            is RecipeCreateEvent.MealTypeToggled -> {
                val currentMealTypes = _uiState.value.mealTypes.toMutableSet()
                if (currentMealTypes.contains(event.mealType)) {
                    currentMealTypes.remove(event.mealType)
                } else {
                    currentMealTypes.add(event.mealType)
                }
                _uiState.update { it.copy(mealTypes = currentMealTypes) }
            }

            is RecipeCreateEvent.InstructionAdded -> {
                val newInstructions = _uiState.value.instructions + event.instruction
                _uiState.update { it.copy(instructions = newInstructions) }
            }

            is RecipeCreateEvent.InstructionRemoved -> {
                val newInstructions = _uiState.value.instructions.toMutableList().also { it.removeAt(event.index) }
                _uiState.update { it.copy(instructions = newInstructions) }
            }

            is RecipeCreateEvent.IngredientAdded -> {
                val newIngredients = _uiState.value.ingredients + event.ingredient
                _uiState.update { it.copy(ingredients = newIngredients) }
            }

            is RecipeCreateEvent.IngredientRemoved -> {
                val newIngredients = _uiState.value.ingredients.toMutableList().also { it.removeAt(event.index) }
                _uiState.update { it.copy(ingredients = newIngredients) }
            }

            // Quando um ingrediente é selecionado na tela de busca
            is RecipeCreateEvent.IngredientSelected -> {
                _uiState.update { it.copy(ingredientForQuantityPrompt = event.ingredient) }
            }

            // Quando o usuário digita no campo de quantidade do diálogo
            is RecipeCreateEvent.QuantityChanged -> {
                _uiState.update { it.copy(quantityInput = event.quantity) }
            }

            // Quando o usuário cancela ou fecha o diálogo
            is RecipeCreateEvent.DismissAddIngredientDialog -> {
                _uiState.update { it.copy(ingredientForQuantityPrompt = null, quantityInput = "") }
            }

            // Quando o usuário confirma a adição do ingrediente no diálogo
            is RecipeCreateEvent.AddIngredientConfirmed -> {
                addIngredientToList()
            }

            RecipeCreateEvent.SaveRecipe -> submitData()
        }
    }

    private fun addIngredientToList(){

        val ingredientToAdd = _uiState.value.ingredientForQuantityPrompt
        val quantity = _uiState.value.quantityInput
        val quantityAsDouble = quantity.toDoubleOrNull()

        if(ingredientToAdd != null && quantityAsDouble != null && quantityAsDouble > 0){
            val newIngredientInForm = IngredientInForm(
                ingredientId = ingredientToAdd.id,
                ingredientName = ingredientToAdd.name,
                quantityInGrams = quantity,
                measurementUnit = RecipeIngredientMeasurementUnit.GRAMS
            )

            val newIngredients = _uiState.value.ingredients + newIngredientInForm

            _uiState.update { it.copy(
                ingredients = newIngredients,
                ingredientForQuantityPrompt = null,
                quantityInput = ""
            ) }
        } else {
            // Se a quantidade for inválida, apenas fecha o diálogo (ou pode-se adicionar um erro)
            _uiState.update { it.copy(ingredientForQuantityPrompt = null, quantityInput = "") }
        }

    }

    private fun validateInputs(): Boolean {

        val state = _uiState.value

        if(state.name.isBlank()){
            _uiState.update { it.copy(errorMessage = "O nome da receita é obrigatório") }
            return false
        }

        if(state.description.isBlank()){
            _uiState.update { it.copy(errorMessage = "A descrição da receita é obrigatória") }
            return false
        }

        if (state.prepTimeMinutes.isBlank() || state.prepTimeMinutes.toIntOrNull() == null || state.prepTimeMinutes.toInt() <= 0) {
            _uiState.update { it.copy(errorMessage = "O tempo de preparo deve ser um número positivo.") }
            return false
        }

        if (state.servings.isBlank() || state.servings.toIntOrNull() == null || state.servings.toInt() <= 0) {
            _uiState.update { it.copy(errorMessage = "O número de porções deve ser um número positivo.") }
            return false
        }

        if (state.ingredients.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "A receita precisa ter pelo menos um ingrediente.") }
            return false
        }

        if (state.instructions.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "A receita precisa ter pelo menos uma instrução.") }
            return false
        }

        if (state.mealTypes.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Selecione pelo menos um tipo de refeição.") }
            return false
        }

        _uiState.update { it.copy(errorMessage = null) }
        return true
    }

    private fun submitData() {

        if(!validateInputs()) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {

                val state = _uiState.value
                val requestDto = RecipeCreateRequest(
                    name = state.name,
                    description = state.description,
                    instructions = state.instructions,
                    image = state.image,
                    prepTimeMinutes = state.prepTimeMinutes.toIntOrNull() ?: 0,
                    servings = state.servings.toIntOrNull() ?: 0,
                    ingredients = state.ingredients.map { it.toIngredientRequest() },
                    mealType = state.mealTypes.toList()
                )

                val newRecipe = recipeRepository.createRecipe(requestDto)

                if (newRecipe != null){
                    _uiState.update { it.copy(isLoading = false, createdRecipeId = newRecipe.id) }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Falha ao salvar a receita. Tente novamente.") }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, errorMessage = "Erro de conexão. Verifique sua internet.") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val recipeRepository = application.container.recipeRepository

                RecipeCreateViewModel(recipeRepository)
            }
        }
    }
}

data class RecipeCreateUiState(
    val name: String = "",
    val description: String = "",
    val image: String = "",
    val prepTimeMinutes: String = "",
    val servings: String = "",

    val instructions: List<String> = emptyList(),
    val ingredients: List<IngredientInForm> = emptyList(),

    val mealTypes: Set<MealType> = emptySet(),

    val ingredientForQuantityPrompt: IngredientDetailsResponse? = null, // Guarda o ingrediente selecionado
    val quantityInput: String = "", // Guarda o texto digitado para a quantidade

    val isLoading: Boolean = false,
    val createdRecipeId: Long? = null, // Guarda o ID da receita criada para navegação
    val errorMessage: String? = null
)

@Parcelize
data class IngredientInForm(
    val ingredientId: Long = 0,
    val ingredientName: String = "",
    val quantityInGrams: String = "",
    val measurementUnit: RecipeIngredientMeasurementUnit
) : Parcelable

/**
 * Função de extensão que converte um objeto do formulário (UiState)
 * para o objeto de requisição da API (DTO).
 */
fun IngredientInForm.toIngredientRequest(): IngredientRequest {
    return IngredientRequest(
        ingredientId = this.ingredientId,
        ingredientName = this.ingredientName,
        quantityInGrams = this.quantityInGrams.toDoubleOrNull() ?: 0.0, // Converte a String para Double
        measurementUnit = this.measurementUnit
    )
}