package com.example.myapp

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ContactsActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_CALL_PHONE = 2 // Example value
        const val REQUEST_CODE_READ_CONTACTS = 1
        const val MAX_SELECTION = 5
    }

    private lateinit var contactsRecyclerView: RecyclerView
    private lateinit var contactsAdapter: ContactsAdapter
    private lateinit var contactsList: MutableList<Contact>
    private val selectedContacts = mutableListOf<Contact>()
    private lateinit var okButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts)

        // Initialize UI components
        contactsRecyclerView = findViewById(R.id.contacts_recycler_view)
        okButton = findViewById(R.id.ok_button)

        // Set up RecyclerView layout
        contactsRecyclerView.layoutManager = LinearLayoutManager(this)

        // Check and request permissions
        checkPermissions()

        // Set OK button click listener
        okButton.setOnClickListener {
            if (selectedContacts.isNotEmpty() && selectedContacts.size <= MAX_SELECTION) {
                Log.d("ContactsActivity", "OK button clicked, proceeding to next activity with ${selectedContacts.size} contacts.")
                val intent = Intent(this, SendActivity::class.java)
                intent.putParcelableArrayListExtra("selectedContacts", ArrayList(selectedContacts))
                startActivity(intent)
            } else {
                Toast.makeText(this, "Select up to $MAX_SELECTION contacts", Toast.LENGTH_SHORT).show()
                Log.d("ContactsActivity", "No contacts selected or too many selected.")
            }
        }
    }

    // Function to check and request permissions
    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_CONTACTS),
                REQUEST_CODE_READ_CONTACTS)
        } else {
            loadContacts() // Permission already granted, load contacts
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_CONTACTS && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadContacts() // Load contacts after permission is granted
        } else {
            Toast.makeText(this, "Permission denied. Cannot load contacts.", Toast.LENGTH_SHORT).show()
        }
    }

    // Load contacts from the device
    private fun loadContacts() {
        contactsList = getContacts().toMutableList()
        contactsAdapter = ContactsAdapter(contactsList, this)
        contactsRecyclerView.adapter = contactsAdapter
    }

    // Function to fetch contacts from the content resolver
    private fun getContacts(): List<Contact> {
        val contactList = mutableListOf<Contact>()
        val contentResolver: ContentResolver = contentResolver

        val cursor = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val id = it.getString(it.getColumnIndex(ContactsContract.Contacts._ID))
                val name = it.getString(it.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                val phoneCursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?",
                    arrayOf(id),
                    null
                )
                val phoneNumbers = mutableListOf<String>()
                phoneCursor?.use { phone ->
                    while (phone.moveToNext()) {
                        val phoneNumber = phone.getString(phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        phoneNumbers.add(phoneNumber)
                    }
                }

                if (phoneNumbers.isNotEmpty()) {
                    contactList.add(Contact(name, phoneNumbers.joinToString(", ")))
                }
            }
        }
        return contactList
    }

    inner class ContactsAdapter(
        private val contactList: List<Contact>,
        private val activity: ContactsActivity
    ) : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

        inner class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val contactName: TextView = itemView.findViewById(R.id.contact_name)
            val contactPhone: TextView = itemView.findViewById(R.id.contact_phone)
            val selectCheckBox: CheckBox = itemView.findViewById(R.id.contact_checkbox)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
            val itemView = LayoutInflater.from(parent.context).inflate(R.layout.contacts_item, parent, false)
            return ContactViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
            val contact = contactList[position]
            holder.contactName.text = contact.name
            holder.contactPhone.text = contact.phoneNumber

            holder.selectCheckBox.isChecked = selectedContacts.contains(contact)
            holder.selectCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (selectedContacts.size < MAX_SELECTION) {
                        selectedContacts.add(contact)
                        Log.d("ContactsActivity", "Contact selected: ${contact.name}")
                    } else {
                        holder.selectCheckBox.isChecked = false
                        Toast.makeText(activity, "You can only select up to $MAX_SELECTION contacts", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    selectedContacts.remove(contact)
                    Log.d("ContactsActivity", "Contact deselected: ${contact.name}")
                }
            }
        }

        override fun getItemCount() = contactList.size
    }

    // Override the back press to let user go back only manually
    override fun onBackPressed() {
        // Call super to allow the back press behavior as expected
        super.onBackPressed()
    }
}
