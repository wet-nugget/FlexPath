package com.example.flexpath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.w3c.dom.Text

class RegisterScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        val edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        val edittextPassword = findViewById<EditText>(R.id.edittextPassword)
        val edittextReenterPassword = findViewById<EditText>(R.id.edittextReenterPassword)
        val buttonSubmit = findViewById<Button>(R.id.buttonSubmit)
        val textviewLoginLink = findViewById<TextView>(R.id.textviewLoginLink)

        // Submit button -> Login
        buttonSubmit.setOnClickListener {
            val username = edittextUsername.text.toString()
            val password = edittextPassword.text.toString()
            val reenterPassword = edittextReenterPassword.text.toString()

            if (username.isNullOrEmpty() || password.isNullOrEmpty() || reenterPassword.isNullOrEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else if (password != reenterPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            } else {
                val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
                val savedUsername = prefs.getString("username", null)

                if (username == savedUsername) {
                    Toast.makeText(this, "Username already taken.", Toast.LENGTH_SHORT).show()
                } else {
                    val editor = prefs.edit()
                    editor.putString("username", username)
                    editor.putString("password", password)
                    editor.apply()

                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginScreenActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

        }

        // Already have account -> Login
        textviewLoginLink.setOnClickListener {
            val intent = Intent(this, LoginScreenActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}