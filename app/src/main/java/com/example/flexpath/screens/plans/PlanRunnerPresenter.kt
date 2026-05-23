package com.example.flexpath.screens.plans

import com.example.flexpath.screens.workouts.Difficulty
import com.example.flexpath.screens.workouts.Equipment
import com.example.flexpath.screens.workouts.MuscleGroup
import com.example.flexpath.screens.workouts.WorkoutItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context

class PlanRunnerPresenter(context: Context) : PlanRunnerContract.Presenter {
    private var view: PlanRunnerContract.View? = null
    private val repo = PlansRepository(context)
    private val scope: CoroutineScope = MainScope()
    private var currentIndex = 0
    private var workouts: List<WorkoutItem> = listOf()
    private var planName: String = ""

    override fun attachView(view: PlanRunnerContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        scope.cancel()
    }

    override fun loadSession(planId: Long) {
        view?.showLoading(true)
        scope.launch {
            try {
                val plan = withContext(Dispatchers.IO) { repo.findPlanById(planId) }
                if (plan == null) {
                    view?.showMessage("Plan not found")
                    view?.showFinished()
                    return@launch
                }
                planName = plan.name
                workouts = withContext(Dispatchers.IO) {
                    plan.workoutIds.map { id -> repo.getWorkoutById(id) ?: createMissingWorkoutPlaceholder(id) }
                }
                withContext(Dispatchers.IO) { repo.touchPlan(planId) }
                if (workouts.isEmpty()) {
                    view?.showMessage("No workouts in this plan")
                    view?.showFinished()
                } else {
                    currentIndex = 0
                    view?.showSession(planName, workouts[currentIndex], currentIndex + 1, workouts.size)
                }
            } catch (ex: Exception) {
                view?.showMessage("Unable to start plan: ${ex.message}")
                view?.showFinished()
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun completeWorkout() {
        if (workouts.isEmpty()) return
        currentIndex++
        if (currentIndex >= workouts.size) {
            view?.showFinished()
        } else {
            view?.showSession(planName, workouts[currentIndex], currentIndex + 1, workouts.size)
        }
    }

    override fun skipWorkout() {
        if (workouts.isEmpty()) return
        currentIndex++
        if (currentIndex >= workouts.size) {
            view?.showFinished()
        } else {
            view?.showSession(planName, workouts[currentIndex], currentIndex + 1, workouts.size)
        }
    }

    private fun createMissingWorkoutPlaceholder(id: Long): WorkoutItem {
        return WorkoutItem(
            id = id,
            title = "Missing workout",
            primaryMuscle = MuscleGroup.CORE,
            secondaryMuscles = emptyList(),
            equipment = Equipment.BODYWEIGHT,
            difficulty = Difficulty.BEGINNER,
            description = "This workout was removed from the pool."
        )
    }
}
