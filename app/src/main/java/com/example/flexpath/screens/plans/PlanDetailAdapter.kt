package com.example.flexpath.screens.plans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flexpath.R
import com.example.flexpath.screens.workouts.WorkoutItem

class PlanDetailAdapter(
    private var workouts: MutableList<WorkoutItem> = mutableListOf()
) : RecyclerView.Adapter<PlanDetailAdapter.VH>() {

    private var clickListener: ((WorkoutItem) -> Unit)? = null
    private var removeListener: ((WorkoutItem, Int) -> Unit)? = null

    fun setOnWorkoutClickListener(cb: (WorkoutItem) -> Unit) { clickListener = cb }
    fun setOnRemoveClickListener(cb: (WorkoutItem, Int) -> Unit) { removeListener = cb }

    fun updateList(newWorkouts: List<WorkoutItem>) {
        workouts = newWorkouts.toMutableList()
        notifyDataSetChanged()
    }

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val item = workouts.removeAt(fromPosition)
        workouts.add(toPosition, item)
        notifyItemMoved(fromPosition, toPosition)
    }

    fun removeItemAt(position: Int): WorkoutItem {
        val item = workouts.removeAt(position)
        notifyItemRemoved(position)
        return item
    }

    fun insertItem(position: Int, item: WorkoutItem) {
        workouts.add(position, item)
        notifyItemInserted(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plan_detail_workout, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val workout = workouts[position]
        holder.title.text = workout.title
        holder.subtitle.text = workout.description ?: workout.primaryMuscle.toString()
        holder.handle.text = "â‰¡"
        holder.itemView.setOnClickListener { clickListener?.invoke(workout) }
        holder.remove.setOnClickListener { removeListener?.invoke(workout, position) }
    }

    override fun getItemCount(): Int = workouts.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textWorkoutTitle)
        val subtitle: TextView = view.findViewById(R.id.textWorkoutSubtitle)
        val remove: TextView = view.findViewById(R.id.buttonRemoveWorkout)
        val handle: TextView = view.findViewById(R.id.textDragHandle)
    }
}
