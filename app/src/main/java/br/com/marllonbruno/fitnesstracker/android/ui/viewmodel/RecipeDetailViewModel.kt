package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.RecipeDetailsResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.repository.ProfileRepository
import br.com.marllonbruno.fitnesstracker.android.data.repository.RecipeRepository
import br.com.marllonbruno.fitnesstracker.android.ui.components.RecipeCard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipeDetailViewModel(private val recipeRepository: RecipeRepository, savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipeDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val recipeId: Long = checkNotNull(savedStateHandle["recipeId"])

    init {
        loadRecipeDetails()
    }

    fun loadRecipeDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val recipeDetails = recipeRepository.getRecipeDetails(recipeId)
                if (recipeDetails != null){
                    _uiState.update { it.copy(isLoading = false, recipe = recipeDetails) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Não foi possível carregar os detalhes da receita") }
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
                // 1. Acessa o contêiner de dependências
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val recipeRepository = application.container.recipeRepository

                // 2. O SavedStateHandle é pego automaticamente do contexto do initializer
                val savedStateHandle = this.createSavedStateHandle()

                // 3. Cria o ViewModel com as dependências corretas
                RecipeDetailViewModel(
                    recipeRepository = recipeRepository,
                    savedStateHandle = savedStateHandle
                )
            }
        }
    }
}

data class RecipeDetailUiState(
    val recipe: RecipeDetailsResponse? = null,
    val error: String? = null,
    val isLoading: Boolean = false
)