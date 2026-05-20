package com.example.flexpath.screens.login

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
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.screens.register.RegisterActivity
import com.example.flexpath.ui.setEnabledRecursive

class LoginActivity : Activity(), LoginContract.View {
    private lateinit var presenter: LoginContract.Presenter
    private lateinit var progressBar: ProgressBar
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var formContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        LoginPresenter(this).also { presenter = it }
        presenter.attachView(this)

        progressBar = findViewById(R.id.progressBarGlobal)
        usernameInput = findViewById(R.id.edittextUsername)
        passwordInput = findViewById(R.id.edittextPassword)
        loginButton = findViewById(R.id.buttonLogin)
        formContainer = findViewById<View?>(R.id.layoutForm) ?: findViewById(android.R.id.content)

        val textviewCreateAccount: TextView = findViewById(R.id.textviewCreateAccount)

        loginButton.setOnClickListener {
            presenter.onLoginClicked(
                usernameInput.text.toString().trim(),
                passwordInput.text.toString().trim()
            )
        }

        textviewCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun showUsernameError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showPasswordError(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showLoginSuccess(username: String) {
        runOnUiThread {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
            finish()
        }
    }

    override fun showLoginFailure(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showLoading(show: Boolean) {
        runOnUiThread {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            (formContainer as? ViewGroup)?.setEnabledRecursive(!show)
            loginButton.isEnabled = !show
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
