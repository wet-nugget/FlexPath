package com.example.flexpath.screens.workouts

data class WorkoutItem(
    val id: Long,
    val title: String,
    val primaryMuscle: MuscleGroup,
    val secondaryMuscles: List<MuscleGroup> = emptyList(),
    val equipment: Equipment = Equipment.BODYWEIGHT,
    val difficulty: Difficulty = Difficulty.BEGINNER,
    val description: String? = null
)

enum class MuscleGroup {
    CHEST, BACK, SHOULDERS, BICEPS, TRICEPS, LEGS, GLUTES, HAMSTRINGS, QUADS, CORE
}

enum class Equipment {
    DUMBBELL, BARBELL, MACHINE, CABLE, BODYWEIGHT, KETTLEBELL
}

enum class Difficulty {
    BEGINNER, INTERMEDIATE, ADVANCED
}
