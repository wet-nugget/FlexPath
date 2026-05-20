package com.example.flexpath.screens.login

import android.content.Context
import com.example.flexpath.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginPresenter(private val context: Context) : LoginContract.Presenter {
    private var view: LoginContract.View? = null
    private val repo = UserRepository(context)
    private val scope: CoroutineScope = MainScope()

    override fun attachView(view: LoginContract.View) { this.view = view }
    override fun detachView() {
        view = null
        scope.cancel()
    }

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
        scope.launch {
            try {
                val (savedUsername, savedPassword) = withContext(Dispatchers.IO) {
                    repo.getSavedCredentials()
                }

                if (savedUsername != null && savedPassword != null) {
                    if (username == savedUsername && password == savedPassword) {
                        view?.showLoginSuccess(username)
                    } else {
                        view?.showLoginFailure("Invalid credentials")
                    }
                } else {
                    view?.showLoginFailure("No registered user. Please register first.")
                }
            } catch (ex: Exception) {
                view?.showLoginFailure("Login failed: ${ex.message ?: "unknown error"}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun onCreateAccountClicked() {

    }
}
