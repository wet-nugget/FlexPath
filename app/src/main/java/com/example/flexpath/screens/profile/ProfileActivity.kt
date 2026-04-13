package com.example.flexpath.screens.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.R
import com.example.flexpath.screens.login.LoginActivity

class ProfileActivity : Activity() {
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

            val intent = Intent(this, LoginActivity::class.java)
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