package com.example.flexpath.screens.plans

interface PlansContract {
    interface View {
        fun showPlans(plans: List<WorkoutPlan>)
        fun showEmptyState()
        fun showMessage(message: String)
        fun showLoading(show: Boolean)
        fun navigateToPlanDetails(planId: Long)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPlans()
        fun createPlan(name: String, description: String?)
        fun deletePlan(planId: Long)
        fun clonePlan(planId: Long)
        fun renamePlan(planId: Long, name: String, description: String?)
        fun onPlanClicked(planId: Long)
    }
}
