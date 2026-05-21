package com.example.flexpath.screens.workouts

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flexpath.R
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.screens.profile.ProfileActivity
import com.example.flexpath.ui.setEnabledRecursive

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)


        presenter = WorkoutsPresenter(this)
        presenter.attachView(this)


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
                .setTitle("Add workout")
                .setMessage("Add \"${item.title}\" to your workouts?")
                .setPositiveButton("Add") { _, _ ->
                    presenter.addFromPool(item.id)
                    poolAdapter.markPending(item.id, true)
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
            showMessage("Plans screen not implemented yet")
        }
    }
    
    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}