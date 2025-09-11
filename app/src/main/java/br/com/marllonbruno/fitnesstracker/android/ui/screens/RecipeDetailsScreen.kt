package br.com.marllonbruno.fitnesstracker.android.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.RestaurantMenu
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.ShortcutInfoCompat
import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.RecipeDetailsResponse
import br.com.marllonbruno.fitnesstracker.android.model.RecipeIngredientMeasurementUnit
import br.com.marllonbruno.fitnesstracker.android.ui.theme.FitnessTrackerTheme
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeDetailViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailsScreen(
    viewModel: RecipeDetailViewModel,
    onBackPress: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.recipe?.name ?: "Detalhes da Receita") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text(text = uiState.error!!)
                uiState.recipe != null -> RecipeDetailContent(recipe = uiState.recipe!!)
            }
        }
    }
}

// A tela "Burra" (Stateless) que contém a UI
@Composable
fun RecipeDetailContent(recipe: RecipeDetailsResponse) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        // 1. Imagem da Receita
        item {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(recipe.image).crossfade(true).build(),
                contentDescription = recipe.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 2. Cabeçalho (Nome, Autor, Descrição)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = recipe.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "Por: ${recipe.authorName}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = recipe.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // 3. Metadados (Tempo, Porções)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                RecipeMetadataItem(icon = Icons.Outlined.Schedule, label = "Tempo", value = "${recipe.prepTimeMinutes} min")
                RecipeMetadataItem(icon = Icons.Outlined.RestaurantMenu, label = "Porções", value = recipe.servings.toString())
            }
            HorizontalDivider(modifier = Modifier.padding(16.dp))
        }

        // 4. Informações Nutricionais
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Informações Nutricionais", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    NutrientChip(label = "Calorias", value = recipe.totalCalories.toString(), unit = "kcal")
                    NutrientChip(label = "Proteínas", value = recipe.totalProtein.toString(), unit = "g")
                    NutrientChip(label = "Carbs", value = recipe.totalCarbohydrate.toString(), unit = "g")
                    NutrientChip(label = "Gorduras", value = recipe.totalLipids.toString(), unit = "g")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(16.dp))
        }

        // 5. Lista de Ingredientes
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Ingredientes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                recipe.ingredients.forEach { ingredient ->
                    Text(text = "• ${ingredient.displayQuantity} ${stringResource(id = ingredient.displayUnit.displayNameRes)} de ${ingredient.name}")
                }
            }
            HorizontalDivider(modifier = Modifier.padding(16.dp))
        }

        // 6. Lista de Instruções
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Modo de Preparo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                recipe.instructions.forEachIndexed { index, instruction ->
                    Text(text = "${index + 1}. $instruction", modifier = Modifier.padding(bottom = 8.dp))
                }
            }
        }
    }
}

// Pequenos componentes reutilizáveis para a UI
@Composable
fun RecipeMetadataItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(24.dp))
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun NutrientChip(label: String, value: String, unit: String) {
    Card(shape = MaterialTheme.shapes.extraLarge) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            Text(text = unit, style = MaterialTheme.typography.labelSmall)
        }
    }
}


// --- Preview para facilitar o desenvolvimento ---
@Preview(showBackground = true)
@Composable
fun RecipeDetailContentPreview() {
    val fakeRecipe = RecipeDetailsResponse(
        id = 1L,
        authorName = "Chef Marllon",
        name = "Frango Grelhado com Arroz e Brócolis",
        description = "Uma refeição clássica, equilibrada e deliciosa...",
        instructions = listOf(
            "Tempere o filé de frango...",
            "Grelhe o filé de frango...",
            "Sirva com o arroz e o brócolis."
        ),
        image = "",
        prepTimeMinutes = 30,
        servings = 1,
        totalCalories = 550,
        totalProtein = 45,
        totalCarbohydrate = 35,
        totalLipids = 25,
        ingredients = listOf(
            IngredientResponse("Peito de Frango", 150.0, RecipeIngredientMeasurementUnit.GRAMS),
            IngredientResponse("Arroz Cozido", 150.0, RecipeIngredientMeasurementUnit.GRAMS),
            IngredientResponse("Brócolis", 100.0, RecipeIngredientMeasurementUnit.GRAMS)
        )
    )
    FitnessTrackerTheme {
            RecipeDetailContent(recipe = fakeRecipe)
    }
}