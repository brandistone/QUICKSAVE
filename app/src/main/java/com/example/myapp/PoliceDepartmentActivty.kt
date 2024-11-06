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

class PoliceDepartmentActivty : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var findPoliceStationButton: Button
    private lateinit var api: NominatimApi
    private lateinit var progressBar: ProgressBar
    private lateinit var policeStationRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_police_department_activty)

        findPoliceStationButton = findViewById(R.id.find_police_station_button)
        progressBar = findViewById(R.id.progress_bar)
        policeStationRecyclerView = findViewById(R.id.police_station_list_view)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/") // OpenStreetMap API
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NominatimApi::class.java)

        // Set RecyclerView layout manager
        policeStationRecyclerView.layoutManager = LinearLayoutManager(this)

        findPoliceStationButton.setOnClickListener {
            findNearestPoliceStation()  // Finding police stations near Nairobi
        }
    }

    private fun findNearestPoliceStation() {
        // Hardcode the coordinates of Nairobi
        val nairobiLatLng = LatLng(-1.286389, 36.817223)
        fetchPoliceStationLocation(nairobiLatLng)  // Use Nairobi coordinates
    }

    private fun fetchPoliceStationLocation(latLng: LatLng) {
        progressBar.visibility = View.VISIBLE
        policeStationRecyclerView.visibility = View.GONE // Hide the list while loading

        // Change the query to search for police stations near Nairobi
        val query = "police station"

        // Call the API to get police station locations
        api.searchLocation(query).enqueue(object : Callback<List<LocationResult>> {
            override fun onResponse(
                call: Call<List<LocationResult>>,
                response: Response<List<LocationResult>>
            ) {
                progressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val locations = response.body()
                    if (!locations.isNullOrEmpty()) {
                        // Set up the adapter with the police station data
                        val policeStationAdapter = PoliceStationAdapter(locations) { policeStation ->
                            // Define what should happen when an item is clicked
                            // For example, show a Toast with the police station name
                            Toast.makeText(this@PoliceDepartmentActivty, "Clicked on: ${policeStation.display_name}", Toast.LENGTH_SHORT).show()
                        }
                        policeStationRecyclerView.adapter = policeStationAdapter
                        policeStationRecyclerView.visibility = View.VISIBLE // Show the list
                    } else {
                        Toast.makeText(this@PoliceDepartmentActivty, "No police stations found near Nairobi.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@PoliceDepartmentActivty, "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LocationResult>>, t: Throwable) {
                progressBar.visibility = View.GONE
                t.printStackTrace()
                Toast.makeText(this@PoliceDepartmentActivty, "Failed to fetch police stations", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                findNearestPoliceStation()
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

