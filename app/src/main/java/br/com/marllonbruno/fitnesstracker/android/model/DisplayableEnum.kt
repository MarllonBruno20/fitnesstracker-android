package br.com.marllonbruno.fitnesstracker.android.model

import androidx.annotation.StringRes

interface DisplayableEnum {
    @get:StringRes
    val displayNameRes: Int
}