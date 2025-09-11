package br.com.marllonbruno.fitnesstracker.android.data.remote

import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val preferencesRepository: UserPreferencesRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        if (originalRequest.url.encodedPath.contains("/api/auth/login")) {
            return chain.proceed(originalRequest)
        }

        // 1. Usamos runBlocking para obter o valor do Flow de forma síncrona
        val token = runBlocking {
            preferencesRepository.jwtToken.first()
        }

        // 2. Montamos a requisição original
        val requestBuilder = chain.request().newBuilder()

        // 3. Se o token existir, o adicionamos ao cabeçalho
        if(!token.isNullOrBlank()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        // 4. Prosseguimos com a requisição (com ou sem o cabeçalho)
        return chain.proceed(requestBuilder.build())
    }

}