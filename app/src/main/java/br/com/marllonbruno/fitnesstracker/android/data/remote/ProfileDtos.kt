package br.com.marllonbruno.fitnesstracker.android.data.remote

import br.com.marllonbruno.fitnesstracker.android.model.ActivityLevel
import br.com.marllonbruno.fitnesstracker.android.model.Gender
import br.com.marllonbruno.fitnesstracker.android.model.Objective
import java.time.LocalDate

// DTO para a requisição de atualização de perfil
data class ProfileUpdateRequest(
    val birthDate: LocalDate,
    val heightCm: Int,
    val currentWeightKg: Double,
    val goalWeightKg: Double?,
    val gender: Gender,
    val activityLevel: ActivityLevel,
    val objective: Objective
)

// DTO para a requisição de perfil
data class ProfileDataResponse(
    val name: String,
    val birthDate: LocalDate,
    val heightCm: Int,
    val currentWeightKg: Double,
    val goalWeightKg: Double?,
    val gender: Gender,
    val activityLevel: ActivityLevel,
    val objective: Objective,
    val dailyCaloriesGoal: Int,
    val dailyProteinGoal: Int,
    val dailyCarbsGoal: Int,
    val dailyFatGoal: Int,
    val imc: Double,
    val tmb: Double
)