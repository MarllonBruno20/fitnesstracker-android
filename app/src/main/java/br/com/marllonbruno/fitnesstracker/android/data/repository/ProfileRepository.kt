package br.com.marllonbruno.fitnesstracker.android.data.repository

import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.ApiService
import br.com.marllonbruno.fitnesstracker.android.data.remote.ProfileDataResponse
import br.com.marllonbruno.fitnesstracker.android.data.remote.ProfileUpdateRequest
import kotlinx.coroutines.flow.first

class ProfileRepository(
    private val apiService: ApiService,
    private val preferencesRepository: UserPreferencesRepository
) {

    suspend fun updateProfile(request: ProfileUpdateRequest) : ProfileDataResponse? {
        // Pega o token do DataStore
        val token = preferencesRepository.jwtToken.first()
        if(token.isNullOrBlank()) return null

        try {
            val response = apiService.updateProfile("Bearer $token", request)

            return if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    suspend fun getProfile() : ProfileDataResponse? {
        // Pega o token do DataStore
        val token = preferencesRepository.jwtToken.first()
        if(token.isNullOrBlank()) return null

        try {
            val response = apiService.getProfile("Bearer $token")

            return if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

}