package br.com.marllonbruno.fitnesstracker.android.model

import androidx.annotation.StringRes
import br.com.marllonbruno.fitnesstracker.android.R

enum class Objective(@StringRes override val displayNameRes: Int) : DisplayableEnum {
    LOSE_WEIGHT(R.string.enum_objective_lose_weight),
    MAINTAIN_WEIGHT(R.string.enum_objective_maintain_weight),
    GAIN_MUSCLE(R.string.enum_objective_gain_muscle)
}