package com.example.flexpath.screens.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.flexpath.R
import com.example.flexpath.screens.login.LoginActivity
import com.example.flexpath.screens.profile.ProfileActivity

class DashboardActivity : Activity(), DashboardContract.View {
    private lateinit var presenter: DashboardContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        presenter = DashboardPresenter(this)
        presenter.attachView(this)

        val textviewGreeting: TextView = findViewById(R.id.textviewGreeting)
        val iconHome: ImageView = findViewById(R.id.iconHome)
        val iconWorkouts: ImageView = findViewById(R.id.iconWorkouts)
        val iconPlans: ImageView = findViewById(R.id.iconPlans)
        val iconProfile: ImageView = findViewById(R.id.iconProfile)
        val textviewLogout: TextView = findViewById(R.id.textviewLogout)

        iconHome.setOnClickListener { showMessage("Already on Dashboard") }
        iconWorkouts.setOnClickListener { presenter.onWorkoutsClicked() }
        iconPlans.setOnClickListener { presenter.onPlansClicked() }
        iconProfile.setOnClickListener { presenter.onProfileClicked() }
        textviewLogout.setOnClickListener { presenter.onLogoutClicked() }

        presenter.loadUser()
    }

    override fun showGreeting(username: String) {
        val textviewGreeting: TextView = findViewById(R.id.textviewGreeting)
        textviewGreeting.text = "Welcome, $username"
    }

    override fun navigateToProfile(username: String) {
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("username", username)
        startActivity(intent)
    }

    override fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showLoading(show: Boolean) {
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
