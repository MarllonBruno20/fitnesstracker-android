package br.com.marllonbruno.fitnesstracker.android.data.remote

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class IngredientDetailsResponse(
    val id: Long,
    val name: String,
    val ingredientMeasurementUnitOfMeasurement: String,
    val group: String,
    val caloriesPer100: Double,
    val availableCarbohydratePer100: Double,
    val proteinPer100: Double,
    val lipidsPer100: Double
) : Parcelable {
}
