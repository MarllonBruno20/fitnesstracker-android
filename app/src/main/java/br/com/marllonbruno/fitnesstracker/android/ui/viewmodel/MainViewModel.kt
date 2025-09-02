package br.com.marllonbruno.fitnesstracker.android.ui.viewmodel
import br.com.marllonbruno.fitnesstracker.android.BuildConfig

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.marllonbruno.fitnesstracker.android.MyApplication
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainViewModel(private val prefsRepository: UserPreferencesRepository) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            // Se a build for de DEBUG, sempre mostre o onboarding para facilitar os testes.
            if (BuildConfig.DEBUG) {
                _startDestination.value = "onboarding"
            } else {
                // Se for uma build de RELEASE (produção), use a lógica normal.
                val hasSeenOnboarding = prefsRepository.hasSeenOnboarding.first()
                _startDestination.value = if (hasSeenOnboarding) "login" else "onboarding"
            }
        }
    }

    fun onOnboardingFinished() {
        viewModelScope.launch {
            prefsRepository.setOnboardingSeen()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication
                val prefsRepository = application.container.userPreferencesRepository

                MainViewModel(prefsRepository)
            }
        }
    }
}