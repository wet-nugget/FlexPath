package com.example.flexpath.screens.profile

import com.example.flexpath.data.UserRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context

class ProfilePresenter(context: Context) : ProfileContract.Presenter {
    private var view: ProfileContract.View? = null
    private val repo = UserRepository(context)
    private val scope: CoroutineScope = MainScope()

    override fun attachView(view: ProfileContract.View) { this.view = view }

    override fun detachView() {
        view = null
        scope.cancel()
    }

    override fun loadProfile() {
        view?.showLoading(true)
        scope.launch {
            try {
                val username = withContext(Dispatchers.IO) { repo.getSavedUsername() }
                if (username != null) view?.showUsername(username)
                else view?.showUsername("")
            } catch (ex: Exception) {
                view?.showMessage("Failed to load profile: ${ex.message ?: "unknown error"}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun clearUsers() {
        view?.showLoading(true)
        scope.launch {
            try {
                withContext(Dispatchers.IO) { repo.clearUser() }
                view?.showMessage("Cleared users")
                view?.navigateToLogin()
            } catch (ex: Exception) {
                view?.showMessage("Failed to clear users: ${ex.message ?: "unknown error"}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun onDashboardClicked() {
        view?.navigateToDashboard()
    }

    override fun onWorkoutsClicked() {
        view?.navigateToWorkouts()
    }

    override fun onPlansClicked() {
        view?.navigateToPlans()
    }
}
