package com.example.flexpath.screens.dashboard

import com.example.flexpath.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context

class DashboardPresenter(context: Context) : DashboardContract.Presenter {
    private var view: DashboardContract.View? = null
    private val repo = UserRepository(context)
    private val scope: CoroutineScope = MainScope()

    override fun attachView(view: DashboardContract.View) { this.view = view }
    override fun detachView() {
        view = null
        scope.cancel()
    }

    override fun loadUser() {
        view?.showLoading(true)
        scope.launch {
            try {
                val username = withContext(Dispatchers.IO) { repo.getSavedUsername() }
                if (username != null) {
                    view?.showGreeting(username)
                } else {
                    view?.showMessage("No user found. Please login.")
                    view?.navigateToLogin()
                }
            } catch (ex: Exception) {
                view?.showMessage("Failed to load user: ${ex.message ?: "unknown error"}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun onLogoutClicked() {
        view?.showLoading(true)
        scope.launch {
            try {
                withContext(Dispatchers.IO) { repo.clearUser() }
                view?.navigateToLogin()
            } catch (ex: Exception) {
                view?.showMessage("Logout failed: ${ex.message ?: "unknown error"}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun onProfileClicked() {
        scope.launch {
            try {
                val username = withContext(Dispatchers.IO) { repo.getSavedUsername() }
                if (username != null) view?.navigateToProfile(username)
                else view?.showMessage("No user info available")
            } catch (ex: Exception) {
                view?.showMessage("Unable to open profile: ${ex.message ?: "unknown error"}")
            }
        }
    }

    override fun onWorkoutsClicked() {
        view?.navigateToWorkouts()
    }

    override fun onPlansClicked() {
        view?.navigateToPlans()
    }
}
