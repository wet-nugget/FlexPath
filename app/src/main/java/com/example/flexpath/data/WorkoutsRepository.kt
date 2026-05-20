package com.example.flexpath.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.flexpath.screens.workouts.Difficulty
import com.example.flexpath.screens.workouts.Equipment
import com.example.flexpath.screens.workouts.MuscleGroup
import com.example.flexpath.screens.workouts.WorkoutItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class WorkoutsRepository(private val context: Context) {
    companion object {
        private val KEY_USER_LIST = stringPreferencesKey("workouts_user_list")
    }

    private val pool: List<WorkoutItem> = listOf(
        WorkoutItem(
            id = 1001L,
            title = "Dumbbell Chest Press",
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            equipment = Equipment.DUMBBELL,
            difficulty = Difficulty.INTERMEDIATE,
            description = "Flat bench dumbbell press, controlled descent, full range."
        ),
        WorkoutItem(
            id = 1002L,
            title = "Machine Chest Press",
            primaryMuscle = MuscleGroup.CHEST,
            secondaryMuscles = listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),
            equipment = Equipment.MACHINE,
            difficulty = Difficulty.BEGINNER,
            description = "Seated machine press for stable chest isolation."
        ),
        WorkoutItem(
            id = 1003L,
            title = "Barbell Deadlift",
            primaryMuscle = MuscleGroup.BACK,
            secondaryMuscles = listOf(MuscleGroup.GLUTES, MuscleGroup.HAMSTRINGS),
            equipment = Equipment.BARBELL,
            difficulty = Difficulty.ADVANCED,
            description = "Conventional deadlift, keep a neutral spine and drive through heels."
        ),
        WorkoutItem(
            id = 1004L,
            title = "Dumbbell Bicep Curls",
            primaryMuscle = MuscleGroup.BICEPS,
            secondaryMuscles = emptyList(),
            equipment = Equipment.DUMBBELL,
            difficulty = Difficulty.BEGINNER,
            description = "Alternating curls with full contraction and controlled lowering."
        ),
        WorkoutItem(
            id = 1005L,
            title = "Barbell Back Squat",
            primaryMuscle = MuscleGroup.LEGS,
            secondaryMuscles = listOf(MuscleGroup.QUADS, MuscleGroup.GLUTES),
            equipment = Equipment.BARBELL,
            difficulty = Difficulty.INTERMEDIATE,
            description = "Back squat, depth as mobility allows, chest up."
        ),
        WorkoutItem(
            id = 1006L,
            title = "Romanian Deadlift",
            primaryMuscle = MuscleGroup.HAMSTRINGS,
            secondaryMuscles = listOf(MuscleGroup.GLUTES),
            equipment = Equipment.BARBELL,
            difficulty = Difficulty.INTERMEDIATE,
            description = "RDL for posterior chain, hinge at hips with slight knee bend."
        ),
        WorkoutItem(
            id = 1007L,
            title = "Overhead Dumbbell Press",
            primaryMuscle = MuscleGroup.SHOULDERS,
            secondaryMuscles = listOf(MuscleGroup.TRICEPS),
            equipment = Equipment.DUMBBELL,
            difficulty = Difficulty.INTERMEDIATE,
            description = "Seated or standing press, control the eccentric phase."
        ),
        WorkoutItem(
            id = 1008L,
            title = "Cable Triceps Pushdown",
            primaryMuscle = MuscleGroup.TRICEPS,
            secondaryMuscles = emptyList(),
            equipment = Equipment.CABLE,
            difficulty = Difficulty.BEGINNER,
            description = "Use rope or bar, keep elbows pinned to sides."
        ),
        WorkoutItem(
            id = 1009L,
            title = "Kettlebell Swing",
            primaryMuscle = MuscleGroup.GLUTES,
            secondaryMuscles = listOf(MuscleGroup.HAMSTRINGS, MuscleGroup.CORE),
            equipment = Equipment.KETTLEBELL,
            difficulty = Difficulty.INTERMEDIATE,
            description = "Hip hinge explosive swing, avoid excessive knee bend."
        ),
        WorkoutItem(
            id = 1010L,
            title = "Plank",
            primaryMuscle = MuscleGroup.CORE,
            secondaryMuscles = emptyList(),
            equipment = Equipment.BODYWEIGHT,
            difficulty = Difficulty.BEGINNER,
            description = "Isometric core hold, maintain a straight line from head to heels."
        )
    )

    
    fun getProvidedPool(): List<WorkoutItem> = pool

    
    suspend fun loadUserList(): MutableList<WorkoutItem> = withContext(Dispatchers.IO) {
        val json = context.dataStore.data
            .map { prefs -> prefs[KEY_USER_LIST] }
            .first()

        if (json.isNullOrBlank()) {
            return@withContext mutableListOf()
        }

        return@withContext try {
            val arr = JSONArray(json)
            val workoutList = mutableListOf<WorkoutItem>()

            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                workoutList.add(jsonToWorkout(obj))
            }
            workoutList
        } catch (ex: Exception) {
            mutableListOf()
        }
    }

    
    suspend fun saveUserList(list: List<WorkoutItem>) = withContext(Dispatchers.IO) {
        val arr = JSONArray()
        list.forEach { item -> arr.put(workoutToJson(item)) }

        val json = arr.toString()

        context.dataStore.edit { prefs ->
            prefs[KEY_USER_LIST] = json
        }
    }

    
    suspend fun addFromPoolById(id: Long): WorkoutItem? = withContext(Dispatchers.IO) {

        val userList = loadUserList()


        if (userList.any { it.id == id }) {
            return@withContext null
        }


        val item = pool.firstOrNull { it.id == id }
        if (item == null) {
            return@withContext null
        }


        userList.add(0, item)


        saveUserList(userList)

        return@withContext item
    }

    
    suspend fun removeFromUserListById(id: Long): WorkoutItem? = withContext(Dispatchers.IO) {

        val userList = loadUserList()


        val idx = userList.indexOfFirst { it.id == id }
        if (idx < 0) {
            return@withContext null
        }

        val removed = userList.removeAt(idx)


        saveUserList(userList)

        return@withContext removed
    }

    
    suspend fun clearUserList() = withContext(Dispatchers.IO) {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_USER_LIST)
        }
    }


    
    private fun workoutToJson(item: WorkoutItem): JSONObject {
        val obj = JSONObject()
        obj.put("id", item.id)
        obj.put("title", item.title)
        obj.put("primary", item.primaryMuscle.name)

        val secArr = JSONArray()
        item.secondaryMuscles.forEach { secArr.put(it.name) }
        obj.put("secondaries", secArr)

        obj.put("equipment", item.equipment.name)
        obj.put("difficulty", item.difficulty.name)
        obj.put("description", item.description)
        return obj
    }

    
    private fun jsonToWorkout(obj: JSONObject): WorkoutItem {
        val id = obj.getLong("id")
        val title = obj.getString("title")
        val primary = MuscleGroup.valueOf(obj.getString("primary"))

        val secondaries = mutableListOf<MuscleGroup>()
        if (obj.has("secondaries") && !obj.isNull("secondaries")) {
            val secArr = obj.getJSONArray("secondaries")
            for (j in 0 until secArr.length()) {
                secondaries.add(MuscleGroup.valueOf(secArr.getString(j)))
            }
        }

        val equipment = Equipment.valueOf(obj.getString("equipment"))
        val difficulty = Difficulty.valueOf(obj.getString("difficulty"))
        val description = if (obj.has("description") && !obj.isNull("description")) {
            obj.getString("description")
        } else {
            null
        }

        return WorkoutItem(id, title, primary, secondaries, equipment, difficulty, description)
    }
}