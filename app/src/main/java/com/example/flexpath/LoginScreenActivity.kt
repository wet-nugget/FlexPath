package com.example.flexpath

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Display.Mode
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class LoginScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val textviewCreateAccount: TextView = findViewById(R.id.textviewCreateAccount)

        val edittextUsername = findViewById<EditText>(R.id.edittextUsername)
        val edittextPassword = findViewById<EditText>(R.id.edittextPassword)

        // Login -> Dashboard
        buttonLogin.setOnClickListener {
            val username = edittextUsername.text.toString()
            val password = edittextPassword.text.toString()

            val prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val savedUsername = prefs.getString("username", null)
            val savedPassword = prefs.getString("password", null)

            if (!username.isNullOrEmpty() || !password.isNullOrEmpty()) {
                if (username == savedUsername && password == savedPassword) {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter username or password", Toast.LENGTH_SHORT).show()
            }
        }

        // Create Account -> Register
        textviewCreateAccount.setOnClickListener {
            val intent = Intent(this, RegisterScreenActivity::class.java)
            startActivity(intent)
        }
    }
}