package com.example.flexpath.screens.login

interface LoginContract {
    interface View {
        fun showUsernameError(message: String)
        fun showPasswordError(message: String)
        fun showLoginSuccess(username: String)
        fun showLoginFailure(message: String)
        fun showLoading(show: Boolean)
    }

    interface Presenter {
        fun onLoginClicked(username: String, password: String)
        fun onCreateAccountClicked()
        fun attachView(view: View)
        fun detachView()
    }
}
