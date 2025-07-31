package br.com.marllonbruno.fitnesstracker.android.navigation

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import br.com.marllonbruno.fitnesstracker.android.ui.screens.LoginScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.RegisterScreen
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.LoginViewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RegisterViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "register") {
        composable("login") {
            val context = LocalContext.current
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory(context.applicationContext as Application))
            val loginState by loginViewModel.loginUiState.collectAsState()

            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    // Navegar para a tela principal aqui
                    println("Login Sucesso! Navegar para a tela principal")
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
        // Adicione outras rotas aqui no futuro
    }
}