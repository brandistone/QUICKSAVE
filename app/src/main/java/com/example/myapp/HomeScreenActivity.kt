package com.example.myapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.RecognizerIntent
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.myapp.SpeechRecognitionHelper

class HomeScreenActivity : AppCompatActivity() {

    private lateinit var speechHelper: SpeechRecognitionHelper
    private lateinit var resultTextView: TextView
    private lateinit var startButton: Button
    private lateinit var contactsButton: Button
    private lateinit var healthDepartmentButton: Button
    private lateinit var policeButton: Button
    private lateinit var safeRoutes: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Initialize the UI elements
        contactsButton = findViewById(R.id.contacts_button)
        healthDepartmentButton = findViewById(R.id.health_department_button)
        policeButton = findViewById(R.id.police_button)
        safeRoutes = findViewById(R.id.safe_routes)
        toolbar = findViewById(R.id.topAppBar)
        resultTextView = findViewById(R.id.textView) // TextView to show the speech result
        startButton = findViewById(R.id.startButton) // Button to start speech recognition

        // Set up the toolbar
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black))

        // Check microphone permission
        checkPermissions()

        // Initialize SpeechRecognitionHelper
        speechHelper = SpeechRecognitionHelper(this) { result ->
            runOnUiThread { resultTextView.text = result }
        }

        // Set up the button listeners for each feature
        contactsButton.setOnClickListener {
            val intent = Intent(this, ContactsActivity::class.java)
            startActivity(intent)
        }

        healthDepartmentButton.setOnClickListener {
            val intent = Intent(this, HealthDepartmentActivity::class.java)
            startActivity(intent)
        }

        policeButton.setOnClickListener {
            val intent = Intent(this, PoliceDepartmentActivty::class.java)
            startActivity(intent)
        }

        safeRoutes.setOnClickListener {
            val intent = Intent(this, SafeRoutes::class.java)
            startActivity(intent)
        }

        // Start Speech Recognition when the start button is clicked
        startButton.setOnClickListener {
            speechHelper.startListening()
        }
    }

    // Check microphone permission
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tool_bar_menu, menu)
        return true
    }

    // Handle toolbar menu item clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Handle Settings action
                val intent = Intent(this, Settings::class.java)
                startActivity(intent)
                true
            }
            R.id.action_profile -> {
                // Handle Profile action
                val intent = Intent(this, Profile::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
