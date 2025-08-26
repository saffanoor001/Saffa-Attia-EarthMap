package com.example.earthapp.api

import com.example.earthapp.model.Country
import com.example.earthapp.model.WebcamsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WindyService {

    @GET("webcams/api/v3/countries")
    fun getCountries(@Query("lang") lang: String = "en"): Call<List<Country>>


    @GET("webcams/api/v3/webcams")
    fun getWebcamsByCountry(
        @Query("countries") countries: String,
        @Query("limit") limit: Int = 50,
        @Query("include") include: String = "images,location"
    ): Call<WebcamsResponse>
}
