package com.example.myapp

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

// Create the Retrofit client
val retrofit = Retrofit.Builder()
    .baseUrl("https://kenyan-news-api.p.rapidapi.com/") // Base URL for the API
    .addConverterFactory(GsonConverterFactory.create()) // Convert the JSON response into Kotlin objects
    .client(OkHttpClient.Builder().addInterceptor { chain ->
        // Add headers, like API keys, to the request
        val newRequest = chain.request().newBuilder()
            .addHeader("x-rapidapi-host", "kenyan-news-api.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "bee9fa892amsha101f2714fc0357p110851jsn2a1a00f7d616") // Replace with your actual API key
            .build()
        chain.proceed(newRequest)
    }.build()) // Optional: OkHttpClient to add custom interceptors, like headers
    .build()

// Create the API service
val service = retrofit.create(KenyanNewsApiService::class.java)
