package com.example.myapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


import android.view.View
import android.widget.ProgressBar
import android.widget.Toast

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

class Routes : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var newsAdapter: NewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes)

        progressBar = findViewById(R.id.progress_bar)
        newsRecyclerView = findViewById(R.id.news_recycler_view)

        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsAdapter = NewsAdapter()
        newsRecyclerView.adapter = newsAdapter

        fetchNews()
    }

    private fun fetchNews() {
        progressBar.visibility = View.VISIBLE

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
                val response = service.getSwahiliNews()
                progressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val newsItems = response.body()
                    if (newsItems != null) {
                        newsAdapter.submitList(newsItems)
                    }
                } else {
                    Toast.makeText(this@Routes, "Failed to fetch news", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@Routes, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
