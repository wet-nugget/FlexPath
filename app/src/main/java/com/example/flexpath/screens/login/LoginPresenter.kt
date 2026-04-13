package com.example.flexpath.screens.login

import android.content.Context

class LoginPresenter(private val context: Context) : LoginContract.Presenter {
    private var view: LoginContract.View? = null
    private val prefsName = "UserPrefs"

    override fun attachView(view: LoginContract.View) { this.view = view }
    override fun detachView() { this.view = null }

    override fun onLoginClicked(username: String, password: String) {
        if (username.isBlank()) {
            view?.showUsernameError("Please enter username")
            return
        }
        if (password.isBlank()) {
            view?.showPasswordError("Please enter password")
            return
        }

        view?.showLoading(true)
        val prefs = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val savedUsername = prefs.getString("username", null)
        val savedPassword = prefs.getString("password", null)

        if (savedUsername != null && savedPassword != null) {
            if (username == savedUsername && password == savedPassword) {
                view?.showLoading(false)
                view?.showLoginSuccess(username)
            } else {
                view?.showLoading(false)
                view?.showLoginFailure("Invalid credentials")
            }
        } else {
            // Demo fallback: allow login or require registration
            view?.showLoading(false)
            view?.showLoginFailure("No registered user. Please register first.")
        }
    }

    override fun onCreateAccountClicked() {
        // Presenter can instruct view to navigate; keep navigation in View implementation
    }
}
