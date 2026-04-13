package com.example.flexpath.screens.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.flexpath.R
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.screens.register.RegisterActivity

class LoginActivity : Activity(), LoginContract.View {
    private lateinit var presenter: LoginContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_screen)

        presenter = LoginPresenter(this)
        presenter.attachView(this)

        val edittextUsername: EditText = findViewById(R.id.edittextUsername)
        val edittextPassword: EditText = findViewById(R.id.edittextPassword)
        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        val textviewCreateAccount: TextView = findViewById(R.id.textviewCreateAccount)

        buttonLogin.setOnClickListener {
            presenter.onLoginClicked(
                edittextUsername.text.toString().trim(),
                edittextPassword.text.toString().trim()
            )
        }

        textviewCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    override fun showUsernameError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showPasswordError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoginSuccess(username: String) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
        finish()
    }

    override fun showLoginFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading(show: Boolean) {

    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
