package com.example.flexpath.screens.profile

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.flexpath.R
import com.example.flexpath.screens.dashboard.DashboardActivity
import com.example.flexpath.screens.login.LoginActivity
import com.example.flexpath.screens.plans.PlansActivity
import com.example.flexpath.screens.workouts.WorkoutsActivity
import com.example.flexpath.ui.setEnabledRecursive

class ProfileActivity : Activity(), ProfileContract.View {
    private lateinit var presenter: ProfileContract.Presenter

    private lateinit var backToDashboard: ImageView
    private lateinit var clearUsers: TextView
    private lateinit var tvUsername: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rootContainer: View
    private lateinit var iconHome: ImageView
    private lateinit var iconWorkouts: ImageView
    private lateinit var iconPlans: ImageView
    private lateinit var iconProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_screen)

        // Presenter (MVP)
        ProfilePresenter(this).also { presenter = it }
        presenter.attachView(this)

        // Views
        backToDashboard = findViewById(R.id.iconHome)
        clearUsers = findViewById(R.id.textviewClearUsers)
        tvUsername = findViewById(R.id.textviewUsername)
        progressBar = findViewById<View?>(R.id.progressBarGlobal) as? ProgressBar
            ?: ProgressBar(this).apply { visibility = View.GONE }
        rootContainer = findViewById(android.R.id.content)

        iconHome = findViewById(R.id.iconHome)
        iconWorkouts = findViewById(R.id.iconWorkouts)
        iconPlans = findViewById(R.id.iconPlans)
        iconProfile = findViewById(R.id.iconProfile)

        // Handlers
        clearUsers.setOnClickListener { showClearConfirmation() }
        iconHome.setOnClickListener { presenter.onDashboardClicked() }
        iconWorkouts.setOnClickListener { presenter.onWorkoutsClicked() }
        iconPlans.setOnClickListener { presenter.onPlansClicked() }
        iconProfile.setOnClickListener { showMessage("Already on Profile") }

        // Load profile
        presenter.loadProfile()
    }

    private fun showClearConfirmation() {
        runOnUiThread {
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.confirm_clear_users_title))
                .setMessage(getString(R.string.confirm_clear_users_message))
                .setPositiveButton(getString(R.string.confirm_clear_users_positive)) { dialog, _ ->
                    dialog.dismiss()
                    presenter.clearUsers()
                }
                .setNegativeButton(getString(R.string.confirm_clear_users_negative)) { dialog, _ ->
                    dialog.dismiss()
                }
                .setCancelable(true)
                .show()
        }
    }

    // View contract
    override fun showUsername(username: String) {
        runOnUiThread {
            tvUsername.text = if (username.isNotBlank()) username else getString(R.string.example_username)
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
            (rootContainer as? ViewGroup)?.setEnabledRecursive(!show)
            backToDashboard.isEnabled = !show
            clearUsers.isEnabled = !show
            iconHome.isEnabled = !show
            iconWorkouts.isEnabled = !show
            iconPlans.isEnabled = !show
            iconProfile.isEnabled = !show
        }
    }

    override fun navigateToDashboard() {
        runOnUiThread {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
        }
    }

    override fun navigateToLogin() {
        runOnUiThread {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    override fun navigateToWorkouts() {
        runOnUiThread {
            startActivity(Intent(this, WorkoutsActivity::class.java))
        }
    }

    override fun navigateToPlans() {
        runOnUiThread {
            startActivity(Intent(this, PlansActivity::class.java))
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
