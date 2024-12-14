package com.example.myapp.api

import com.example.myapp.models.FoursquareResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface FoursquareApi {
    @GET("search")
    fun searchPlaces(
        @Header("Authorization") apiKey: String, // Add the API key here
        @Header("Accept") accept: String = "application/json", // Set default accept header
        @Query("query") query: String, // Search query (e.g., "hospital")
        @Query("ll") location: String // Latitude and longitude as a string (e.g., "40.7484,-73.9857")
    ): Call<FoursquareResponse>
}


