package br.com.marllonbruno.fitnesstracker.android.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.marllonbruno.fitnesstracker.android.ui.screens.LoginScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.RegisterScreen
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.LoginViewModel
import androidx.compose.ui.platform.LocalContext
import br.com.marllonbruno.fitnesstracker.android.ui.screens.OnboardingScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.ProfileSetupScreen
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.MainViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.ProfileSetupViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RegisterViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(context.applicationContext as Application)
    )
    val startDestination by mainViewModel.startDestination.collectAsState()

    if (startDestination == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    NavHost(navController = navController, startDestination = startDestination!!) {

        composable("onboarding") {
            OnboardingScreen(
                onOnboardingFinished = {
                    mainViewModel.onOnboardingFinished()
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory(context.applicationContext as Application))
            val loginState by loginViewModel.loginUiState.collectAsState()

            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToProfileSetup = {
                    navController.navigate("profile_setup") {
                        // Limpa a tela de login da pilha para que o usuário não possa voltar
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToHome = {
                    navController.navigate("home") {
                        // Limpa a tela de login da pilha para que o usuário não possa voltar
                        popUpTo("login") { inclusive = true }
                    }
                                   },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            val context = LocalContext.current
            val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory(context.applicationContext as Application))
            val registerState by registerViewModel.registerUiState.collectAsState()

            RegisterScreen(
                viewModel = registerViewModel,
                onRegisterSuccess = {
                    navController.navigate("login")
                },
                onNavigateToLogin = { navController.navigate("login") }
            )
        }
        composable("profile_setup") {
            val context = LocalContext.current
            val profileSetupViewModel: ProfileSetupViewModel = viewModel(factory = ProfileSetupViewModel.Factory(context.applicationContext as Application))
            val profileSetupState by profileSetupViewModel.uiState.collectAsState()

            ProfileSetupScreen(
                viewModel = profileSetupViewModel,
                onProfileUpdateSuccess = { navController.navigate("home") }
            )
        }

        composable("home") {
            Text("Bem-vindo à Tela Principal!")
        }

    }
}