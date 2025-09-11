package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientDetailsResponse
import br.com.marllonbruno.fitnesstracker.android.data.repository.IngredientRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class SearchIngredientViewModel(
    private val ingredientRepository: IngredientRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchIngredientUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    fun onEvent(event: SearchIngredientEvent) {
        when (event) {
            is SearchIngredientEvent.QueryChanged -> onSearchQueryChanged(event.query)
            is SearchIngredientEvent.IngredientClicked -> {
                _uiState.update { it.copy(ingredientForQuantityPrompt = event.ingredient) }
            }
            is SearchIngredientEvent.QuantityChanged -> {
                _uiState.update { it.copy(quantityInput = event.quantity) }
            }
            is SearchIngredientEvent.DismissDialog -> {
                _uiState.update { it.copy(ingredientForQuantityPrompt = null, quantityInput = "") }
            }
            // O 'AddIngredientConfirmed' será tratado pela UI para retornar o resultado
            // pois o ViewModel não tem acesso direto ao NavController.
            SearchIngredientEvent.AddIngredientConfirmed -> { /* A UI irá lidar com isso */ }
        }
    }

    init {
        viewModelScope.launch {
            _searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect {
                    query -> searchIngredients(query)
                }
        }
    }

    /**
     * Esta função é chamada pela UI a cada letra digitada.
     * Ela apenas atualiza o _searchQuery.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        _uiState.update {
            it.copy(searchQuery = query) }
    }

    private suspend fun searchIngredients(query: String) {
        if(query.isBlank()){
            _uiState.update { it.copy(ingredients = emptyList(), isLoading = false) }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        try {
            val result = ingredientRepository.getIngredients(query)
            _uiState.update { it.copy(isLoading = false, ingredients = result ?: emptyList()) }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, error = "Falha ao buscar ingredientes.") }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // 1. Acessa o contêiner de dependências a partir da classe Application
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val ingredientRepository = application.container.ingredientRepository

                // 2. Usa o repositório do contêiner para criar o ViewModel
                SearchIngredientViewModel(ingredientRepository)
            }
        }
    }
}

data class SearchIngredientUiState(
    val searchQuery: String = "",
    val ingredients: List<IngredientDetailsResponse> = emptyList(),

    val ingredientForQuantityPrompt: IngredientDetailsResponse? = null,
    val quantityInput: String = "",

    val isLoading: Boolean = false,
    val error: String? = null
)