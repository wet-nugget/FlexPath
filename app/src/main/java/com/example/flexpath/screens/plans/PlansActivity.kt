package com.example.flexpath.screens.plans

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flexpath.R
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.screens.profile.ProfileActivity
import com.example.flexpath.screens.workouts.WorkoutsActivity
import com.example.flexpath.ui.setEnabledRecursive

class PlansActivity : Activity(), PlansContract.View {

    private lateinit var presenter: PlansContract.Presenter
    private lateinit var recyclerPlans: RecyclerView
    private lateinit var adapter: PlanAdapter
    private lateinit var buttonCreatePlan: TextView
    private lateinit var layoutEmptyState: View
    private lateinit var progressBar: ProgressBar
    private lateinit var rootContainer: ViewGroup
    private lateinit var iconHome: ImageView
    private lateinit var iconWorkouts: ImageView
    private lateinit var iconPlans: ImageView
    private lateinit var iconProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plans)

        presenter = PlansPresenter(this)
        presenter.attachView(this)

        recyclerPlans = findViewById(R.id.recyclerPlans)
        buttonCreatePlan = findViewById(R.id.buttonCreatePlan)
        layoutEmptyState = findViewById(R.id.layoutEmptyState)
        progressBar = findViewById(R.id.progressBarLoading)
        rootContainer = findViewById(R.id.plansRoot)

        iconHome = findViewById(R.id.iconHome)
        iconWorkouts = findViewById(R.id.iconWorkouts)
        iconPlans = findViewById(R.id.iconPlans)
        iconProfile = findViewById(R.id.iconProfile)

        adapter = PlanAdapter()
        recyclerPlans.layoutManager = LinearLayoutManager(this)
        recyclerPlans.adapter = adapter

        buttonCreatePlan.setOnClickListener { showPlanEditor(null) }

        iconHome.setOnClickListener { startActivity(Intent(this, DashboardActivity::class.java)) }
        iconWorkouts.setOnClickListener { startActivity(Intent(this, WorkoutsActivity::class.java)) }
        iconPlans.setOnClickListener { showMessage("Already on Plans") }
        iconProfile.setOnClickListener { startActivity(Intent(this, ProfileActivity::class.java)) }

        adapter.setOnPlanClickListener { presenter.onPlanClicked(it.id) }
        adapter.setOnPlanLongClickListener { showPlanOptions(it) }

        presenter.loadPlans()
    }

    private fun showPlanOptions(plan: WorkoutPlan) {
        AlertDialog.Builder(this)
            .setTitle(plan.name)
            .setItems(arrayOf("Edit", "Clone", "Delete", "Share")) { _, which ->
                when (which) {
                    0 -> showPlanEditor(plan)
                    1 -> presenter.clonePlan(plan.id)
                    2 -> confirmDeletePlan(plan)
                    3 -> showMessage("Sharing not available yet")
                }
            }
            .show()
    }

    private fun confirmDeletePlan(plan: WorkoutPlan) {
        AlertDialog.Builder(this)
            .setTitle("Delete plan")
            .setMessage("Delete \"${plan.name}\"? This cannot be undone.")
            .setPositiveButton("Delete") { _, _ -> presenter.deletePlan(plan.id) }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showPlanEditor(plan: WorkoutPlan?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_plan_edit, null)
        val title = dialogView.findViewById<TextView>(R.id.textDialogTitle)
        val nameField = dialogView.findViewById<TextView>(R.id.editTextPlanName)
        val descriptionField = dialogView.findViewById<TextView>(R.id.editTextPlanDescription)

        title.text = if (plan == null) "Create plan" else "Edit plan"
        if (plan != null) {
            nameField.text = plan.name
            descriptionField.text = plan.description ?: ""
        }

        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel", null)

        val dialog = builder.create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val planName = nameField.text.toString().trim()
                val planDescr = descriptionField.text.toString().trim().takeIf { it.isNotBlank() }
                if (planName.isBlank()) {
                    nameField.error = "Name required"
                    return@setOnClickListener
                }
                if (plan == null) {
                    presenter.createPlan(planName, planDescr)
                } else {
                    presenter.renamePlan(plan.id, planName, planDescr)
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun showPlans(plans: List<WorkoutPlan>) {
        runOnUiThread {
            layoutEmptyState.visibility = View.GONE
            recyclerPlans.visibility = View.VISIBLE
            adapter.updateList(plans)
        }
    }

    override fun showEmptyState() {
        runOnUiThread {
            recyclerPlans.visibility = View.GONE
            layoutEmptyState.visibility = View.VISIBLE
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
            iconHome.isEnabled = !show
            iconWorkouts.isEnabled = !show
            iconPlans.isEnabled = !show
            iconProfile.isEnabled = !show
            buttonCreatePlan.isEnabled = !show
        }
    }

    override fun navigateToPlanDetails(planId: Long) {
        runOnUiThread {
            val intent = Intent(this, PlanDetailActivity::class.java)
            intent.putExtra("plan_id", planId)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
