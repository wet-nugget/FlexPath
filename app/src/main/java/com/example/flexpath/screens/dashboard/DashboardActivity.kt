package com.example.flexpath.screens.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.flexpath.R
import com.example.flexpath.screens.login.LoginActivity
import com.example.flexpath.screens.profile.ProfileActivity
import com.example.flexpath.screens.workouts.WorkoutsActivity
import com.example.flexpath.ui.setEnabledRecursive

class DashboardActivity : Activity(), DashboardContract.View {
    private lateinit var presenter: DashboardContract.Presenter

    private lateinit var textviewGreeting: TextView
    private lateinit var iconHome: ImageView
    private lateinit var iconWorkouts: ImageView
    private lateinit var iconPlans: ImageView
    private lateinit var iconProfile: ImageView
    private lateinit var textviewLogout: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var rootContainer: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        DashboardPresenter(this).also { presenter = it }
        presenter.attachView(this)

        textviewGreeting = findViewById(R.id.textviewGreeting)
        iconHome = findViewById(R.id.iconHome)
        iconWorkouts = findViewById(R.id.iconWorkouts)
        iconPlans = findViewById(R.id.iconPlans)
        iconProfile = findViewById(R.id.iconProfile)
        textviewLogout = findViewById(R.id.textviewLogout)

        progressBar = findViewById<View?>(R.id.progressBarGlobal) as? ProgressBar
            ?: ProgressBar(this).apply { visibility = View.GONE }

        rootContainer = findViewById(android.R.id.content)

        iconHome.setOnClickListener { showMessage("Already on Dashboard") }
        iconWorkouts.setOnClickListener { presenter.onWorkoutsClicked() }
        iconPlans.setOnClickListener { presenter.onPlansClicked() }
        iconProfile.setOnClickListener { presenter.onProfileClicked() }
        textviewLogout.setOnClickListener { presenter.onLogoutClicked() }

        presenter.loadUser()
    }

    override fun showGreeting(username: String) {
        runOnUiThread {
            textviewGreeting.text = "Welcome, $username"
        }
    }

    override fun navigateToProfile(username: String) {
        runOnUiThread {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
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
            showMessage("Plans screen not implemented yet")
        }
    }

    override fun showMessage(message: String) {
        runOnUiThread {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showLoading(show: Boolean) {
        runOnUiThread {
            val pb = findViewById<View?>(R.id.progressBarGlobal) as? ProgressBar
            if (pb != null) {
                pb.visibility = if (show) View.VISIBLE else View.GONE
            } else {
                (rootContainer as? ViewGroup)?.setEnabledRecursive(!show)
            }
            iconHome.isEnabled = !show
            iconWorkouts.isEnabled = !show
            iconPlans.isEnabled = !show
            iconProfile.isEnabled = !show
            textviewLogout.isEnabled = !show
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
