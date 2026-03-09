package com.example.flexpath

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class ProfileScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen)

        val backToDashboard = findViewById<ImageView>(R.id.iconHome) // add this TextView in XML
        val clearUsers = findViewById<TextView>(R.id.textviewClearUsers)

        clearUsers.setOnClickListener {
            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val editor = prefs.edit()
            editor.clear()
            editor.apply()

            val intent = Intent(this, LoginScreenActivity::class.java)
            Toast.makeText(this, "Cleared Users", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
        // Profile → Dashboard
        backToDashboard.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}