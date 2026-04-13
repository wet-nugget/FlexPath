package com.example.flexpath.screens.register

import com.example.flexpath.data.UserRepository
import android.content.Context

class RegisterPresenter(context: Context) : RegisterContract.Presenter {
    private var view: RegisterContract.View? = null
    private val repo = UserRepository(context)

    override fun attachView(view: RegisterContract.View) { this.view = view }
    override fun detachView() { this.view = null }

    override fun onRegister(username: String, password: String, reenter: String) {
        if (username.isBlank() || password.isBlank() || reenter.isBlank()) {
            view?.showError("Please fill in all fields")
            return
        }
        if (password != reenter) {
            view?.showError("Passwords do not match")
            return
        }
        val existing = repo.getSavedUsername()
        if (existing != null && existing == username) {
            view?.showError("Username already registered")
            return
        }
        view?.showLoading(true)
        repo.saveUser(username, password)
        view?.showLoading(false)
        view?.showSuccess()
    }
}
