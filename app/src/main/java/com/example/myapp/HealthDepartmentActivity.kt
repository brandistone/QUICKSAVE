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
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthDepartmentActivity : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var findHospitalButton: Button
    private lateinit var api: NominatimApi
    private lateinit var progressBar: ProgressBar
    private lateinit var hospitalRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_health_department)

        findHospitalButton = findViewById(R.id.find_hospital_button)
        progressBar = findViewById(R.id.progress_bar)
        hospitalRecyclerView = findViewById(R.id.hospital_list_view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NominatimApi::class.java)

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

        val query = "hospital near ${latLng.latitude},${latLng.longitude}"

        // Call the API to get hospital locations
        api.searchLocation(query).enqueue(object : Callback<List<LocationResult>> {
            override fun onResponse(
                call: Call<List<LocationResult>>,
                response: Response<List<LocationResult>>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val locations = response.body()
                    if (!locations.isNullOrEmpty()) {
                        // Set up the adapter with the hospital data
                        val hospitalAdapter = HospitalAdapter(locations)
                        hospitalRecyclerView.adapter = hospitalAdapter
                        hospitalRecyclerView.visibility = View.VISIBLE // Show the list

                        // Now, send the user's location to the hospitals
                        sendLocationToHospitals(latLng.latitude, latLng.longitude)
                    } else {
                        Toast.makeText(this@HealthDepartmentActivity, "No hospitals found.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@HealthDepartmentActivity, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LocationResult>>, t: Throwable) {
                progressBar.visibility = View.GONE
                t.printStackTrace()
                Toast.makeText(this@HealthDepartmentActivity, "Failed to fetch hospitals", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Method to send the user's location to the hospitals
    private fun sendLocationToHospitals(latitude: Double, longitude: Double) {
        // Example hospital contact numbers (can be replaced with dynamic contact data)
        val hospitalContacts = listOf("1234567890", "0987654321") // Add actual hospital contact numbers here

        val message = "Emergency: A user at Latitude: $latitude, Longitude: $longitude needs assistance."

        // Send the location via SMS to each hospital
        for (contact in hospitalContacts) {
            sendSms(contact, message)
        }

        Toast.makeText(this, "Location sent to hospitals.", Toast.LENGTH_SHORT).show()
    }

    // Method to send SMS to a hospital contact
    private fun sendSms(contact: String, message: String) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(contact, null, message, null, null)
        } else {
            // Request SMS permission if not granted
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
                // Retry sending SMS after permission is granted
                val latitude = 0.0 // Replace with actual latitude
                val longitude = 0.0 // Replace with actual longitude
                sendLocationToHospitals(latitude, longitude)
            } else {
                Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
