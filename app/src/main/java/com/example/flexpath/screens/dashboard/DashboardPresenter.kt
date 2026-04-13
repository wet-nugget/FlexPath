package com.example.flexpath.screens.dashboard

import android.content.Context
import com.example.flexpath.data.UserRepository

class DashboardPresenter(context: Context) : DashboardContract.Presenter {
    private var view: DashboardContract.View? = null
    private val repo = UserRepository(context)

    override fun attachView(view: DashboardContract.View) { this.view = view }
    override fun detachView() { this.view = null }

    override fun loadUser() {
        view?.showLoading(true)
        val username = repo.getSavedUsername()
        view?.showLoading(false)
        if (username != null) {
            view?.showGreeting(username)
        } else {
            // No saved user -> force return to login
            view?.showMessage("No user found. Please login.")
            view?.navigateToLogin()
        }
    }

    override fun onLogoutClicked() {
        repo.clear()
        view?.navigateToLogin()
    }

    override fun onProfileClicked() {
        val username = repo.getSavedUsername()
        if (username != null) view?.navigateToProfile(username)
        else view?.showMessage("No user info available")
    }

    override fun onWorkoutsClicked() {
        view?.showMessage("Workouts not implemented yet")
    }

    override fun onPlansClicked() {
        view?.showMessage("Plans not implemented yet")
    }
}
