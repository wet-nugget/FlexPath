package com.example.flexpath.screens.plans

import com.example.flexpath.screens.workouts.WorkoutItem

interface PlanRunnerContract {
    interface View {
        fun showSession(planName: String, workout: WorkoutItem, currentIndex: Int, total: Int)
        fun showFinished()
        fun showMessage(message: String)
        fun showLoading(show: Boolean)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadSession(planId: Long)
        fun completeWorkout()
        fun skipWorkout()
    }
}
