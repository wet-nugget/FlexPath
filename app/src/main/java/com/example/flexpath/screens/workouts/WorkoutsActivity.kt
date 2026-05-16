package com.example.flexpath.screens.workouts

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import com.example.flexpath.R

class WorkoutsActivity : Activity(), WorkoutsContract.View {
    private lateinit var presenter: WorkoutsContract.Presenter

    private lateinit var userAdapter: WorkoutAdapter
    private lateinit var poolAdapter: WorkoutAdapter
    private lateinit var listViewUser: ListView
    private lateinit var listViewPool: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = WorkoutsPresenter(this)
        presenter.attachView(this)

        listViewUser = findViewById(R.id.listviewUserWorkouts)
        listViewPool = findViewById(R.id.listviewPoolWorkouts)

        userAdapter = WorkoutAdapter(this, emptyList(), android.R.layout.simple_list_item_2)
        poolAdapter = WorkoutAdapter(this, emptyList(), android.R.layout.simple_list_item_2)

        listViewUser.adapter = userAdapter
        listViewPool.adapter = poolAdapter

        userAdapter.setOnItemClickListener { item ->
            presenter.onUserItemClicked(item)
        }
        userAdapter.setOnItemLongClickListener { item ->
            AlertDialog.Builder(this)
                .setTitle("Remove workout")
                .setMessage("Remove \"${item.title}\" from your list?")
                .setPositiveButton("Remove") { _, _ -> presenter.removeFromUserList(item.id) }
                .setNegativeButton("Cancel", null)
                .show()
            true
        }

        poolAdapter.setOnItemClickListener { item ->
            // confirm add
            AlertDialog.Builder(this)
                .setTitle("Add workout")
                .setMessage("Add \"${item.title}\" to your list?")
                .setPositiveButton("Add") { _, _ -> presenter.addFromPool(item.id) }
                .setNegativeButton("Cancel", null)
                .show()
        }
        poolAdapter.setOnItemLongClickListener { item ->
            // show details
            AlertDialog.Builder(this)
                .setTitle(item.title)
                .setMessage(buildString {
                    appendLine(item.description ?: "")
                    appendLine()
                    append("Primary: ${item.primaryMuscle}")
                    append(" • Equipment: ${item.equipment}")
                    append(" • Difficulty: ${item.difficulty}")
                })
                .setPositiveButton("OK", null)
                .show()
            true
        }

        // load data
        presenter.loadAll()
    }

    // View contract implementations
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
            userAdapter.addItem(item)
            listViewUser.smoothScrollToPosition(0)
            Toast.makeText(this, "Added: ${item.title}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun showRemoved(item: WorkoutItem) {
        runOnUiThread {
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
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }
}
