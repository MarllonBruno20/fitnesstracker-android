package br.com.marllonbruno.fitnesstracker.android.model

import androidx.annotation.StringRes
import br.com.marllonbruno.fitnesstracker.android.R

enum class RecipeIngredientMeasurementUnit(@StringRes override val displayNameRes: Int) : DisplayableEnum {

    GRAMS(R.string.enum_recipe_unit_grams),
    KILOGRAMS(R.string.enum_recipe_unit_kilograms),
    MILILITERS(R.string.enum_recipe_unit_mililiters),
    LITERS(R.string.enum_recipe_unit_liters),
    TEASPOON(R.string.enum_recipe_unit_teaspoon),
    TABLESPOON(R.string.enum_recipe_unit_tablespoon),
    CUP(R.string.enum_recipe_unit_cup),

    UNIT(R.string.enum_recipe_unit_unit),
    PINCH(R.string.enum_recipe_unit_pinch)

}