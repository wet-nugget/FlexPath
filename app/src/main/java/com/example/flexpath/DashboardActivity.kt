package com.example.flexpath

import android.app.Activity
import android.content.Intent
import android.media.Image
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class DashboardActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val username = intent.getStringExtra("username")
        val password = intent.getStringExtra("password")

        val iconHome = findViewById<ImageView>(R.id.iconHome)
        val iconProfile = findViewById<ImageView>(R.id.iconProfile)

        val logoutView = findViewById<TextView>(R.id.textviewLogout)


        // Dashboard -> Profile
        iconProfile.setOnClickListener {
            val intent = Intent(this, ProfileScreenActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        // Dashboard -> Logout
        logoutView.setOnClickListener {
            val intent = Intent(this, LoginScreenActivity::class.java)
            Toast.makeText(this, "Logged out.", Toast.LENGTH_SHORT).show()
            startActivity(intent)
            finish()
        }
    }
}