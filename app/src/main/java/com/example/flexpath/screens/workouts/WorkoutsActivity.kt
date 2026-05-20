package com.example.flexpath.screens.workouts

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.flexpath.R
import com.example.flexpath.ui.setEnabledRecursive

class WorkoutsActivity : Activity(), WorkoutsContract.View {
    private lateinit var presenter: WorkoutsContract.Presenter

    private lateinit var recyclerPool: RecyclerView
    private lateinit var recyclerUser: RecyclerView
    private lateinit var poolAdapter: WorkoutAdapter
    private lateinit var userAdapter: WorkoutAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var rootContainer: ViewGroup

    private var operationInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workouts)

        presenter = WorkoutsPresenter(this)
        presenter.attachView(this)

        recyclerPool = findViewById(R.id.recyclerPool)
        recyclerUser = findViewById(R.id.recyclerUser)
        progressBar = findViewById(R.id.progressBarLoading)
        rootContainer = findViewById(R.id.workoutsRoot)

        poolAdapter = WorkoutAdapter()
        userAdapter = WorkoutAdapter()

        recyclerPool.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerPool.adapter = poolAdapter

        recyclerUser.layoutManager = LinearLayoutManager(this)
        recyclerUser.adapter = userAdapter

        poolAdapter.setOnItemClickListener { item ->
            if (operationInProgress) return@setOnItemClickListener
            AlertDialog.Builder(this)
                .setTitle("Add workout")
                .setMessage("Add \"${item.title}\" to your workouts?")
                .setPositiveButton("Add") { _, _ ->
                    operationInProgress = true
                    poolAdapter.markPending(item.id, true)
                    (presenter as WorkoutsPresenter).addFromPool(item.id)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        poolAdapter.setOnItemLongClickListener { item ->
            AlertDialog.Builder(this)
                .setTitle(item.title)
                .setMessage(buildString {
                    appendLine(item.description ?: "")
                    appendLine()
                    append("Primary: ${item.primaryMuscle ?: "—"}")
                    append(" • Equipment: ${item.equipment ?: "—"}")
                    append(" • Difficulty: ${item.difficulty ?: "—"}")
                })
                .setPositiveButton("OK", null)
                .show()
        }

        userAdapter.setOnItemLongClickListener { item ->
            if (operationInProgress) return@setOnItemLongClickListener
            AlertDialog.Builder(this)
                .setTitle("Remove workout")
                .setMessage("Remove \"${item.title}\" from your workouts?")
                .setPositiveButton("Remove") { _, _ ->
                    operationInProgress = true
                    userAdapter.markPending(item.id, true)
                    presenter.removeFromUserList(item.id)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        presenter.loadAll()
    }

    override fun showProvidedPool(pool: List<WorkoutItem>) {
        runOnUiThread { poolAdapter.updateList(pool) }
    }

    override fun showUserList(list: List<WorkoutItem>) {
        runOnUiThread { userAdapter.updateList(list) }
    }

    override fun showAdded(item: WorkoutItem) {
        runOnUiThread {
            poolAdapter.markPending(item.id, false)
            operationInProgress = false
            userAdapter.addItem(item)
            recyclerUser.scrollToPosition(0)
            Toast.makeText(this, "Added: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showRemoved(item: WorkoutItem) {
        runOnUiThread {
            userAdapter.markPending(item.id, false)
            operationInProgress = false
            userAdapter.removeItemById(item.id)
            Toast.makeText(this, "Removed: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showMessage(message: String) {
        runOnUiThread { Toast.makeText(this, message, Toast.LENGTH_SHORT).show() }
    }

    override fun showLoading(show: Boolean) {
        runOnUiThread {
            progressBar.visibility = if (show) View.VISIBLE else View.GONE
            rootContainer.setEnabledRecursive(!show)
        }
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
