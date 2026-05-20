package com.example.flexpath.screens.workouts

import com.example.flexpath.data.WorkoutsRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import android.content.Context

class WorkoutsPresenter(private val context: Context) : WorkoutsContract.Presenter {
    private var view: WorkoutsContract.View? = null
    private val repo = WorkoutsRepository(context)
    private val scope: CoroutineScope = MainScope()
    private val operationLock = Mutex()

    override fun attachView(view: WorkoutsContract.View) { this.view = view }
    override fun detachView() {
        view = null
        scope.cancel()
    }

    override fun loadAll() {
        view?.showLoading(true)
        scope.launch {
            try {
                val pool = withContext(Dispatchers.IO) { repo.getProvidedPool() }
                val user = withContext(Dispatchers.IO) { repo.getUserList() }
                view?.showProvidedPool(pool)
                view?.showUserList(user)
            } catch (ex: Exception) {
                view?.showMessage("Failed to load workouts: ${ex.message ?: "unknown"}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun addFromPool(id: String) {
        scope.launch {
            if (!operationLock.tryLock()) {
                view?.showMessage("Operation already in progress")
                return@launch
            }
            try {
                val item = withContext(Dispatchers.IO) { repo.findInPool(id) }
                if (item == null) {
                    view?.showMessage("Workout not found")
                    return@launch
                }
                val added = withContext(Dispatchers.IO) { repo.addToUserList(item) }
                if (added) view?.showAdded(item) else view?.showMessage("Already in your list")
            } catch (ex: Exception) {
                view?.showMessage("Failed to add workout: ${ex.message ?: "unknown"}")
            } finally {
                operationLock.unlock()
            }
        }
    }

    override fun removeFromUserList(id: String) {
        scope.launch {
            if (!operationLock.tryLock()) {
                view?.showMessage("Operation already in progress")
                return@launch
            }
            try {
                val item = withContext(Dispatchers.IO) { repo.findInUserList(id) }
                if (item == null) {
                    view?.showMessage("Workout not found in your list")
                    return@launch
                }
                val removed = withContext(Dispatchers.IO) { repo.removeFromUserList(id) }
                if (removed) view?.showRemoved(item) else view?.showMessage("Failed to remove workout")
            } catch (ex: Exception) {
                view?.showMessage("Failed to remove workout: ${ex.message ?: "unknown"}")
            } finally {
                operationLock.unlock()
            }
        }
    }
}
