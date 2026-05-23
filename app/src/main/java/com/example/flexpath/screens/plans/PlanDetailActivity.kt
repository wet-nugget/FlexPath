package com.example.flexpath.screens.plans

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flexpath.R
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.screens.profile.ProfileActivity
import com.example.flexpath.screens.workouts.WorkoutsActivity
import com.example.flexpath.data.WorkoutsRepository
import com.example.flexpath.ui.setEnabledRecursive
import com.google.android.material.snackbar.Snackbar

class PlanDetailActivity : Activity(), PlanDetailContract.View {

    private lateinit var presenter: PlanDetailContract.Presenter
    private lateinit var recyclerWorkouts: RecyclerView
    private lateinit var adapter: PlanDetailAdapter
    private lateinit var titleView: TextView
    private lateinit var descriptionView: TextView
    private lateinit var buttonStartPlan: TextView
    private lateinit var buttonAddWorkout: TextView
    private lateinit var buttonEditPlan: TextView
    private lateinit var emptyPlaceholder: View
    private lateinit var progressBar: ProgressBar
    private lateinit var rootContainer: ViewGroup
    private lateinit var iconHome: ImageView
    private lateinit var iconWorkouts: ImageView
    private lateinit var iconPlans: ImageView
    private lateinit var iconProfile: ImageView

