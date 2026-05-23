package com.example.flexpath.screens.plans

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context

class PlansPresenter(context: Context) : PlansContract.Presenter {
    private var view: PlansContract.View? = null
    private val repo = PlansRepository(context)
    private val scope: CoroutineScope = MainScope()

    override fun attachView(view: PlansContract.View) {
        this.view = view
    }

    override fun detachView() {
        view = null
        scope.cancel()
    }

    override fun loadPlans() {
        view?.showLoading(true)
        scope.launch {
            try {
                val plans = withContext(Dispatchers.IO) { repo.loadPlans() }
                if (plans.isEmpty()) {
                    view?.showEmptyState()
                } else {
                    view?.showPlans(plans)
                }
            } catch (ex: Exception) {
                view?.showMessage("Unable to load plans: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun createPlan(name: String, description: String?) {
        view?.showLoading(true)
        scope.launch {
            try {
                val plan = withContext(Dispatchers.IO) { repo.createPlan(name, description) }
                if (plan != null) {
                    view?.showMessage("Created plan '${plan.name}'")
                    loadPlans()
                } else {
                    view?.showMessage("Plan name is required and must be unique")
                }
            } catch (ex: Exception) {
                view?.showMessage("Failed to create plan: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun deletePlan(planId: Long) {
        view?.showLoading(true)
        scope.launch {
            try {
                val deleted = withContext(Dispatchers.IO) { repo.deletePlanById(planId) }
                if (deleted) {
                    view?.showMessage("Plan deleted")
                    loadPlans()
                } else {
                    view?.showMessage("Plan not found")
                }
            } catch (ex: Exception) {
                view?.showMessage("Failed to delete plan: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun clonePlan(planId: Long) {
        view?.showLoading(true)
        scope.launch {
            try {
                val clone = withContext(Dispatchers.IO) { repo.clonePlan(planId) }
                if (clone != null) {
                    view?.showMessage("Cloned plan '${clone.name}'")
                    loadPlans()
                } else {
                    view?.showMessage("Plan not found")
                }
            } catch (ex: Exception) {
                view?.showMessage("Failed to clone plan: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun renamePlan(planId: Long, name: String, description: String?) {
        if (name.isBlank()) {
            view?.showMessage("Plan name cannot be empty")
            return
        }
        view?.showLoading(true)
        scope.launch {
            try {
                val plans = withContext(Dispatchers.IO) { repo.loadPlans() }
                val selectedPlan = plans.firstOrNull { it.id == planId }
                if (selectedPlan == null) {
                    view?.showMessage("Plan not found")
                } else {
                    val updatedDescription = description?.trim().takeIf { desc -> !desc.isNullOrBlank() }
                    val updated = selectedPlan.copy(name = name.trim(), description = updatedDescription)
                    val saved = withContext(Dispatchers.IO) { repo.updatePlan(updated) }
                    if (saved) {
                        view?.showMessage("Plan updated")
                        loadPlans()
                    } else {
                        view?.showMessage("Plan name must be unique")
                    }
                }
            } catch (ex: Exception) {
                view?.showMessage("Failed to update plan: ${ex.message}")
            } finally {
                view?.showLoading(false)
            }
        }
    }

    override fun onPlanClicked(planId: Long) {
        view?.navigateToPlanDetails(planId)
    }
}
