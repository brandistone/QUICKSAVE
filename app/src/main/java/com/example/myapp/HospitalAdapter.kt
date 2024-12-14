package com.example.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp.models.LocationResult

class HospitalAdapter(private val hospitals: List<LocationResult>) :
    RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.hospital_item, parent, false)
        return HospitalViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitals[position]
        holder.nameTextView.text = hospital.name
        holder.locationTextView.text = "Lat: ${hospital.location.lat}, Lng: ${hospital.location.lng}"
    }

    override fun getItemCount(): Int = hospitals.size

    class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.hospital_name)
        val locationTextView: TextView = itemView.findViewById(R.id.hospital_location)
    }
}
