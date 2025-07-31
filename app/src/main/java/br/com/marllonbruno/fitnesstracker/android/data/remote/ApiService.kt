package br.com.marllonbruno.fitnesstracker.android.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body request: UserRegisterRequest): Response<AuthenticationResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: AuthenticationRequest): Response<AuthenticationResponse>
}