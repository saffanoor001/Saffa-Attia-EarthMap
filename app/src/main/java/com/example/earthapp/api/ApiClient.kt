package com.example.earthapp.api


import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // Base for Windy v3 webcams:
    private const val BASE_URL = "https://api.windy.com/"

    // TODO: replace with BuildConfig.WINDY_API_KEY or read from secure storage
    const val WINDY_API_KEY = "bKBex3E3AyPqVFEWh6Iz1zLIxMJgII3C"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val headerInterceptor = Interceptor { chain ->
        val newReq = chain.request().newBuilder()
            .addHeader("x-windy-api-key", WINDY_API_KEY)
            .build()
        chain.proceed(newReq)
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(logging)
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val windyService: WindyService by lazy {
        retrofit.create(WindyService::class.java)
    }
}