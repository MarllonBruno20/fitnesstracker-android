package br.com.marllonbruno.fitnesstracker.android.model

import androidx.annotation.StringRes
import br.com.marllonbruno.fitnesstracker.android.R

enum class ActivityLevel(@StringRes override val displayNameRes: Int) : DisplayableEnum {
    SEDENTARY(R.string.enum_activity_sedentary),
    LIGHTLY_ACTIVE(R.string.enum_activity_lightly_active),
    MODERATELY_ACTIVE(R.string.enum_activity_moderately_active),
    ACTIVE(R.string.enum_activity_active),
    VERY_ACTIVE(R.string.enum_activity_very_active)
}