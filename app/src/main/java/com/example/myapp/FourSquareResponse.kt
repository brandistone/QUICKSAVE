package com.example.myapp.models

data class FoursquareResponse(
    val results: List<LocationResult>
)

data class LocationResult(
    val name: String,
    val location: Location,
    val distance: Int? // In meters, distance from the query point
)

data class Location(
    val lat: Double,
    val lng: Double
)
