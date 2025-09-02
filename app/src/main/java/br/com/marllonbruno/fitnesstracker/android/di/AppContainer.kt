package br.com.marllonbruno.fitnesstracker.android.di

import android.content.Context
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.ApiService
import br.com.marllonbruno.fitnesstracker.android.data.remote.RetrofitClient
import br.com.marllonbruno.fitnesstracker.android.data.repository.AuthRepository
import br.com.marllonbruno.fitnesstracker.android.data.repository.ProfileRepository
import br.com.marllonbruno.fitnesstracker.android.data.repository.RecipeRepository

/**
 * Contêiner de dependências manual para o aplicativo.
 * Ele cria e fornece instâncias únicas (Singletons) dos repositórios e serviços.
 */
interface AppContainer {
    val apiService: ApiService
    val userPreferencesRepository: UserPreferencesRepository
    val authRepository: AuthRepository
    val profileRepository: ProfileRepository
    val recipeRepository: RecipeRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    override val apiService: ApiService by lazy {
        RetrofitClient.create(context)
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    override val authRepository: AuthRepository by lazy {
        AuthRepository(apiService, userPreferencesRepository)
    }

    override val profileRepository: ProfileRepository by lazy {
        ProfileRepository(apiService, userPreferencesRepository)
    }

    override val recipeRepository: RecipeRepository by lazy {
        RecipeRepository(apiService)
    }
}

