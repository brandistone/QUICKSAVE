package com.example.myapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PoliceStationAdapter(
    private val policeStations: List<LocationResult>,
    private val onItemClick: (LocationResult) -> Unit // Click listener for item click
) : RecyclerView.Adapter<PoliceStationAdapter.PoliceStationViewHolder>() {

    // Define the ViewHolder
    inner class PoliceStationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Define the UI components (e.g., TextViews) that will display the police station info
        val nameTextView: TextView = itemView.findViewById(R.id.station_name)

        fun bind(policeStation: LocationResult) {
            // Bind data to the UI components
            nameTextView.text = policeStation.display_name // Example, assuming display_name is part of LocationResult
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoliceStationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_police_station, parent, false)
        return PoliceStationViewHolder(view)
    }

    override fun onBindViewHolder(holder: PoliceStationViewHolder, position: Int) {
        val policeStation = policeStations[position]
        holder.bind(policeStation)

        // Set the click listener on the item
        holder.itemView.setOnClickListener {
            onItemClick(policeStation) // Notify item click
        }
    }

    override fun getItemCount(): Int {
        return policeStations.size
    }
}
