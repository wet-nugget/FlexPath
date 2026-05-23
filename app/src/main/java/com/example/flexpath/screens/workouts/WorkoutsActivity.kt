package com.example.flexpath.screens.workouts

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
import com.example.flexpath.data.WorkoutsRepository
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.screens.plans.PlansActivity
import com.example.flexpath.screens.plans.PlansRepository
import com.example.flexpath.screens.profile.ProfileActivity
import com.example.flexpath.ui.setEnabledRecursive
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WorkoutsActivity : Activity(), WorkoutsContract.View {

    private lateinit var presenter: WorkoutsContract.Presenter

    private lateinit var recyclerPool: RecyclerView
    private lateinit var recyclerUser: RecyclerView
    private lateinit var poolAdapter: WorkoutAdapter
    private lateinit var userAdapter: WorkoutAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var rootContainer: ViewGroup
    private lateinit var iconHome: ImageView
    private lateinit var iconWorkouts: ImageView
    private lateinit var iconPlans: ImageView
    private lateinit var iconProfile: ImageView
    private lateinit var plansRepo: PlansRepository
    private val planScope: CoroutineScope = MainScope()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)


        presenter = WorkoutsPresenter(this)
        presenter.attachView(this)
        plansRepo = PlansRepository(this)
        recyclerPool = findViewById(R.id.recyclerPool)
        recyclerUser = findViewById(R.id.recyclerUser)
        progressBar = findViewById(R.id.progressBarLoading)
        rootContainer = findViewById(R.id.workoutsRoot)

        iconHome = findViewById(R.id.iconHome)
        iconWorkouts = findViewById(R.id.iconWorkouts)
        iconPlans = findViewById(R.id.iconPlans)
        iconProfile = findViewById(R.id.iconProfile)


        poolAdapter = WorkoutAdapter()
        userAdapter = WorkoutAdapter()


        recyclerPool.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerPool.adapter = poolAdapter


        recyclerUser.layoutManager = LinearLayoutManager(this)
        recyclerUser.adapter = userAdapter

        iconHome.setOnClickListener { presenter.onDashboardClicked() }
        iconWorkouts.setOnClickListener { showMessage("Already on Workouts") }
        iconPlans.setOnClickListener { presenter.onPlansClicked() }
        iconProfile.setOnClickListener { presenter.onProfileClicked() }


        poolAdapter.setOnItemClickListener { item ->
            AlertDialog.Builder(this)
                .setTitle(item.title)
                .setItems(arrayOf("Add to My Workouts", "Add to Plan", "View details")) { _, which ->
                    when (which) {
                        0 -> {
                            presenter.addFromPool(item.id)
                            poolAdapter.markPending(item.id, true)
                        }
                        1 -> showAddToPlanChooser(item)
                        2 -> showWorkoutDetails(item)
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
        }


        poolAdapter.setOnItemLongClickListener { item ->
            AlertDialog.Builder(this)
                .setTitle(item.title)
                .setMessage(buildString {
                    appendLine(item.description ?: "No description")
                    appendLine()

                    append("Primary: ${item.primaryMuscle}")
                    append(" • Equipment: ${item.equipment}")
                    append(" • Difficulty: ${item.difficulty}")
                })
                .setPositiveButton("OK", null)
                .show()
        }


        userAdapter.setOnItemLongClickListener { item ->
            AlertDialog.Builder(this)
                .setTitle("Remove workout")
                .setMessage("Remove \"${item.title}\" from your workouts?")
                .setPositiveButton("Remove") { _, _ ->
                    presenter.removeFromUserList(item.id)
                    userAdapter.markPending(item.id, true)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        presenter.loadAll()
    }

    private fun showAddToPlanChooser(item: WorkoutItem) {
        planScope.launch {
            val plans = withContext(Dispatchers.IO) { plansRepo.loadPlans() }
            val options = mutableListOf("Create new plan")
            options += plans.map { "${it.name} (${it.workoutCount})" }
            runOnUiThread {
                AlertDialog.Builder(this@WorkoutsActivity)
                    .setTitle("Add \"${item.title}\" to plan")
                    .setItems(options.toTypedArray()) { _, index ->
                        if (index == 0) {
                            showCreatePlanForItem(item)
                        } else {
                            val plan = plans[index - 1]
                            planScope.launch {
                                val added = withContext(Dispatchers.IO) { plansRepo.addWorkoutToPlan(plan.id, item.id) }
                                runOnUiThread {
                                    if (added) {
                                        Snackbar.make(rootContainer, "Added \"${item.title}\" to ${plan.name}", Snackbar.LENGTH_LONG)
                                            .setAction("Undo") {
                                                planScope.launch {
                                                    withContext(Dispatchers.IO) { plansRepo.removeWorkoutFromPlan(plan.id, item.id) }
                                                }
                                            }
                                            .show()
                                    } else {
                                        showMessage("This workout is already in ${plan.name}")
                                    }
                                }
                            }
                        }
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }

    private fun showCreatePlanForItem(item: WorkoutItem) {
        runOnUiThread {
            val dialogView = layoutInflater.inflate(R.layout.dialog_plan_edit, null)
            val title = dialogView.findViewById<TextView>(R.id.textDialogTitle)
            val nameField = dialogView.findViewById<TextView>(R.id.editTextPlanName)
            val descriptionField = dialogView.findViewById<TextView>(R.id.editTextPlanDescription)
            title.text = "Create plan"

            val dialog = AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Create", null)
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
                    planScope.launch {
                        val plan = withContext(Dispatchers.IO) { plansRepo.createPlan(planName, planDescr) }
                        runOnUiThread {
                            if (plan != null) {
                                Snackbar.make(rootContainer, "Created plan ${plan.name}", Snackbar.LENGTH_LONG)
                                    .setAction("Add workout") {
                                        planScope.launch {
                                            withContext(Dispatchers.IO) { plansRepo.addWorkoutToPlan(plan.id, item.id) }
                                        }
                                    }
                                    .show()
                                dialog.dismiss()
                            } else {
                                showMessage("Plan name must be unique")
                            }
                        }
                    }
                }
            }
            dialog.show()
        }
    }

    private fun showWorkoutDetails(item: WorkoutItem) {
        AlertDialog.Builder(this)
            .setTitle(item.title)
            .setMessage(item.description ?: "No description")
            .setPositiveButton("OK", null)
            .show()
    }

    
    override fun showProvidedPool(pool: List<WorkoutItem>) {
        runOnUiThread {
            poolAdapter.updateList(pool)
        }
    }

    
    override fun showUserList(list: List<WorkoutItem>) {
        runOnUiThread {
            userAdapter.updateList(list)
        }
    }

    
    override fun showAdded(item: WorkoutItem) {
        runOnUiThread {
            poolAdapter.markPending(item.id, false)
            userAdapter.addItem(item)
            recyclerUser.scrollToPosition(0)
            Toast.makeText(this, "Added: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    
    override fun showRemoved(item: WorkoutItem) {
        runOnUiThread {
            userAdapter.markPending(item.id, false)
            userAdapter.removeItemById(item.id)
            Toast.makeText(this, "Removed: ${item.title}", Toast.LENGTH_SHORT).show()
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
        }
    }

    override fun navigateToDashboard() {
        runOnUiThread {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
    }

    override fun navigateToProfile() {
        runOnUiThread {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
    }

    override fun navigateToPlans() {
        runOnUiThread {
            startActivity(Intent(this, PlansActivity::class.java))
        }
    }
    
    override fun onDestroy() {
        presenter.detachView()
        planScope.cancel()
        super.onDestroy()
    }
}