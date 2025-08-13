package br.com.marllonbruno.fitnesstracker.android.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body request: UserRegisterRequest): Response<AuthenticationResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: AuthenticationRequest): Response<AuthenticationResponse>

    @GET("/api/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ProfileDataResponse>

    @PUT("/api/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileUpdateRequest
    ) : Response<ProfileDataResponse>
}