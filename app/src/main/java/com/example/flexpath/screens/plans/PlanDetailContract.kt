package com.example.flexpath.screens.plans

import com.example.flexpath.screens.workouts.WorkoutItem

interface PlanDetailContract {
    interface View {
        fun showPlan(plan: WorkoutPlan, workouts: List<WorkoutItem>)
        fun showEmptyPlan()
        fun showMessage(message: String)
        fun showLoading(show: Boolean)
        fun navigateToRunner(planId: Long)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPlan(planId: Long)
        fun addWorkoutToPlan(planId: Long, workoutId: Long)
        fun removeWorkoutFromPlan(planId: Long, workoutId: Long)
        fun reorderWorkout(planId: Long, fromPosition: Int, toPosition: Int)
        fun updatePlan(planId: Long, name: String, description: String?)
        fun startPlan(planId: Long)
    }
}
