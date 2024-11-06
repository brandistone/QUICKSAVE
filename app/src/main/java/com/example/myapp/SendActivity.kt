package com.example.myapp

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class SendActivity : AppCompatActivity() {
    private lateinit var selectedContactListView: ListView
    private lateinit var sendLocationButton: Button
    private lateinit var walkWithMeButton: Button
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var isSharingLocation = false
    private val handler = Handler(Looper.getMainLooper())
    private val LOCATION_INTERVAL = 10000L // 10 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        selectedContactListView = findViewById(R.id.selectedContactListView)
        sendLocationButton = findViewById(R.id.location_button) // Assuming this exists in your layout
        walkWithMeButton = findViewById(R.id.walk_with_me_button) // Add this button in your layout

        // Retrieve the selected contacts from the previous activity
        val selectedContacts: ArrayList<Contact>? = intent.getParcelableArrayListExtra("selectedContacts")
        val adapter = ContactAdapter(this, selectedContacts ?: arrayListOf())
        selectedContactListView.adapter = adapter

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Set up location request for Walk With Me
        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_INTERVAL
            fastestInterval = LOCATION_INTERVAL / 2
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // Set up location callback
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    sendSmsWithLocation("I'm walking, current location: https://maps.google.com/?q=${location.latitude},${location.longitude}")
                }
            }
        }

        // Click listener for sending a single location
        sendLocationButton.setOnClickListener {
            sendLocation() // Trigger the location sending process
        }

        // Click listener for Walk With Me feature
        walkWithMeButton.setOnClickListener {
            if (!isSharingLocation) {
                startWalkWithMe()
            } else {
                stopWalkWithMe()
            }
        }
    }

    // Function to fetch and send the current location
    private fun sendLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }

        // Get the last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val locationMessage = "My location: https://maps.google.com/?q=$latitude,$longitude"

                // Send the location via SMS to the selected contacts
                sendSmsWithLocation(locationMessage)
            } else {
                Toast.makeText(this, "Failed to retrieve location", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to start Walk With Me feature
    private fun startWalkWithMe() {
        isSharingLocation = true
        walkWithMeButton.text = "Stop Walk With Me"
        Toast.makeText(this, "Walk With Me started", Toast.LENGTH_SHORT).show()

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        }
    }

    // Function to stop Walk With Me feature
    private fun stopWalkWithMe() {
        isSharingLocation = false
        walkWithMeButton.text = "Start Walk With Me"
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Toast.makeText(this, "Walk With Me stopped", Toast.LENGTH_SHORT).show()
    }

    // Function to send SMS with the location link
    private fun sendSmsWithLocation(message: String) {
        val selectedContacts = (selectedContactListView.adapter as ContactAdapter).contacts
        if (selectedContacts.isEmpty()) {
            Toast.makeText(this, "No contacts selected.", Toast.LENGTH_SHORT).show()
            return
        }

        for (contact in selectedContacts) {
            val phoneNumber = contact.phoneNumber
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null)
        }

        Toast.makeText(this, "Location sent!", Toast.LENGTH_SHORT).show()
    }

    // Override back press to only go back when the user presses the back button
    override fun onBackPressed() {
        super.onBackPressed() // Only go back when the back button is pressed
    }

    // Adapter for displaying contacts in the ListView
    inner class ContactAdapter(private val context: AppCompatActivity, var contacts: List<Contact>) : BaseAdapter() {
        override fun getCount(): Int = contacts.size
        override fun getItem(position: Int): Contact = contacts[position]
        override fun getItemId(position: Int): Long = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.contacts_item, parent, false)
            val contact = getItem(position)
            val nameTextView = view.findViewById<TextView>(R.id.contact_name)
            nameTextView.text = contact.name
            return view
        }
    }
}
