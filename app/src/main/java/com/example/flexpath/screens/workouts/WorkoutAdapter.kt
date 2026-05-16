package com.example.flexpath.screens.workouts

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class WorkoutAdapter(
    private val context: Context,
    initialList: List<WorkoutItem> = emptyList(),
    private val rowLayoutRes: Int = android.R.layout.simple_list_item_2
) : BaseAdapter() {

    private val items = ArrayList<WorkoutItem>(initialList)
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private var clickListener: ((WorkoutItem) -> Unit)? = null
    private var longClickListener: ((WorkoutItem) -> Boolean)? = null

    fun setOnItemClickListener(listener: (WorkoutItem) -> Unit) {
        clickListener = listener
    }

    fun setOnItemLongClickListener(listener: (WorkoutItem) -> Boolean) {
        longClickListener = listener
    }

    fun updateList(newList: List<WorkoutItem>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }

    fun addItem(item: WorkoutItem) {
        // add to top
        items.add(0, item)
        notifyDataSetChanged()
    }

    fun removeItemById(id: Long) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) {
            items.removeAt(idx)
            notifyDataSetChanged()
        }
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): WorkoutItem = items[position]

    override fun getItemId(position: Int): Long = items[position].id

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val view: View

        if (convertView == null) {
            view = inflater.inflate(rowLayoutRes, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = view.tag as ViewHolder
        }

        val item = getItem(position)
        holder.bind(item)

        view.setOnClickListener { clickListener?.invoke(item) }
        view.setOnLongClickListener {
            longClickListener?.invoke(item) ?: false
        }

        return view
    }

    private class ViewHolder(private val root: View) {
        private val titleView: TextView? = findTextView(root, android.R.id.text1, "rowTitle")
        private val subtitleView: TextView? = findTextView(root, android.R.id.text2, "rowSubtitle")

        fun bind(item: WorkoutItem) {
            titleView?.text = item.title
            val subtitle = buildString {
                append(item.primaryMuscle.name.lowercase().replaceFirstChar { it.uppercase() })
                append(" • ")
                append(item.equipment.name.lowercase().replaceFirstChar { it.uppercase() })
                append(" • ")
                append(item.difficulty.name.lowercase().replaceFirstChar { it.uppercase() })
            }
            subtitleView?.text = subtitle
        }

        companion object {
            private fun findTextView(root: View, androidId: Int, altIdName: String): TextView? {
                val byAndroid = root.findViewById<TextView?>(androidId)
                if (byAndroid != null) return byAndroid
                val res = root.resources
                val altId = res.getIdentifier(altIdName, "id", root.context.packageName)
                return if (altId != 0) root.findViewById(altId) else null
            }
        }
    }
}
