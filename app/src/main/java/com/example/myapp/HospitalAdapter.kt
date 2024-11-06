// HospitalAdapter.kt
package com.example.myapp

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class HospitalAdapter(
    private val hospitals: List<LocationResult>
) : RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val hospitalNameTextView: TextView = itemView.findViewById(R.id.hospital_name)
        private val hospitalLocationTextView: TextView = itemView.findViewById(R.id.hospital_location)

        fun bind(hospital: LocationResult) {
            hospitalNameTextView.text = hospital.display_name
            val locationText = if (!hospital.lat.isNullOrEmpty() && !hospital.lon.isNullOrEmpty()) {
                "Lat: ${hospital.lat}, Lon: ${hospital.lon}"
            } else {
                "Location data unavailable"
            }
            hospitalLocationTextView.text = locationText

            itemView.setOnClickListener {
                // Make sure the latitude and longitude are available
                if (!hospital.lat.isNullOrEmpty() && !hospital.lon.isNullOrEmpty()) {
                    // Create a URI for Google Maps
                    val uri = "geo:${hospital.lat},${hospital.lon}?q=${hospital.lat},${hospital.lon}(${hospital.display_name})"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                    intent.setPackage("com.google.android.apps.maps") // Optional: only use Google Maps

                    try {
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(itemView.context, "Google Maps app is not installed.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle missing location data
                    Toast.makeText(itemView.context, "Hospital location not available.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hospital_item, parent, false)
        return HospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        holder.bind(hospitals[position])
    }

    override fun getItemCount(): Int {
        return hospitals.size
    }
}
