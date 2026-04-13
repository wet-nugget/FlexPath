package com.example.flexpath.screens.dashboard

interface DashboardContract {
    interface View {
        fun showGreeting(username: String)
        fun navigateToProfile(username: String)
        fun navigateToLogin()
        fun showMessage(message: String)
        fun showLoading(show: Boolean)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadUser()
        fun onLogoutClicked()
        fun onProfileClicked()
        fun onWorkoutsClicked()
        fun onPlansClicked()
    }
}
