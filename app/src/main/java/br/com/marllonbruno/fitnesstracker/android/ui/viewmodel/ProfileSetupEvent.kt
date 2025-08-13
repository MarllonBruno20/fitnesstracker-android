package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import br.com.marllonbruno.fitnesstracker.android.model.ActivityLevel
import br.com.marllonbruno.fitnesstracker.android.model.Gender
import br.com.marllonbruno.fitnesstracker.android.model.Objective
import java.time.LocalDate

sealed class ProfileSetupEvent {
    data class BirthDateChanged(val birthDate: LocalDate) : ProfileSetupEvent()
    data class HeightChanged(val height: String) : ProfileSetupEvent()
    data class WeightChanged(val weight: String) : ProfileSetupEvent()
    data class GoalWeightChanged(val goalWeight: String) : ProfileSetupEvent()
    data class GenderSelected(val gender: Gender) : ProfileSetupEvent()
    data class ActivityLevelSelected(val level: ActivityLevel) : ProfileSetupEvent()
    data class ObjectiveSelected(val objective: Objective) : ProfileSetupEvent()
    object SaveProfile : ProfileSetupEvent()
}