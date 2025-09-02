package br.com.marllonbruno.fitnesstracker.android.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body request: UserRegisterRequest): Response<AuthenticationResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: AuthenticationRequest): Response<AuthenticationResponse>

    @GET("/api/profile")
    suspend fun getProfile(): Response<ProfileDataResponse>

    @PUT("/api/profile")
    suspend fun updateProfile(
        @Body request: ProfileUpdateRequest
    ) : Response<ProfileDataResponse>

    @GET("/api/recipes/list-all")
    suspend fun getRecipes(): Response<List<RecipeSummaryResponse>>

    @GET("/api/recipes/details/{id}")
    suspend fun getRecipeDetails(@Path("id") recipeId: Long): Response<RecipeDetailsResponse>

    @POST("/api/recipes/new")
    suspend fun createRecipe(@Body request: RecipeCreateRequest): Response<RecipeDetailsResponse>
}