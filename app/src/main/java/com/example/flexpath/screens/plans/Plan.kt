package com.example.flexpath.screens.plans

import android.text.format.DateFormat

data class WorkoutPlan(
    val id: Long,
    val name: String,
    val description: String? = null,
    val workoutIds: MutableList<Long> = mutableListOf(),
    val createdAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long? = null
) {
    val workoutCount: Int
        get() = workoutIds.size

    val estimatedDurationMinutes: Int
        get() = workoutCount * 12

    fun displaySubtitle(): String {
        val countText = if (workoutCount == 1) "1 workout" else "$workoutCount workouts"
        val durationText = "$estimatedDurationMinutes min"
        val lastUsedText = if (lastUsedAt != null) {
            val formattedDate = DateFormat.format("MMM d", lastUsedAt)
            "Last used $formattedDate"
        } else {
            "Never used"
        }
        return "$countText \u2022 $durationText \u2022 $lastUsedText"
    }
}
