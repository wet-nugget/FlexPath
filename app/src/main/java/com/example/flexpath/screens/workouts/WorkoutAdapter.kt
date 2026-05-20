package com.example.flexpath.screens.workouts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.flexpath.R

class WorkoutAdapter(
    private var items: MutableList<WorkoutItem> = mutableListOf()
) : RecyclerView.Adapter<WorkoutAdapter.VH>() {

    private val pendingSet = mutableSetOf<Long>()
    private var clickListener: ((WorkoutItem) -> Unit)? = null
    private var longClickListener: ((WorkoutItem) -> Unit)? = null

    fun setOnItemClickListener(cb: (WorkoutItem) -> Unit) { clickListener = cb }
    fun setOnItemLongClickListener(cb: (WorkoutItem) -> Unit) { longClickListener = cb }

    fun updateList(newItems: List<WorkoutItem>) {
        items = newItems.toMutableList()
        pendingSet.clear()
        notifyDataSetChanged()
    }

    fun addItem(item: WorkoutItem) {
        items.add(0, item)
        notifyItemInserted(0)
    }

    fun removeItemById(id: Long) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) {
            items.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }

    fun markPending(id: Long, pending: Boolean) {
        if (pending) pendingSet.add(id) else pendingSet.remove(id)
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) notifyItemChanged(idx)
    }

    fun isPending(id: Long) = pendingSet.contains(id)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.title.text = item.title

        val primary = item.primaryMuscle.toString()
        val equip = item.equipment.toString()
        val diff = item.difficulty.toString()
        holder.subtitle.text = "$primary • $equip • $diff"

        val pending = isPending(item.id)
        holder.disabledOverlay.visibility = if (pending) View.VISIBLE else View.GONE
        holder.itemView.isEnabled = !pending

        holder.itemView.setOnClickListener {
            if (!pending) clickListener?.invoke(item)
        }
        holder.itemView.setOnLongClickListener {
            if (!pending) {
                longClickListener?.invoke(item)
                true
            } else false
        }
    }

    override fun getItemCount(): Int = items.size

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.textTitle)
        val subtitle: TextView = view.findViewById(R.id.textSubtitle)
        val disabledOverlay: View = view.findViewById(R.id.viewDisabledOverlay)
    }
}