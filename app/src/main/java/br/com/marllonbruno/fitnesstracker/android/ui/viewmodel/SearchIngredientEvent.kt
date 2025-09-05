package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientDetailsResponse

sealed class SearchIngredientEvent {
    data class QueryChanged(val query: String) : SearchIngredientEvent()
    data class IngredientClicked(val ingredient: IngredientDetailsResponse) : SearchIngredientEvent()
    data class QuantityChanged(val quantity: String) : SearchIngredientEvent()
    object AddIngredientConfirmed : SearchIngredientEvent()
    object DismissDialog : SearchIngredientEvent()
}