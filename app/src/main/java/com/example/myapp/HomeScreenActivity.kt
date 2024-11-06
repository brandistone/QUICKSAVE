package com.example.myapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class HomeScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        val contactsButton = findViewById<Button>(R.id.contacts_button)
        val healthDepartmentButton = findViewById<Button>(R.id.health_department_button)
        val policeButton = findViewById<Button>(R.id.police_button)
        val safeRoutes = findViewById<Button>(R.id.safe_routes)
        val toolbar = findViewById<Toolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)

        // Set title text color to black
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.black))

        // Set up the toolbar
        setSupportActionBar(toolbar)

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
