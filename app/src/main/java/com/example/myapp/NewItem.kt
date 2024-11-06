package com.example.myapp
data class NewsItem(
    val title: String,
    val description: String,
    val link: String? = null  // Adjust this if the field name is different in your API
)
