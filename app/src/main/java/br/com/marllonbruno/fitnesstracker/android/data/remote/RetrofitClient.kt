package br.com.marllonbruno.fitnesstracker.android.data.remote

import android.content.Context
import br.com.marllonbruno.fitnesstracker.android.data.local.UserPreferencesRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val moshi = Moshi.Builder()
        .add(LocalDateAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

    fun create(context: Context): ApiService {
        val prefsRepository = UserPreferencesRepository(context)

        val authInterceptor = AuthInterceptor(prefsRepository)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(ApiService::class.java)
    }
}