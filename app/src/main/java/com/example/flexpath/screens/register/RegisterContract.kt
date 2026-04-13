package com.example.flexpath.screens.register

interface RegisterContract {
    interface View {
        fun showError(message: String)
        fun showSuccess()
        fun showLoading(show: Boolean)
    }
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun onRegister(username: String, password: String, reenter: String)
    }
}
