package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.AuthenticationRequest
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.repository.AuthRepository
import br.com.marllonbruno.fitnesstracker.android.data.repository.LoginResult
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
            val result = authRepository.login(request)
            if (result.success) {
                _loginUiState.update { it.copy(isLoading = false, loginResult = result) }
            } else {
                _loginUiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Falha ao fazer login."
                    )
                }
            }
        }
    }

    companion object {
        fun Factory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Pega a instância da Application e, através dela, o nosso contêiner
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val authRepository = application.container.authRepository

                // Cria o ViewModel com a dependência compartilhada
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
    val loginResult: LoginResult? = null,
    val errorMessage: String? = null
)