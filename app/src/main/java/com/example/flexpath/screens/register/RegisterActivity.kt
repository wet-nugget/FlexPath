package com.example.flexpath.screens.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.flexpath.R
import com.example.flexpath.screens.login.LoginActivity

class RegisterActivity : Activity(), RegisterContract.View {
    private lateinit var presenter: RegisterContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        presenter = RegisterPresenter(this)
        presenter.attachView(this)

        val edittextUsername: EditText = findViewById(R.id.edittextUsername)
        val edittextPassword: EditText = findViewById(R.id.edittextPassword)
        val edittextReenter: EditText = findViewById(R.id.edittextReenterPassword)
        val btnSubmit: Button = findViewById(R.id.buttonSubmit)
        val textviewLoginLink: TextView = findViewById(R.id.textviewLoginLink)

        btnSubmit.setOnClickListener {
            presenter.onRegister(
                edittextUsername.text.toString().trim(),
                edittextPassword.text.toString().trim(),
                edittextReenter.text.toString().trim()
            )
        }

        textviewLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    override fun showError(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    override fun showSuccess() {
        Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
    override fun showLoading(show: Boolean) {

    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
