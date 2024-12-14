package com.example.myapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.myapp.api.FoursquareApi
import com.example.myapp.models.FoursquareResponse

class HealthDepartmentActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var findHospitalButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var hospitalRecyclerView: RecyclerView
    private lateinit var api: FoursquareApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_department)

        findHospitalButton = findViewById(R.id.find_hospital_button)
        progressBar = findViewById(R.id.progress_bar)
        hospitalRecyclerView = findViewById(R.id.hospital_list_view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.foursquare.com/v3/places/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(FoursquareApi::class.java)

        // Set RecyclerView layout manager
        hospitalRecyclerView.layoutManager = LinearLayoutManager(this)

        findHospitalButton.setOnClickListener {
            findNearestHospital()
        }
    }

    private fun findNearestHospital() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val currentLatLng = LatLng(it.latitude, it.longitude)
                fetchHospitalLocation(currentLatLng)
            } ?: Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchHospitalLocation(latLng: LatLng) {
        progressBar.visibility = View.VISIBLE
        hospitalRecyclerView.visibility = View.GONE // Hide the list while loading

        val latLngString = "${latLng.latitude},${latLng.longitude}"
        val query = "hospital"
        val apiKey = "fsq3Wre39Ss+KtfemSWHYErY9wk4lzq2D8J833FeYCyVK3I=" // Replace with your actual Foursquare API key

        api.searchPlaces(apiKey, "application/json", query, latLngString)
            .enqueue(object : Callback<FoursquareResponse> {
                override fun onResponse(
                    call: Call<FoursquareResponse>,
                    response: Response<FoursquareResponse>
                ) {
                    if (response.isSuccessful) {
                        val results = response.body()?.results
                        // Handle successful response
                    } else {
                        Toast.makeText(
                            this@HealthDepartmentActivity,
                            "Error: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<FoursquareResponse>, t: Throwable) {
                    Toast.makeText(
                        this@HealthDepartmentActivity,
                        "Failed to fetch hospitals",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

    }

    private fun sendLocationToHospitals(latitude: Double, longitude: Double) {
        val hospitalContacts = listOf("1234567890", "0987654321") // Add actual hospital contact numbers here
        val message = "Emergency: A user at Latitude: $latitude, Longitude: $longitude needs assistance."

        for (contact in hospitalContacts) {
            sendSms(contact, message)
        }

        Toast.makeText(this, "Location sent to hospitals.", Toast.LENGTH_SHORT).show()
    }

    private fun sendSms(contact: String, message: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(contact, null, message, null, null)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 2)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findNearestHospital()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "SMS permission granted.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
