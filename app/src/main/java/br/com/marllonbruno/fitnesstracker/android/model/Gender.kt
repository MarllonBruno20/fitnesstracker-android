package br.com.marllonbruno.fitnesstracker.android.model

import androidx.annotation.StringRes
import br.com.marllonbruno.fitnesstracker.android.R

enum class Gender(@StringRes override val displayNameRes: Int) : DisplayableEnum {
    MALE(R.string.enum_gender_male),
    FEMALE(R.string.enum_gender_female)
}