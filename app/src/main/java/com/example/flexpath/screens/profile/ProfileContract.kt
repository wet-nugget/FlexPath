package com.example.flexpath.screens.profile

interface ProfileContract {
    interface View {
        fun showUsername(username: String)
        fun showMessage(message: String)
        fun showLoading(show: Boolean)
        fun navigateToDashboard()
        fun navigateToLogin()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadProfile()
        fun clearUsers()
        fun onBackToDashboard()
    }
}
