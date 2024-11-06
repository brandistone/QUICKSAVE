package com.example.myapp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KenyanNewsApiService {
    @GET("news/swahili")
    suspend fun getSwahiliNews(
        @Query("location") location: String? = null  // Optional parameter for location
    ): Response<List<NewsItem>>
}
