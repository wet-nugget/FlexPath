package com.example.flexpath.screens.workouts

import com.example.flexpath.data.WorkoutsRepository
import kotlinx.coroutines.*
import android.content.Context

class WorkoutsPresenter(private val context: Context) : WorkoutsContract.Presenter {

    private var view: WorkoutsContract.View? = null
    private val repo = WorkoutsRepository(context)

    private val scope: CoroutineScope = MainScope()

    private var isOperationInProgress = false

    override fun attachView(view: WorkoutsContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        scope.cancel()
    }


    override fun loadAll() {
        view?.showLoading(true)

        scope.launch {
            try {
                val pool = withContext(Dispatchers.IO) {
                    repo.getProvidedPool()
                }

                val userWorkouts = withContext(Dispatchers.IO) {
                    repo.loadUserList()
                }

                view?.showProvidedPool(pool)
                view?.showUserList(userWorkouts)

            } catch (ex: Exception) {
                view?.showMessage("Failed to load workouts: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun addFromPool(id: Long) {
        if (isOperationInProgress) {
            view?.showMessage("Please wait, operation in progress")
            return
        }

        isOperationInProgress = true

        scope.launch {
            try {
                val addedWorkout = withContext(Dispatchers.IO) {
                    repo.addFromPoolById(id)
                }

                if (addedWorkout != null) {
                    view?.showAdded(addedWorkout)
                } else {
                    view?.showMessage("Workout already in your list or not found")
                }
            } catch (ex: Exception) {
                view?.showMessage("Failed to add workout: ${ex.message}")

            } finally {
                isOperationInProgress = false
            }
        }
    }


    override fun removeFromUserList(id: Long) {
        if (isOperationInProgress) {
            view?.showMessage("Please wait, operation in progress")
            return
        }
        isOperationInProgress = true

        scope.launch {
            try {
                val removedWorkout = withContext(Dispatchers.IO) {
                    repo.removeFromUserListById(id)
                }

                if (removedWorkout != null) {
                    view?.showRemoved(removedWorkout)
                } else {
                    view?.showMessage("Workout not found in your list")
                }
            } catch (ex: Exception) {
                view?.showMessage("Failed to remove workout: ${ex.message}")

            } finally {
                isOperationInProgress = false
            }
        }
    }

    override fun onDashboardClicked() {
        view?.navigateToDashboard()
    }

    override fun onProfileClicked() {
        view?.navigateToProfile()
    }

    override fun onPlansClicked() {
        view?.navigateToPlans()
    }
}