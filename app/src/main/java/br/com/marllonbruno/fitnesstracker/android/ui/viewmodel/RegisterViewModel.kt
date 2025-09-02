package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.remote.UserRegisterRequest
import br.com.marllonbruno.fitnesstracker.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _registerUiState = MutableStateFlow(RegisterUiState())
    val registerUiState: StateFlow<RegisterUiState> = _registerUiState

    fun onNameChanged(name: String) {
        _registerUiState.update { it.copy(name = name) }
    }

    fun onEmailChanged(email: String) {
        _registerUiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _registerUiState.update { it.copy(password = password) }
    }

    fun registerUser() {
        viewModelScope.launch {
            _registerUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val request = UserRegisterRequest(
                name = _registerUiState.value.name,
                email = _registerUiState.value.email,
                password = _registerUiState.value.password
            )
            val success = authRepository.register(request)
            _registerUiState.update { it.copy(isLoading = false, isRegistered = success) }
            if (!success) {
                _registerUiState.update { it.copy(errorMessage = "Falha ao registrar. Tente novamente.") }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val authRepository = application.container.authRepository

                RegisterViewModel(authRepository)
            }
        }
    }
}

// Estado da UI para a tela de Cadastro
data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isRegistered: Boolean = false,
    val errorMessage: String? = null
)