    private var currentPlanId: Long = -1L
    private var currentPlan: WorkoutPlan? = null
    private var lastRemovedWorkout: Pair<Long, Int>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plan_detail)

        presenter = PlanDetailPresenter(this)
        presenter.attachView(this)

        currentPlanId = intent.getLongExtra("plan_id", -1L)
        if (currentPlanId < 0) {
            Toast.makeText(this, "Plan not found", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, PlansActivity::class.java))
            finish()
            return
        }

        titleView = findViewById(R.id.textviewPlanTitle)
        descriptionView = findViewById(R.id.textviewPlanDescription)
        buttonStartPlan = findViewById(R.id.buttonStartPlan)
        buttonAddWorkout = findViewById(R.id.buttonAddWorkout)
        buttonEditPlan = findViewById(R.id.buttonEditPlan)
        recyclerWorkouts = findViewById(R.id.recyclerPlanWorkouts)
        emptyPlaceholder = findViewById(R.id.viewEmptyPlaceholder)
        progressBar = findViewById(R.id.progressBarLoading)
        rootContainer = findViewById(R.id.planDetailRoot)
        iconHome = findViewById(R.id.iconHome)
        iconWorkouts = findViewById(R.id.iconWorkouts)
        iconPlans = findViewById(R.id.iconPlans)
        iconProfile = findViewById(R.id.iconProfile)

        adapter = PlanDetailAdapter()
        recyclerWorkouts.layoutManager = LinearLayoutManager(this)
        recyclerWorkouts.adapter = adapter

        buttonStartPlan.setOnClickListener { presenter.startPlan(currentPlanId) }
        buttonAddWorkout.setOnClickListener { showWorkoutChooser() }
        buttonEditPlan.setOnClickListener { showPlanEditor() }

        iconHome.setOnClickListener { startActivity(Intent(this, DashboardActivity::class.java)) }
        iconWorkouts.setOnClickListener { startActivity(Intent(this, WorkoutsActivity::class.java)) }
        iconPlans.setOnClickListener { startActivity(Intent(this, PlansActivity::class.java)) }
        iconProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }

        adapter.setOnWorkoutClickListener { workout -> showWorkoutPreview(workout) }
        adapter.setOnRemoveClickListener { workout, position -> confirmRemoveWorkout(workout, position) }
        setupDragAndDrop()

        try {
            presenter.loadPlan(currentPlanId)
        } catch (ex: Exception) {
            Toast.makeText(this, "Unable to open plan: ${ex.message}", Toast.LENGTH_LONG).show()
            startActivity(Intent(this, PlansActivity::class.java))
            finish()
        }
    }

    private fun setupDragAndDrop() {
        val callback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val from = viewHolder.bindingAdapterPosition
                val to = target.bindingAdapterPosition
                if (from == RecyclerView.NO_POSITION || to == RecyclerView.NO_POSITION) return false
                adapter.moveItem(from, to)
                presenter.reorderWorkout(currentPlanId, from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // no swipe actions
            }
        }
        ItemTouchHelper(callback).attachToRecyclerView(recyclerWorkouts)
    }

    private fun showWorkoutChooser() {
        val pool = WorkoutsRepository(this).getProvidedPool()
        val items = pool.map { it.title }.toTypedArray()
        if (items.isEmpty()) {
            showMessage("No available workouts to add")
            return
        }
        AlertDialog.Builder(this)
            .setTitle("Add workout to plan")
            .setItems(items) { _, which ->
                val workout = pool[which]
                if (currentPlan?.workoutIds?.contains(workout.id) == true) {
                    showMessage("This workout is already in the plan")
                } else {
                    presenter.addWorkoutToPlan(currentPlanId, workout.id)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPlanEditor() {
        if (currentPlan == null) return
        val dialogView = layoutInflater.inflate(R.layout.dialog_plan_edit, null)
        val title = dialogView.findViewById<TextView>(R.id.textDialogTitle)
        val nameField = dialogView.findViewById<TextView>(R.id.editTextPlanName)
        val descriptionField = dialogView.findViewById<TextView>(R.id.editTextPlanDescription)

        title.text = "Edit plan"
        nameField.text = currentPlan?.name
        descriptionField.text = currentPlan?.description ?: ""

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)
            .create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val planName = nameField.text.toString().trim()
                val planDescr = descriptionField.text.toString().trim().takeIf { it.isNotBlank() }
                if (planName.isBlank()) {
                    nameField.error = "Name required"
                    return@setOnClickListener
                }
                presenter.updatePlan(currentPlanId, planName, planDescr)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun confirmRemoveWorkout(workout: com.example.flexpath.screens.workouts.WorkoutItem, position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Remove workout")
            .setMessage("Remove \"${workout.title}\" from this plan?")
            .setPositiveButton("Remove") { _, _ ->
                lastRemovedWorkout = workout.id to position
                presenter.removeWorkoutFromPlan(currentPlanId, workout.id)
                Snackbar.make(rootContainer, "Removed ${workout.title}", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        lastRemovedWorkout?.let { (id, _) -> presenter.addWorkoutToPlan(currentPlanId, id) }
                    }
                    .show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showWorkoutPreview(workout: com.example.flexpath.screens.workouts.WorkoutItem) {
        AlertDialog.Builder(this)
            .setTitle(workout.title)
            .setMessage(workout.description ?: "No details available")
            .setPositiveButton("OK", null)
            .show()
    }

    override fun showPlan(plan: WorkoutPlan, workouts: List<com.example.flexpath.screens.workouts.WorkoutItem>) {
        runOnUiThread {
            currentPlan = plan
            titleView.text = plan.name
            descriptionView.text = plan.description ?: ""
            emptyPlaceholder.visibility = if (workouts.isEmpty()) View.VISIBLE else View.GONE
            recyclerWorkouts.visibility = if (workouts.isEmpty()) View.GONE else View.VISIBLE
            adapter.updateList(workouts)
        }
    }

    override fun showEmptyPlan() {
        runOnUiThread {
            titleView.text = "Plan not found"
            descriptionView.text = ""
            emptyPlaceholder.visibility = View.VISIBLE
            recyclerWorkouts.visibility = View.GONE
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
            rootContainer.setEnabledRecursive(!show)
            buttonStartPlan.isEnabled = !show
            buttonAddWorkout.isEnabled = !show
            buttonEditPlan.isEnabled = !show
            iconHome.isEnabled = !show
            iconWorkouts.isEnabled = !show
            iconPlans.isEnabled = !show
            iconProfile.isEnabled = !show
        }
    }

    override fun navigateToRunner(planId: Long) {
        runOnUiThread {
            val intent = Intent(this, PlanRunnerActivity::class.java)
            intent.putExtra("plan_id", planId)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
