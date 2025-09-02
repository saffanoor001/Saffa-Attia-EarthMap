package com.example.earthapp.nearby.api

import com.example.earthapp.nearby.model.Places
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface PlacesApi {
    @GET("places/search")
    suspend fun searchPlaces(
        @Query("ll") latLong: String,
        @Query("radius") radius: Int,
        @Query("categories") categoryId: String? = null,
        @Query("limit") limit: Int = 20
    ): Response<Places>
}