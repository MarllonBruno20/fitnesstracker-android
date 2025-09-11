package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.ProfileDataResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.ProfileUpdateRequest
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.repository.AuthRepository
import br.com.marllonbruno.fitnesstracker.android.data.repository.ProfileRepository
import br.com.marllonbruno.fitnesstracker.android.model.ActivityLevel
import br.com.marllonbruno.fitnesstracker.android.model.Gender
import br.com.marllonbruno.fitnesstracker.android.model.Objective
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class ProfileSetupViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState

    fun onEvent(event: ProfileSetupEvent) {
        when (event) {
            is ProfileSetupEvent.BirthDateChanged -> _uiState.update { it.copy(birthDate = event.birthDate) }
            is ProfileSetupEvent.HeightChanged -> _uiState.update { it.copy(heightCm = event.height) }
            is ProfileSetupEvent.WeightChanged -> _uiState.update { it.copy(currentWeightKg = event.weight) }
            is ProfileSetupEvent.GoalWeightChanged -> _uiState.update { it.copy(goalWeightKg = event.goalWeight) }
            is ProfileSetupEvent.GenderSelected -> _uiState.update { it.copy(gender = event.gender) }
            is ProfileSetupEvent.ActivityLevelSelected -> _uiState.update { it.copy(activityLevel = event.level) }
            is ProfileSetupEvent.ObjectiveSelected -> _uiState.update { it.copy(objective = event.objective) }
            ProfileSetupEvent.SaveProfile -> saveProfileData()
        }
    }

    private fun validateInputs(): Map<String, String> {
        val currentState = _uiState.value
        val errors = mutableMapOf<String, String>()

        if (currentState.heightCm.isBlank()) errors["height"] = "Altura é obrigatória"
        if (currentState.currentWeightKg.isBlank()) errors["weight"] = "Peso é obrigatório"
        if (currentState.birthDate == null) errors["birthDate"] = "Data de Nascimento é obrigatória"
        if (currentState.gender == null) errors["gender"] = "Gênero é obrigatório"
        if (currentState.activityLevel == null) errors["activityLevel"] = "Nível de Atividade é obrigatório"
        if (currentState.objective == null) errors["objective"] = "Objetivo é obrigatório"

        return errors
    }

    private fun saveProfileData() {

        val validationErrors = validateInputs()

        // 2. Verificamos se há erros.
        if (validationErrors.isNotEmpty()) {
            // Se houver erros, fazemos UMA ÚNICA atualização de estado com todos eles.
            _uiState.update {
                it.copy(
                    heightCmError = validationErrors["height"],
                    currentWeightKgError = validationErrors["weight"],
                    birthDateError = validationErrors["birthDate"],
                    genderError = validationErrors["gender"],
                    activityLevelError = validationErrors["activityLevel"],
                    objectiveError = validationErrors["objective"]
                )
            }
            return // Para a execução aqui.
        }

            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                // Converte os valores de String para os tipos corretos, tratando possíveis erros
                val request = try {
                    ProfileUpdateRequest(
                        birthDate = _uiState.value.birthDate!!,
                        heightCm = _uiState.value.heightCm.toInt(),
                        currentWeightKg = _uiState.value.currentWeightKg.toDouble(),
                        goalWeightKg = _uiState.value.goalWeightKg.takeIf { it.isNotBlank() }
                            ?.toDouble(),
                        gender = _uiState.value.gender!!,
                        activityLevel = _uiState.value.activityLevel!!,
                        objective = _uiState.value.objective!!
                    )
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Por favor, preencha todos os campos obrigatórios corretamente."
                        )
                    }
                    return@launch
                }

                val result = profileRepository.updateProfile(request)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        updatedProfile = result,
                        errorMessage = if (result == null) "Falha ao salvar o perfil. Tente novamente." else null
                    )
                }
            }
    }

    companion object {
        fun Factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {

                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val profileRepository = application.container.profileRepository

                ProfileSetupViewModel(profileRepository)
            }
        }
    }

}

// O UiState agora usa Strings para os campos de texto para facilitar o controle no TextField
data class ProfileSetupUiState(
    // Campos de valor existentes
    val birthDate: LocalDate? = null,
    val heightCm: String = "",
    val currentWeightKg: String = "",
    val goalWeightKg: String = "",
    val gender: Gender? = null,
    val activityLevel: ActivityLevel? = null,
    val objective: Objective? = null,

    // Campos para erros
    val birthDateError: String? = null,
    val heightCmError: String? = null,
    val currentWeightKgError: String? = null,
    val genderError: String? = null,
    val activityLevelError: String? = null,
    val objectiveError: String? = null,

    // Estados de controle existentes
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val updatedProfile: ProfileDataResponse? = null
)