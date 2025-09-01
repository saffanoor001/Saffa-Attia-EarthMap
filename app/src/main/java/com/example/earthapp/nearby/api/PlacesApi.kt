package com.example.earthapp.nearby.api

import com.example.earthapp.nearby.model.Places
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface PlacesApi {
    @GET("places/search")

    suspend fun searchPlaces(
        @Header("Accept") accept: String = "application/json",
        @Query("ll") latLong: String,
        @Query("radius") radius: Int,
        @Query("categories") categories: String,
        @Query("limit") limit: Int = 20
    ): Response<Places>
}