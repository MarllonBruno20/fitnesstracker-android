package br.com.marllonbruno.fitnesstracker.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat
import br.com.marllonbruno.fitnesstracker.android.model.RecipeIngredientMeasurementUnit
import br.com.marllonbruno.fitnesstracker.android.ui.theme.FitnessTrackerTheme
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.IngredientInForm
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeCreateEvent
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeCreateUiState
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeCreateViewModel

@Composable
fun RecipeCreateScreen(
    viewModel: RecipeCreateViewModel,
    onNavigateToSearchIngredient: () -> Unit,
    onRecipeCreated: (Long) -> Unit,
    onBackPress: () -> Unit
){

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.createdRecipeId) {
        uiState.createdRecipeId?.let { onRecipeCreated(it) }
    }

}

@Composable
fun CreateRecipeScreenContent(
    modifier: Modifier = Modifier,
    uiState: RecipeCreateUiState,
    onEvent: (RecipeCreateEvent) -> Unit,
    onNavigateToSearchIngredient: () -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Informações básicas", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = uiState.name,
                onValueChange = { onEvent(RecipeCreateEvent.NameChanged(it)) },
                label = { Text("Nome da receita") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { onEvent(RecipeCreateEvent.DescriptionChanged(it)) },
                label = { Text("Descrição") },
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = uiState.prepTimeMinutes,
                onValueChange = { onEvent(RecipeCreateEvent.PrepTimeChanged(it)) },
                label = { Text("Tempo de preparo (minutos)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = uiState.servings,
                onValueChange = { onEvent(RecipeCreateEvent.ServingsChanged(it)) },
                label = { Text("Quantidade de porções") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            OutlinedTextField(
                value = uiState.image,
                onValueChange = { onEvent(RecipeCreateEvent.ImageChanged(it)) },
                label = { Text("URL da imagem") },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        item {
            Text("Ingredientes", style = MaterialTheme.typography.titleLarge)
        }

        items(uiState.ingredients.size) { index ->
            val ingredient = uiState.ingredients[index]

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "• ${ingredient.quantityInGrams} ${ingredient.measurementUnit.name} de ${ingredient.ingredientName}",
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onEvent(RecipeCreateEvent.InstructionRemoved(index)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover ingrediente")
                }
            }
        }

        item{
            Button(onClick = onNavigateToSearchIngredient) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Adicionar ingrediente")
            }
        }

        item {
            Text("Instruções", style = MaterialTheme.typography.titleLarge)
        }

        items(uiState.instructions.size) { index ->
            val instruction = uiState.instructions[index]

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "• $instruction",
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onEvent(RecipeCreateEvent.InstructionRemoved(index)) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover instrução")
                }
            }
        }

        item {
            Button(
                onClick = { onEvent(RecipeCreateEvent.SaveRecipe) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
            ) {
                Text(if (uiState.isLoading) "Salvando..." else "Salvar Receita")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecipeCreateScreenContentPreview() {
    val previewState = RecipeCreateUiState(
        name = "Salada Refrescante de Atum com Feijão Branco",
        description = "Uma salada completa, rica em proteínas e fibras. É uma opção de refeição leve, prática e que não precisa ir ao fogo, perfeita para dias quentes ou para quem tem pouco tempo.",
        prepTimeMinutes = "30",
        servings = "2",
        ingredients = listOf(
            IngredientInForm(1L, "Peixe, água salgada, atum, conserva em óleo", "130", RecipeIngredientMeasurementUnit.GRAMS),
            IngredientInForm(2L, "Feijão, branco, cozido, s/ óleo, s/ sal", "150", RecipeIngredientMeasurementUnit.UNIT)
        ),
        instructions = listOf(
            "Em uma tigela grande, adicione 130g de atum drenado e 150g de feijão branco cozido.",
            "Pique 1 tomate médio e 1/4 de uma cebola roxa em cubos pequenos e adicione à tigela.",
            "Tempere a salada com 10g de azeite de oliva e uma pitada de sal a gosto.",
            "Misture delicadamente todos os ingredientes até que estejam bem incorporados.",
            "Sirva a salada fria. Pode ser acompanhada de folhas verdes."
        )
    )
    FitnessTrackerTheme {
        CreateRecipeScreenContent(
            uiState = previewState,
            onEvent = {},
            onNavigateToSearchIngredient = {}
        )
    }
}