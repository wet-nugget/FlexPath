package com.example.flexpath.screens.plans

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.flexpath.R

class PlanRunnerActivity : Activity(), PlanRunnerContract.View {

    private lateinit var presenter: PlanRunnerContract.Presenter
    private lateinit var textPlanName: TextView
    private lateinit var textProgress: TextView
    private lateinit var textWorkoutTitle: TextView
    private lateinit var textWorkoutDetails: TextView
    private lateinit var textWorkoutDescription: TextView
    private lateinit var buttonSkipWorkout: TextView
    private lateinit var buttonCompleteWorkout: TextView
    private lateinit var progressBar: ProgressBar

    private var planId: Long = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_runner)

        presenter = PlanRunnerPresenter(this)
        presenter.attachView(this)

        planId = intent.getLongExtra("plan_id", -1L)
        if (planId < 0) {
            finish()
            return
        }

        textPlanName = findViewById(R.id.textviewRunnerPlanName)
        textProgress = findViewById(R.id.textviewRunnerProgress)
        textWorkoutTitle = findViewById(R.id.textviewRunnerWorkoutTitle)
        textWorkoutDetails = findViewById(R.id.textviewRunnerWorkoutDetails)
        textWorkoutDescription = findViewById(R.id.textviewRunnerWorkoutDescription)
        buttonSkipWorkout = findViewById(R.id.buttonSkipWorkout)
        buttonCompleteWorkout = findViewById(R.id.buttonCompleteWorkout)
        progressBar = findViewById(R.id.progressBarLoading)

        buttonSkipWorkout.setOnClickListener { presenter.skipWorkout() }
        buttonCompleteWorkout.setOnClickListener { presenter.completeWorkout() }

        presenter.loadSession(planId)
    }

    override fun showSession(planName: String, workout: com.example.flexpath.screens.workouts.WorkoutItem, currentIndex: Int, total: Int) {
        runOnUiThread {
            textPlanName.text = planName
            textProgress.text = "Workout $currentIndex of $total"
            textWorkoutTitle.text = workout.title
            textWorkoutDetails.text = "${workout.primaryMuscle} â€¢ ${workout.equipment} â€¢ ${workout.difficulty}"
            textWorkoutDescription.text = workout.description ?: "No description available."
        }
    }

    override fun showFinished() {
        runOnUiThread {
            Toast.makeText(this, "Plan complete!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showLoading(show: Boolean) {
        runOnUiThread {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            buttonSkipWorkout.isEnabled = !show
            buttonCompleteWorkout.isEnabled = !show
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
