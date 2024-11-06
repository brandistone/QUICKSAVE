package com.example.myapp

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Function to create Retrofit with an interceptor
fun createRetrofit(): Retrofit {
    // Create an Interceptor to add headers to every request
    val interceptor = Interceptor { chain ->
        val originalRequest: Request = chain.request()

        // Add headers to the original request
        val modifiedRequest = originalRequest.newBuilder()
            .addHeader("x-rapidapi-host", "kenyan-news-api.p.rapidapi.com")
            .addHeader("x-rapidapi-key", "bee9fa892amsha101f2714fc0357p110851jsn2a1a00f7d616") // Add your RapidAPI key here
            .build()

        // Proceed with the modified request
        chain.proceed(modifiedRequest)
    }

    // Create OkHttpClient with the interceptor
    val client = OkHttpClient.Builder()
        .addInterceptor(interceptor)  // Add the interceptor
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })  // Optional: for logging network requests
        .build()

    // Create and return Retrofit instance
    return Retrofit.Builder()
        .baseUrl("https://kenyan-news-api.p.rapidapi.com/")  // Use the base URL for the API
        .client(client)  // Set the OkHttpClient with the interceptor
        .addConverterFactory(GsonConverterFactory.create())  // Add Gson converter
        .build()
}
