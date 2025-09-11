package br.com.marllonbruno.fitnesstracker.android.navigation

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import br.com.marllonbruno.fitnesstracker.android.ui.screens.LoginScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.RegisterScreen
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.LoginViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.navArgument
import br.com.marllonbruno.fitnesstracker.android.data.remote.IngredientDetailsResponse
import br.com.marllonbruno.fitnesstracker.android.ui.screens.OnboardingScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.ProfileSetupScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.RecipeCreateScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.RecipeDetailsScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.RecipeListScreen
import br.com.marllonbruno.fitnesstracker.android.ui.screens.SearchIngredientScreen
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.IngredientInForm
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.MainViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.ProfileSetupViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeCreateEvent
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeCreateViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeDetailViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RecipeListViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.RegisterViewModel
import br.com.marllonbruno.fitnesstracker.android.ui.viewmodel.SearchIngredientViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val mainViewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory
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
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory())
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
                    navController.navigate("recipe_list") {
                        // Limpa a tela de login da pilha para que o usuário não possa voltar
                        popUpTo("login") { inclusive = true }
                    }
                                   },
                onNavigateToRegister = { navController.navigate("register") }
            )
        }
        composable("register") {
            val context = LocalContext.current
            val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
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
            val profileSetupViewModel: ProfileSetupViewModel = viewModel(factory = ProfileSetupViewModel.Factory())
            val profileSetupState by profileSetupViewModel.uiState.collectAsState()

            ProfileSetupScreen(
                viewModel = profileSetupViewModel,
                onProfileUpdateSuccess = { navController.navigate("recipe_list") }
            )
        }

        composable("home") {
            Text("Bem-vindo à Tela Principal!")
        }

        composable ("recipe_list") {

            val context = LocalContext.current
            val recipeListViewModel: RecipeListViewModel = viewModel(factory = RecipeListViewModel.Factory)
            val recipeListState by recipeListViewModel.uiState.collectAsState()

            RecipeListScreen(
                viewModel = recipeListViewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate("recipe_detail/$recipeId")
                },
                onCreateClick = { navController.navigate("create_recipe") }
            )
        }

        composable(route = "recipe_detail/{recipeId}", // Rota com argumento
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })) {
            val recipeDetailViewModel: RecipeDetailViewModel = viewModel(factory = RecipeDetailViewModel.Factory) // Use sua factory aqui
            RecipeDetailsScreen(
                viewModel = recipeDetailViewModel,
                onBackPress = { navController.popBackStack() }
            )
        }

        composable("search_ingredient") {

            val searchIngredientViewModel: SearchIngredientViewModel = viewModel(factory = SearchIngredientViewModel.Factory)

            SearchIngredientScreen(
                viewModel = searchIngredientViewModel,
                onBackPress = { navController.popBackStack() },
                onIngredientConfirmed = { ingredientForm ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_ingredient_form", ingredientForm)
                    navController.popBackStack()
                }
            )
        }

        composable("create_recipe") { backStackEntry ->

            val recipeCreateViewModel: RecipeCreateViewModel = viewModel(factory = RecipeCreateViewModel.Factory)

            val searchResult by backStackEntry.savedStateHandle
                .getLiveData<IngredientInForm>("selected_ingredient_form")
                .observeAsState()

            LaunchedEffect(searchResult) {
                searchResult?.let { ingredientForm ->
                    // O resultado já vem completo!
                    recipeCreateViewModel.onEvent(RecipeCreateEvent.IngredientAdded(ingredientForm))
                    backStackEntry.savedStateHandle.remove<IngredientInForm>("selected_ingredient_form")
                }
            }

            RecipeCreateScreen(
              viewModel = recipeCreateViewModel,
                onNavigateToSearchIngredient = {
                    navController.navigate("search_ingredient")
                },
                onRecipeCreated = { newRecipeId ->
                    navController.navigate("recipe_detail/$newRecipeId") {
                        popUpTo("recipe_list")
                    }
                },
                onBackPress = { navController.popBackStack() }
            )

        }

    }
}