package br.com.marllonbruno.fitnesstracker.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.marllonbruno.fitnesstracker.android.model.ActivityLevel
import br.com.marllonbruno.fitnesstracker.android.model.DisplayableEnum
import br.com.marllonbruno.fitnesstracker.android.model.Gender
import br.com.marllonbruno.fitnesstracker.android.model.Objective
import br.com.marllonbruno.fitnesstracker.android.ui.components.DatePickerField
import br.com.marllonbruno.fitnesstracker.android.ui.theme.FitnessTrackerTheme
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.ProfileSetupEvent
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.ProfileSetupUiState
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.ProfileSetupViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    viewModel: ProfileSetupViewModel,
    onProfileUpdateSuccess: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.updatedProfile) {
        if (uiState.updatedProfile != null) {
            onProfileUpdateSuccess()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage != null) {
            snackbarHostState.showSnackbar(uiState.errorMessage!!)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text("Complete seu Perfil") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ProfileSetupScreenContent(
                uiState = uiState,
                onEvent = viewModel::onEvent
            )
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun ProfileSetupScreenContent(
    uiState: ProfileSetupUiState,
    onEvent: (ProfileSetupEvent) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- CAMPOS DO FORMULÁRIO ---
        item {
            OutlinedTextField(
                value = uiState.heightCm,
                onValueChange = { onEvent(ProfileSetupEvent.HeightChanged(it)) },
                label = { Text("Altura (cm)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),

                isError = uiState.heightCmError != null,
                supportingText = {
                    if(uiState.heightCmError != null) {
                        Text(
                            text = uiState.heightCmError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
        item {
            OutlinedTextField(
                value = uiState.currentWeightKg,
                onValueChange = { onEvent(ProfileSetupEvent.WeightChanged(it)) },
                label = { Text("Peso Atual (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),

                isError = uiState.currentWeightKgError != null,
                supportingText = {
                    if(uiState.currentWeightKgError != null) {
                        Text(
                            text = uiState.currentWeightKgError,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )
        }
        item {
            OutlinedTextField(
                value = uiState.goalWeightKg,
                onValueChange = { onEvent(ProfileSetupEvent.GoalWeightChanged(it)) },
                label = { Text("Peso Alvo (kg)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        item {
            // Aqui seria o local ideal para um DatePicker, mas um TextField serve para o exemplo
            DatePickerField(
                label = "Data de Nascimento",
                selectedDate = uiState.birthDate,
                onDateSelected = { newDate ->
                    onEvent(ProfileSetupEvent.BirthDateChanged(newDate))
                },
                isError = uiState.birthDateError != null,
                errorMessage = uiState.birthDateError,
                modifier = Modifier.fillMaxWidth()

            )
        }

        // --- SELETORES DE ENUM ---
        item {
            Text("Gênero", style = MaterialTheme.typography.bodyLarge)
            Row {
                Gender.entries.forEach { gender ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = uiState.gender == gender,
                            onClick = { onEvent(ProfileSetupEvent.GenderSelected(gender)) }
                        )
                        Text(text = stringResource(id = gender.displayNameRes),
                            modifier = Modifier.padding(start = 4.dp, end = 16.dp))
                    }
                }

                if (uiState.genderError != null) {
                    Text(
                        text = uiState.genderError,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }

            }
        }
        item {
            // Exemplo com Dropdown para ActivityLevel e Objective
            EnumDropdown(
                label = "Nível de Atividade",
                options = ActivityLevel.entries,
                selectedOption = uiState.activityLevel,
                isError = uiState.activityLevelError != null,
                onOptionSelected = { onEvent(ProfileSetupEvent.ActivityLevelSelected(it)) }
            )

            if (uiState.activityLevelError != null) {
                Text(
                    text = uiState.activityLevelError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
        item {
            EnumDropdown(
                label = "Meu Objetivo",
                options = Objective.entries,
                selectedOption = uiState.objective,
                isError = uiState.objectiveError != null,
                onOptionSelected = { onEvent(ProfileSetupEvent.ObjectiveSelected(it)) }
            )

            if (uiState.objectiveError != null) {
                Text(
                    text = uiState.objectiveError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }

        // --- BOTÃO DE AÇÃO ---
        item {
            Button(
                onClick = { onEvent(ProfileSetupEvent.SaveProfile) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Salvar e Calcular Metas")
            }
        }
    }
}

// Componente reutilizável para os Dropdowns
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EnumDropdown(
    label: String,
    options: List<T>,
    selectedOption: T?,
    isError: Boolean,
    onOptionSelected: (T) -> Unit
) where T : Enum<T>, T : DisplayableEnum {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOption?.let { stringResource(id = it.displayNameRes) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(stringResource(id = option.displayNameRes)) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 3. AS PREVIEWS
@Preview(name = "Estado Padrão", showBackground = true)
@Composable
fun ProfileSetupScreenContent_DefaultPreview() {
    FitnessTrackerTheme {
        Surface {
            ProfileSetupScreenContent(
                uiState = ProfileSetupUiState(
                    birthDate = LocalDate.of(1995, 5, 20)
                ),
                onEvent = {}
            )
        }
    }
}

@Preview(name = "Estado de Carregamento", showBackground = true)
@Composable
fun ProfileSetupScreenContent_LoadingPreview() {
    FitnessTrackerTheme {
        Surface {
            Box(contentAlignment = Alignment.Center) {
                ProfileSetupScreenContent(
                    uiState = ProfileSetupUiState(isLoading = true),
                    onEvent = {}
                )
                if (true) { // Simulando isLoading
                    CircularProgressIndicator()
                }
            }
        }
    }
}

