package com.example.earthapp.nearby.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PlacesApiInstance {
    private const val BASE_URL = "https://places-api.foursquare.com/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("accept", "application/json")
                .addHeader("X-Places-Api-Version", "2025-06-17")
                .addHeader(
                    "authorization",
                    "Bearer LCRB5AZWGK4JDACFYJ34F1MHBMSQBTXXQGPBWVJGZVPJQZUO"
                )
                .build()
            chain.proceed(request)
        }
        .build()

    val api: PlacesApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(PlacesApi::class.java)
    }
}
