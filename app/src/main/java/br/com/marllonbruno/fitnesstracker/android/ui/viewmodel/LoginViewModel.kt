package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.AuthenticationRequest
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginUiState = MutableStateFlow(LoginUiState())
    val loginUiState: StateFlow<LoginUiState> = _loginUiState

    fun onEmailChanged(email: String) {
        _loginUiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _loginUiState.update { it.copy(password = password) }
    }

    fun loginUser() {
        viewModelScope.launch {
            _loginUiState.update { it.copy(isLoading = true, errorMessage = null) }
            val request = AuthenticationRequest(
                email = _loginUiState.value.email,
                password = _loginUiState.value.password
            )
            val success = authRepository.login(request)
            _loginUiState.update { it.copy(isLoading = false, isAuthenticated = success) }
            if (!success) {
                _loginUiState.update { it.copy(errorMessage = "Falha ao fazer login. Verifique seu email e senha.") }
            }
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val apiService = RetrofitClient.instance
                val prefsRepository = UserPreferencesRepository(application)
                val authRepository = AuthRepository(apiService, prefsRepository)
                LoginViewModel(authRepository)
            }
        }
    }

}

// Estado da UI para a tela de Login
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null
)