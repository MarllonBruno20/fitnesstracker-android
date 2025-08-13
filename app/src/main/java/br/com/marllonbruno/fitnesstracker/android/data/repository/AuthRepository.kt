package br.com.marllonbruno.fitnesstracker.android.data.repository

import android.util.Log
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import br.com.marllonbruno.fitnesstracker.android.data.remote.ApiService
import br.com.marllonbruno.fitnesstracker.android.data.remote.AuthenticationRequest
import br.com.marllonbruno.fitnesstracker.android.data.remote.UserRegisterRequest

class AuthRepository(
    private val apiService: ApiService,
    private val preferencesRepository: UserPreferencesRepository
) {

    // Função para fazer o login
    suspend fun login(request: AuthenticationRequest): LoginResult {
        try {
            val response = apiService.login(request)
            if(response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                // Se o login foi bem-sucedido, salve o token no DataStore
                preferencesRepository.saveJwtToken(authResponse.token)
                preferencesRepository.saveProfileStatus(authResponse.isProfileComplete)
                return LoginResult(success = true, isProfileComplete = authResponse.isProfileComplete)
            }
            return LoginResult(success = false, isProfileComplete = false)
        } catch (e: Exception) {
            // Em caso de erro de rede ou outro, retorna falha
            e.printStackTrace()
            return LoginResult(success = false, isProfileComplete = false)
        }
    }

    // Função para registrar um novo usuário
    suspend fun register(request: UserRegisterRequest): Boolean {
        // Log para sabermos que a função foi chamada e com quais dados
        Log.d("AuthRepository", "Tentando registrar com: $request")

        try {
            val response = apiService.register(request)

            if (response.isSuccessful && response.body() != null) {
                // Sucesso!
                Log.d("AuthRepository", "Registro bem-sucedido. Token recebido.")
                val token = response.body()!!.token
                preferencesRepository.saveJwtToken(token)
                return true
            } else {
                // Falha! O backend retornou um erro HTTP.
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string() // Lê o corpo do erro
                Log.e("AuthRepository", "Falha no registro. Código: $errorCode, Corpo do Erro: $errorBody")
                return false
            }
        } catch (e: Exception) {
            // Falha! Ocorreu uma exceção de rede ou outra.
            Log.e("AuthRepository", "Exceção durante o registro.", e)
            e.printStackTrace()
            return false
        }
    }
}

data class LoginResult(val success: Boolean, val isProfileComplete: Boolean)