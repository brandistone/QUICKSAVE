package com.example.myapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {
    @GET("search?format=json")
    fun searchLocation(
        @Query("q") query: String,
        @Query("email") email: String = "your_email@example.com"
    ): Call<List<LocationResult>> // Ensure this matches the data class used
}

