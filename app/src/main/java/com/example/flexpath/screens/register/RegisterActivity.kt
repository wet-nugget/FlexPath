package com.example.flexpath.screens.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.flexpath.R
import com.example.flexpath.screens.login.LoginActivity
import com.example.flexpath.ui.setEnabledRecursive

class RegisterActivity : Activity(), RegisterContract.View {
    private lateinit var presenter: RegisterContract.Presenter

    private lateinit var progressBar: ProgressBar
    private lateinit var edittextUsername: EditText
    private lateinit var edittextPassword: EditText
    private lateinit var edittextReenter: EditText
    private lateinit var btnSubmit: Button
    private lateinit var formContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_screen)

        // Presenter creation and attach
        RegisterPresenter(this).also { presenter = it }
        presenter.attachView(this)

        // View bindings
        progressBar = findViewById(R.id.progressBarGlobal)
        edittextUsername = findViewById(R.id.edittextUsername)
        edittextPassword = findViewById(R.id.edittextPassword)
        edittextReenter = findViewById(R.id.edittextReenterPassword)
        btnSubmit = findViewById(R.id.buttonSubmit)
        // layoutForm exists in the provided layout; fallback to root if not found
        formContainer = findViewById<View?>(R.id.layoutForm) ?: findViewById(android.R.id.content)

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
            finish()
        }
    }

    override fun showError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showSuccess() {
        runOnUiThread {
            Toast.makeText(this, getString(R.string.registration_successful), Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun showLoading(show: Boolean) {
        runOnUiThread {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            (formContainer as? ViewGroup)?.setEnabledRecursive(!show)
            btnSubmit.isEnabled = !show
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
