package com.example.flexpath.screens.register

import android.content.Context
import com.example.flexpath.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterPresenter(context: Context) : RegisterContract.Presenter {
    private var view: RegisterContract.View? = null
    private val repo = UserRepository(context)
    private val scope: CoroutineScope = MainScope()

    override fun attachView(view: RegisterContract.View) { this.view = view }
    override fun detachView() {
        view = null
        scope.cancel()
    }

    override fun onRegister(username: String, password: String, reenter: String) {
        if (username.isBlank() || password.isBlank() || reenter.isBlank()) {
            view?.showError("Please fill in all fields")
            return
        }
        if (password != reenter) {
            view?.showError("Passwords do not match")
            return
        }

        view?.showLoading(true)
        scope.launch {
            try {
                val existing = withContext(Dispatchers.IO) { repo.getSavedUsername() } // suspend
                if (existing != null && existing == username) {
                    view?.showError("Username already registered")
                    return@launch
                }

                withContext(Dispatchers.IO) {
                    repo.saveUser(username, password) // suspend
                }

                view?.showSuccess()
            } catch (ex: Exception) {
                view?.showError("Registration failed: ${ex.message ?: "unknown error"}")
            } finally {
                view?.showLoading(false)
            }
        }
    }
}
