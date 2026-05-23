package com.example.flexpath.screens.plans

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.flexpath.data.WorkoutsRepository
import com.example.flexpath.data.dataStore
import com.example.flexpath.screens.workouts.WorkoutItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

class PlansRepository(private val context: Context) {
    companion object {
        private val KEY_PLANS = stringPreferencesKey("user_plans")
    }

    private val workoutsRepository = WorkoutsRepository(context)

    suspend fun loadPlans(): MutableList<WorkoutPlan> = withContext(Dispatchers.IO) {
        val json = context.dataStore.data
            .map { prefs -> prefs[KEY_PLANS] }
            .first()

        if (json.isNullOrBlank()) {
            return@withContext mutableListOf()
        }

        return@withContext try {
            val arr = JSONArray(json)
            val plans = mutableListOf<WorkoutPlan>()
            for (i in 0 until arr.length()) {
                val planJson = arr.getJSONObject(i)
                plans.add(jsonToPlan(planJson))
            }
            plans
        } catch (ex: Exception) {
            mutableListOf()
        }
    }

    suspend fun savePlans(plans: List<WorkoutPlan>) = withContext(Dispatchers.IO) {
        val arr = JSONArray()
        plans.forEach { arr.put(planToJson(it)) }
        val json = arr.toString()
        context.dataStore.edit { prefs -> prefs[KEY_PLANS] = json }
    }

    suspend fun createPlan(name: String, description: String?): WorkoutPlan? {
        if (name.isBlank()) return null

        val plans = loadPlans()
        if (plans.any { it.name.equals(name.trim(), ignoreCase = true) }) return null

        val nextId = (plans.maxOfOrNull { it.id } ?: 10000L) + 1
        val plan = WorkoutPlan(
            id = nextId,
            name = name.trim(),
            description = description?.trim().takeIf { !it.isNullOrBlank() },
            workoutIds = mutableListOf(),
            createdAt = System.currentTimeMillis(),
            lastUsedAt = null
        )
        plans.add(0, plan)
        savePlans(plans)
        return plan
    }

    suspend fun updatePlan(updatedPlan: WorkoutPlan): Boolean {
        if (updatedPlan.name.isBlank()) return false

        val plans = loadPlans()
        val existing = plans.indexOfFirst { it.id == updatedPlan.id }
        if (existing < 0) return false

        if (plans.any { it.id != updatedPlan.id && it.name.equals(updatedPlan.name, ignoreCase = true) }) {
            return false
        }

        plans[existing] = updatedPlan
        savePlans(plans)
        return true
    }

    suspend fun touchPlan(planId: Long): Boolean {
        val plans = loadPlans()
        val index = plans.indexOfFirst { it.id == planId }
        if (index < 0) return false
        val plan = plans[index]
        plans[index] = plan.copy(lastUsedAt = System.currentTimeMillis())
        savePlans(plans)
        return true
    }

    suspend fun deletePlanById(planId: Long): Boolean {
        val plans = loadPlans()
        val index = plans.indexOfFirst { it.id == planId }
        if (index < 0) return false
        plans.removeAt(index)
        savePlans(plans)
        return true
    }

    suspend fun clonePlan(planId: Long): WorkoutPlan? {
        val plans = loadPlans()
        val source = plans.firstOrNull { it.id == planId } ?: return null
        val cloneName = buildString {
            append(source.name)
            append(" Copy")
            var suffix = 1
            while (plans.any { it.name.equals(toString(), ignoreCase = true) }) {
                suffix++
                setLength(0)
                append(source.name)
                append(" Copy")
                append(suffix)
            }
        }
        val nextId = (plans.maxOfOrNull { it.id } ?: 10000L) + 1
        val clone = WorkoutPlan(
            id = nextId,
            name = cloneName,
            description = source.description,
            workoutIds = source.workoutIds.toMutableList(),
            createdAt = System.currentTimeMillis(),
            lastUsedAt = source.lastUsedAt
        )
        plans.add(0, clone)
        savePlans(plans)
        return clone
    }

    suspend fun addWorkoutToPlan(planId: Long, workoutId: Long): Boolean {
        val plans = loadPlans()
        val index = plans.indexOfFirst { it.id == planId }
        if (index < 0) return false
        val plan = plans[index]
        if (plan.workoutIds.contains(workoutId)) return false
        plan.workoutIds.add(workoutId)
        plans[index] = plan.copy(workoutIds = plan.workoutIds)
        savePlans(plans)
        return true
    }

    suspend fun removeWorkoutFromPlan(planId: Long, workoutId: Long): Boolean {
        val plans = loadPlans()
        val index = plans.indexOfFirst { it.id == planId }
        if (index < 0) return false
        val plan = plans[index]
        if (!plan.workoutIds.remove(workoutId)) return false
        plans[index] = plan.copy(workoutIds = plan.workoutIds)
        savePlans(plans)
        return true
    }

    suspend fun reorderWorkoutInPlan(planId: Long, fromIndex: Int, toIndex: Int): Boolean {
        val plans = loadPlans()
        val index = plans.indexOfFirst { it.id == planId }
        if (index < 0) return false
        val plan = plans[index]
        if (fromIndex < 0 || toIndex < 0 || fromIndex >= plan.workoutIds.size || toIndex >= plan.workoutIds.size) return false
        val ids = plan.workoutIds.toMutableList()
        val item = ids.removeAt(fromIndex)
        ids.add(toIndex, item)
        plans[index] = plan.copy(workoutIds = ids)
        savePlans(plans)
        return true
    }

    suspend fun findPlanById(planId: Long): WorkoutPlan? = loadPlans().firstOrNull { it.id == planId }

    suspend fun getWorkoutById(workoutId: Long): WorkoutItem? = workoutsRepository.getWorkoutById(workoutId)

    private fun planToJson(plan: WorkoutPlan): JSONObject {
        val obj = JSONObject()
        obj.put("id", plan.id)
        obj.put("name", plan.name)
        obj.put("description", plan.description ?: JSONObject.NULL)
        obj.put("createdAt", plan.createdAt)
        obj.put("lastUsedAt", plan.lastUsedAt ?: JSONObject.NULL)
        val workouts = JSONArray()
        plan.workoutIds.forEach { workouts.put(it) }
        obj.put("workoutIds", workouts)
        return obj
    }

    private fun jsonToPlan(obj: JSONObject): WorkoutPlan {
        val id = obj.getLong("id")
        val name = obj.optString("name", "Unnamed Plan")
        val description = if (obj.has("description") && !obj.isNull("description")) obj.getString("description") else null
        val createdAt = obj.optLong("createdAt", System.currentTimeMillis())
        val lastUsedAt = if (obj.has("lastUsedAt") && !obj.isNull("lastUsedAt")) obj.getLong("lastUsedAt") else null
        val workoutIds = mutableListOf<Long>()
        if (obj.has("workoutIds") && !obj.isNull("workoutIds")) {
            val arr = obj.getJSONArray("workoutIds")
            for (i in 0 until arr.length()) {
                workoutIds.add(arr.optLong(i))
            }
        }
        return WorkoutPlan(id, name, description, workoutIds, createdAt, lastUsedAt)
    }
}
