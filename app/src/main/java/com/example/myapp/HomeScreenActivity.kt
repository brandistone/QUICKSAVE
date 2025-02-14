package com.example.myapp

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.telephony.SmsManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class HomeScreenActivity : AppCompatActivity() {
    private lateinit var resultTextView: TextView
    private lateinit var startButton: Button
    private lateinit var contactsButton: Button
    private lateinit var healthDepartmentButton: Button
    private lateinit var policeButton: Button
    private lateinit var safeRoutesButton: Button
    private lateinit var selectContactsButton: Button
    private lateinit var toolbar: Toolbar
    private lateinit var speechRecognizer: SpeechRecognizer

    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 1
        private const val SEND_SMS_PERMISSION_REQUEST_CODE = 2
        private const val READ_CONTACTS_PERMISSION_REQUEST_CODE = 3
        private const val PICK_CONTACT_REQUEST = 4

        // SharedPreferences Key
        private const val PREFS_NAME = "EmergencyContacts"
        private const val CONTACTS_KEY = "SelectedContacts"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        initializeViews()
        setupToolbar()
        checkPermissions()
        setupClickListeners()
        setupSpeechRecognition()
    }

    private fun initializeViews() {
        contactsButton = findViewById(R.id.contacts_button)
        healthDepartmentButton = findViewById(R.id.health_department_button)
        policeButton = findViewById(R.id.police_button)
        safeRoutesButton = findViewById(R.id.safe_routes)
        selectContactsButton = findViewById(R.id.contact_checkbox)
        toolbar = findViewById(R.id.topAppBar)
        resultTextView = findViewById(R.id.textView)
        startButton = findViewById(R.id.startButton)
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black))
    }

    private fun setupClickListeners() {
        contactsButton.setOnClickListener {
            startActivity(Intent(this, ContactsActivity::class.java))
        }

        healthDepartmentButton.setOnClickListener {
            startActivity(Intent(this, HealthDepartmentActivity::class.java))
        }

        policeButton.setOnClickListener {
            startActivity(Intent(this, PoliceDepartmentActivty::class.java))
        }

        safeRoutesButton.setOnClickListener {
            startActivity(Intent(this, SafeRoutes::class.java))
        }

        selectContactsButton.setOnClickListener {
            pickContact()
        }

        startButton.setOnClickListener {
            speechRecognizer.startListening(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            })
        }
    }

    private fun checkPermissions() {
        val permissions = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) permissions.add(Manifest.permission.RECORD_AUDIO)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
            != PackageManager.PERMISSION_GRANTED
        ) permissions.add(Manifest.permission.SEND_SMS)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED
        ) permissions.add(Manifest.permission.READ_CONTACTS)

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 1)
        }
    }

    private fun setupSpeechRecognition() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                resultTextView.text = "Listening..."
            }

            override fun onBeginningOfSpeech() {
                resultTextView.text = "Processing..."
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val spokenText = matches[0].lowercase(Locale.getDefault())

                    resultTextView.text = spokenText  // Show recognized text

                    // Detect emergency words
                    if (spokenText.contains("help") || spokenText.contains("scream")) {
                        sendEmergencyMessage()
                    }
                }
            }

            override fun onError(error: Int) {
                resultTextView.text = "Error: $error"
            }

            override fun onEndOfSpeech() {
                resultTextView.text = "Tap to Speak"
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    val partialText = matches[0].lowercase(Locale.getDefault())
                    resultTextView.text = partialText

                    // Emergency detection in partial results
                    if (partialText.contains("help") || partialText.contains("scream")) {
                        sendEmergencyMessage()
                    }
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onRmsChanged(rmsdB: Float) {}
        })
    }

    private fun pickContact() {
        val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(intent, PICK_CONTACT_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            val contactUri: Uri? = data?.data
            contactUri?.let {
                val cursor: Cursor? = contentResolver.query(it, null, null, null, null)
                cursor?.use {
                    if (it.moveToFirst()) {
                        val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val contactNumber = it.getString(numberIndex)

                        // Save the selected contact
                        saveSelectedContact(contactNumber)
                    }
                }
            }
        }
    }

    private fun saveSelectedContact(contact: String) {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(CONTACTS_KEY, contact)
        editor.apply()
    }

    private fun sendEmergencyMessage() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val contact = prefs.getString(CONTACTS_KEY, null) ?: return

        val smsManager = SmsManager.getDefault()
        val message = "🚨 EMERGENCY ALERT: 'HELP' detected! Please respond immediately."
        smsManager.sendTextMessage(contact, null, message, null, null)

        resultTextView.text = "Emergency Alert Sent 🚨"
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
    }
}
