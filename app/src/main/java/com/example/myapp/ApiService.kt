package com.example.myapp

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("hospitals/nearby")
    fun getNearbyHospitals(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double
    ): Call<List<Hospital>>
}
