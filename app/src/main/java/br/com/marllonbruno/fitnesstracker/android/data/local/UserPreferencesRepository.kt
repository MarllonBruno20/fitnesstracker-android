package br.com.marllonbruno.fitnesstracker.android.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(private val context: Context) {

    private val jwtTokenKey = stringPreferencesKey("jwt_token")

    // Função para salvar o token. É uma 'suspend fun' pois o DataStore é assíncrono.
    suspend fun saveJwtToken(jwtToken: String) {
        context.dataStore.edit { preferences ->
            preferences[jwtTokenKey] = jwtToken
        }
    }

    // Propriedade para ler o token. Retorna um Flow, que é um fluxo de dados assíncrono.
    // Isso permite que a UI reaja automaticamente se o token mudar.
    val jwtToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[jwtTokenKey]
        }

    private val hasSeenOnboardingKey = booleanPreferencesKey("has_seen_onboarding")

    suspend fun setOnboardingSeen() {
        context.dataStore.edit { preferences ->
            preferences[hasSeenOnboardingKey] = true
        }
    }

    val hasSeenOnboarding: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[hasSeenOnboardingKey] ?: false
        }

}