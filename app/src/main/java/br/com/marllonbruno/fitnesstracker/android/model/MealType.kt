package br.com.marllonbruno.fitnesstracker.android.model

import androidx.annotation.StringRes
import br.com.marllonbruno.fitnesstracker.android.R

enum class MealType(@StringRes override val displayNameRes: Int) : DisplayableEnum {

    BREAKFAST(R.string.enum_meal_type_breakfast),
    LUNCH(R.string.enum_meal_type_lunch),
    DINNER(R.string.enum_meal_type_dinner),
    SNACK(R.string.enum_meal_type_snack)

}