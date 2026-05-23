package com.example.flexpath.screens.plans

import android.content.Context
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

class PlanDetailPresenter(context: Context) : PlanDetailContract.Presenter {
    private var view: PlanDetailContract.View? = null
    private val repo = PlansRepository(context)
    private val scope: CoroutineScope = MainScope()

    override fun attachView(view: PlanDetailContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        scope.cancel()
    }

    override fun loadPlan(planId: Long) {
        view?.showLoading(true)
        scope.launch {
            try {
                val plan = withContext(Dispatchers.IO) { repo.findPlanById(planId) }
                if (plan == null) {
                    view?.showMessage("Plan not found")
                    view?.showEmptyPlan()
                    return@launch
                }
                val workouts = withContext(Dispatchers.IO) {
                    plan.workoutIds.map { id -> repo.getWorkoutById(id) ?: createMissingWorkoutPlaceholder(id) }
                }
                view?.showPlan(plan, workouts)
            } catch (ex: Exception) {
                view?.showMessage("Unable to load plan: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun addWorkoutToPlan(planId: Long, workoutId: Long) {
        view?.showLoading(true)
        scope.launch {
            try {
                val added = withContext(Dispatchers.IO) { repo.addWorkoutToPlan(planId, workoutId) }
                if (added) {
                    loadPlan(planId)
                    view?.showMessage("Workout added to plan")
                } else {
                    view?.showMessage("Workout already exists in this plan")
                }
            } catch (ex: Exception) {
                view?.showMessage("Unable to add workout: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun removeWorkoutFromPlan(planId: Long, workoutId: Long) {
        view?.showLoading(true)
        scope.launch {
            try {
                val removed = withContext(Dispatchers.IO) { repo.removeWorkoutFromPlan(planId, workoutId) }
                if (removed) {
                    loadPlan(planId)
                    view?.showMessage("Workout removed")
                } else {
                    view?.showMessage("Workout not found in plan")
                }
            } catch (ex: Exception) {
                view?.showMessage("Unable to remove workout: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun reorderWorkout(planId: Long, fromPosition: Int, toPosition: Int) {
        scope.launch {
            try {
                withContext(Dispatchers.IO) { repo.reorderWorkoutInPlan(planId, fromPosition, toPosition) }
            } catch (ex: Exception) {
                view?.showMessage("Unable to reorder workout: ${ex.message}")
            }
        }
    }

    override fun updatePlan(planId: Long, name: String, description: String?) {
        view?.showLoading(true)
        scope.launch {
            try {
                val plan = withContext(Dispatchers.IO) { repo.findPlanById(planId) }
                if (plan == null) {
                    view?.showMessage("Plan not found")
                } else {
                    val updated = plan.copy(name = name.trim(), description = description?.trim().takeIf { !it.isNullOrBlank() })
                    val saved = withContext(Dispatchers.IO) { repo.updatePlan(updated) }
                    if (saved) {
                        loadPlan(planId)
                        view?.showMessage("Plan updated")
                    } else {
                        view?.showMessage("Plan name must be unique")
                    }
                }
            } catch (ex: Exception) {
                view?.showMessage("Unable to update plan: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun startPlan(planId: Long) {
        view?.navigateToRunner(planId)
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
