package com.example.flexpath.screens.workouts

interface WorkoutsContract {
    interface View {

        fun showProvidedPool(pool: List<WorkoutItem>)
        fun showUserList(list: List<WorkoutItem>)
        fun showAdded(item: WorkoutItem)
        fun showRemoved(item: WorkoutItem)
        fun showMessage(message: String)
        fun showLoading(show: Boolean)
        fun navigateToDashboard()
        fun navigateToProfile()
        fun navigateToPlans()
    }
    interface Presenter {

        fun attachView(view: View)
        fun detachView()
        fun loadAll()
        fun addFromPool(id: Long)
        fun removeFromUserList(id: Long)
        fun onDashboardClicked()
        fun onProfileClicked()
        fun onPlansClicked()
    }
}