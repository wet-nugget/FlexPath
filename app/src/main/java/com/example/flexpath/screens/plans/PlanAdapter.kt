package com.example.flexpath.screens.plans

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flexpath.R

class PlanAdapter(
    private var plans: List<WorkoutPlan> = listOf()
) : RecyclerView.Adapter<PlanAdapter.VH>() {

    private var clickListener: ((WorkoutPlan) -> Unit)? = null
    private var longClickListener: ((WorkoutPlan) -> Unit)? = null

    fun setOnPlanClickListener(cb: (WorkoutPlan) -> Unit) {
        clickListener = cb
    }

    fun setOnPlanLongClickListener(cb: (WorkoutPlan) -> Unit) {
        longClickListener = cb
    }

    fun updateList(newPlans: List<WorkoutPlan>) {
        plans = newPlans
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_plan, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val plan = plans[position]
        holder.title.text = plan.name
        holder.details.text = plan.displaySubtitle()
        holder.description.text = plan.description ?: ""
        holder.itemView.setOnClickListener { clickListener?.invoke(plan) }
        holder.itemView.setOnLongClickListener {
            longClickListener?.invoke(plan)
            true
        }
    }

    override fun getItemCount(): Int = plans.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textPlanName)
        val description: TextView = view.findViewById(R.id.textPlanDescription)
        val details: TextView = view.findViewById(R.id.textPlanMeta)
    }
}
