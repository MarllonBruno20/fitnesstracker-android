package br.com.marllonbruno.fitnesstracker.android.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.marllonbruno.fitnesstracker.android.R
import br.com.marllonbruno.fitnesstracker.android.ui.theme.FitnessTrackerTheme
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RegisterUiState
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(viewModel: RegisterViewModel = viewModel(), onRegisterSuccess: () -> Unit, onNavigateToLogin: () -> Unit) {
    val state = viewModel.registerUiState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(state.isRegistered) {
        if (state.isRegistered) {
            Toast.makeText(context, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
            onRegisterSuccess() // Ou navegue para a tela de login
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Cadastro") }) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            RegisterScreenContent(
                uiState = state,
                onNameChanged = viewModel::onNameChanged,
                onEmailChanged = viewModel::onEmailChanged,
                onPasswordChanged = viewModel::onPasswordChanged,
                onRegisterClicked = viewModel::registerUser,
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}
@Composable
fun RegisterScreenContent(
    uiState: RegisterUiState,
    onNameChanged: (String) -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRegisterClicked: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Text(
            text = stringResource(R.string.register_title),
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.name,
            onValueChange = onNameChanged,
            label = { Text(stringResource(R.string.register_name)) },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value =  uiState.email,
            onValueChange = onEmailChanged,
            label = { Text(stringResource(R.string.register_email)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            label = { Text(stringResource(R.string.register_password)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onRegisterClicked,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (uiState.isLoading) stringResource(R.string.register_button_loading) else stringResource(R.string.register_button_done))
        }
        if (uiState.errorMessage != null) {
            Text(text = uiState.errorMessage, color = MaterialTheme.colorScheme.error)
        }
        TextButton(onClick = onNavigateToLogin) {
            Text(stringResource(R.string.register_login))
        }
    }
}

@Preview(name = "Estado Padrão", showBackground = true)
@Composable
fun RegisterScreenPreview_DefaultState() {
    FitnessTrackerTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(), // Estado inicial
            onNameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onRegisterClicked = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(name = "Estado de Carregamento", showBackground = true)
@Composable
fun RegisterScreenPreview_LoadingState() {
    FitnessTrackerTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(isLoading = true),
            onNameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onRegisterClicked = {},
            onNavigateToLogin = {}
        )
    }
}

@Preview(name = "Estado de Erro", showBackground = true)
@Composable
fun RegisterScreenPreview_ErrorState() {
    FitnessTrackerTheme {
        RegisterScreenContent(
            uiState = RegisterUiState(errorMessage = "Digite um email válido."),
            onNameChanged = {},
            onEmailChanged = {},
            onPasswordChanged = {},
            onRegisterClicked = {},
            onNavigateToLogin = {}
        )
    }
}