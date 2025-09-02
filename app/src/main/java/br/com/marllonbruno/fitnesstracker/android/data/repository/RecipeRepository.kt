package br.com.marllonbruno.fitnesstracker.android.data.repository

import android.util.Log
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.ApiService
import br.com.marllonbruno.fitnesstracker.android.data.remote.RecipeCreateRequest
import br.com.marllonbruno.fitnesstracker.android.data.remote.RecipeDetailsResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.RecipeSummaryResponse
import kotlinx.coroutines.flow.first

class RecipeRepository(
    private val apiService: ApiService
) {

    suspend fun createRecipe(recipe: RecipeCreateRequest): RecipeDetailsResponse? {
        try {
            val response = apiService.createRecipe(recipe)
            return response.body()
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Exceção na chamada createRecipe.", e)
            return null
        }
    }

    suspend fun getRecipes(): List<RecipeSummaryResponse>? {
        Log.d("RecipeRepository", "Tentando buscar a lista de receitas...")

        try {
            // A chamada de API agora é mais limpa graças ao interceptor
            val response = apiService.getRecipes()

            if (response.isSuccessful) {
                Log.d("RecipeRepository", "Receitas carregadas com sucesso. Status: ${response.code()}")
                return response.body()
            } else {
                // ✅ ESTE É O LOG MAIS IMPORTANTE
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string()
                Log.e("RecipeRepository", "Falha ao carregar receitas. Código: $errorCode")
                Log.e("RecipeRepository", "Corpo do erro: $errorBody")
                return null
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Exceção na chamada getRecipes.", e)
            return null
        }
    }

    suspend fun getRecipeDetails(recipeId: Long): RecipeDetailsResponse? {
        Log.d("RecipeRepository", "Tentando buscar detalhes para a receita ID: $recipeId")

        try {
            val response = apiService.getRecipeDetails(recipeId)

            if (response.isSuccessful) {
                Log.d("RecipeRepository", "Detalhes da receita $recipeId carregados com sucesso.")
                return response.body()
            } else {
                // ✅ ESTE É O LOG QUE VAI REVELAR O PROBLEMA
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string()
                Log.e("RecipeRepository", "Falha ao carregar detalhes da receita. Código: $errorCode")
                Log.e("RecipeRepository", "Corpo do erro: $errorBody")
                return null
            }
        } catch (e: Exception) {
            Log.e("RecipeRepository", "Exceção na chamada getRecipeDetails.", e)
            return null
        }
    }

}