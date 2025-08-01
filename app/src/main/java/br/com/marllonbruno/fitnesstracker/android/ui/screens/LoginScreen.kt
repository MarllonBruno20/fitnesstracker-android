package br.com.marllonbruno.fitnesstracker.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.marllonbruno.fitnesstracker.android.ui.theme.FitnessTrackerTheme
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.LoginUiState
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel(), onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit) {
    val state = viewModel.loginUiState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Login") }) }
    ) { paddingValues ->
        // Delega a UI para a função "burra"
        Box(modifier = Modifier.padding(paddingValues)) {
            LoginScreenContent(
                uiState = state,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onLoginClicked = viewModel::loginUser,
                onNavigateToRegister = onNavigateToRegister
            )
        }
    }
}

// Em LoginScreen.kt

@Composable
fun LoginScreenContent(
    uiState: LoginUiState, // Recebe o estado diretamente
    onEmailChanged: (String) -> Unit, // Recebe as ações como lambdas
    onPasswordChanged: (String) -> Unit,
    onLoginClicked: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text("Senha") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onLoginClicked,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.isLoading) "Entrando..." else "Entrar")
        }
        if (uiState.errorMessage != null) {
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
        }
        TextButton(onClick = onNavigateToRegister) {
            Text("Não tem uma conta? Cadastre-se")
        }
    }
}

@Preview(name = "Estado Padrão", showBackground = true)
@Composable
fun LoginScreenPreview_DefaultState() {
    FitnessTrackerTheme {
        LoginScreenContent(
            uiState = LoginUiState(), // Estado inicial
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClicked = {},
            onNavigateToRegister = {}
        )
    }
}

@Preview(name = "Estado de Carregamento", showBackground = true)
@Composable
fun LoginScreenPreview_LoadingState() {
    FitnessTrackerTheme {
        LoginScreenContent(
            uiState = LoginUiState(isLoading = true), // Simulando carregamento
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClicked = {},
            onNavigateToRegister = {}
        )
    }
}

@Preview(name = "Estado de Erro", showBackground = true)
@Composable
fun LoginScreenPreview_ErrorState() {
    FitnessTrackerTheme {
        LoginScreenContent(
            uiState = LoginUiState(errorMessage = "Email ou senha inválidos."), // Simulando erro
            onEmailChanged = {},
            onPasswordChanged = {},
            onLoginClicked = {},
            onNavigateToRegister = {}
        )
    }
}
