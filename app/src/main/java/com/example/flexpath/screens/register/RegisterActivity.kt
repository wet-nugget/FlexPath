package com.example.flexpath.screens.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
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

        val editTextUsername: EditText = findViewById(R.id.edittextUsername)
        val editTextPassword: EditText = findViewById(R.id.edittextPassword)
        val editTextReenter: EditText = findViewById(R.id.edittextReenterPassword)
        val btnSubmit: Button = findViewById(R.id.buttonSubmit)

        btnSubmit.setOnClickListener {
            presenter.onRegister(
                editTextUsername.text.toString().trim(),
                editTextPassword.text.toString().trim(),
                editTextReenter.text.toString().trim()
            )
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
