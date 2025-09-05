package br.com.marllonbruno.fitnesstracker.android.data.repository

import br.com.marllonbruno.fitnesstracker.android.data.remote.ApiService
import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientDetailsResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientResponse

class IngredientRepository(
    private val apiService: ApiService
) {

    suspend fun getIngredients(query: String? = null): List<IngredientDetailsResponse>? {
        return apiService.getIngredients(query).body()
    }

}