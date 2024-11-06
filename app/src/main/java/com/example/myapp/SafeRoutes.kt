package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class SafeRoutes : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe_routes)

        // Create Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://kenyan-news-api.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Create the service
        val service = retrofit.create(KenyanNewsApiService::class.java)

        // Make the network request on a coroutine
        GlobalScope.launch(Dispatchers.Main) {
            try {
                // Pass "Machakos" as the location parameter
                val response = service.getSwahiliNews()
                if (response.isSuccessful) {
                    val newsItems = response.body()
                    if (newsItems != null) {
                        // Display the news for Machakos
                        Toast.makeText(this@SafeRoutes, "Found ${newsItems.size} items for Machakos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SafeRoutes, "Failed to fetch news", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@SafeRoutes, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Button to go to Routes activity
        val safeRoutesButton: Button = findViewById(R.id.safe_routes)
        safeRoutesButton.setOnClickListener {
            val intent = Intent(this, Routes::class.java)
            startActivity(intent)
        }
    }
}

