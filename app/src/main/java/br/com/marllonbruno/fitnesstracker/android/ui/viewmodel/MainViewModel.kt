package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val prefsRepository: UserPreferencesRepository) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            // Verifica no DataStore se o onboarding já foi visto
            val hasSeenOnboarding = prefsRepository.hasSeenOnboarding.first()
            // Define a rota inicial com base no resultado
            _startDestination.value = if (hasSeenOnboarding) "login" else "onboarding"
        }
    }

    fun onOnboardingFinished() {
        viewModelScope.launch {
            prefsRepository.setOnboardingSeen()
        }
    }

    companion object {
        fun Factory(application: Application): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val prefsRepository = UserPreferencesRepository(application)
                MainViewModel(prefsRepository)
            }
        }
    }
}