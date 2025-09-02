package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.RecipeSummaryResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeListViewModel (private val recipeRepository: RecipeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeListUiState())
    val uiState = _uiState.asStateFlow() // Definindo que é uma interface apenas de leitura

    // O bloco init é executado quando o ViewModel é criado pela primeira vez
    init {
        loadRecipes()
    }

    fun loadRecipes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val recipeList = recipeRepository.getRecipes()
                if (recipeList != null){
                    _uiState.update { it.copy(isLoading = false, recipes = recipeList) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Não foi possível carregar as receitas") }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, error = "Erro de conexão. Verifique sua internet.") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val recipeRepository = application.container.recipeRepository

                RecipeListViewModel(recipeRepository)
            }
        }
    }
}

data class RecipeListUiState(
    val recipes: List<RecipeSummaryResponse>? = emptyList(),
    val error: String? = null,
    val isLoading: Boolean = false
)