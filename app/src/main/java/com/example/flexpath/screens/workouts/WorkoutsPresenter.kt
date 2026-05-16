package com.example.flexpath.screens.workouts

import android.content.Context
import com.example.flexpath.data.WorkoutsRepository

class WorkoutsPresenter(context: Context) : WorkoutsContract.Presenter {

    private var view: WorkoutsContract.View? = null
    private val repo = WorkoutsRepository(context)

    override fun attachView(view: WorkoutsContract.View) {
        this.view = view
    }

    override fun detachView() {
        this.view = null
    }

    override fun loadAll() {
        view?.showLoading(true)
        val pool = repo.getProvidedPool()
        val userList = repo.loadUserList()
        view?.showProvidedPool(pool)
        view?.showUserList(userList)
        view?.showLoading(false)
    }

    override fun addFromPool(id: Long) {
        val added = repo.addFromPoolById(id)
        if (added != null) {
            view?.showAdded(added)
        } else {
            view?.showMessage("Could not add item (already added or not found)")
        }
    }

    override fun removeFromUserList(id: Long) {
        val removed = repo.removeFromUserListById(id)
        if (removed != null) {
            view?.showRemoved(removed)
        } else {
            view?.showMessage("Item not found in your list")
        }
    }

    override fun onUserItemClicked(item: WorkoutItem) {
        view?.showMessage("${item.title} — ${item.primaryMuscle}")
    }

    override fun onPoolItemClicked(item: WorkoutItem) {
        view?.showMessage("Tap Add to include \"${item.title}\" in your list")
    }
}
