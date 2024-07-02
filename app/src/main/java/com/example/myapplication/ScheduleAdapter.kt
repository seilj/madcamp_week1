package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(
    private var items: List<Schedule>,
    private val onCancelClick: (Schedule) -> Unit,
    private val onAddClassClick: () -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM = 0
    private val TYPE_BUTTON = 1

    inner class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bindItems(items: Schedule) {
            val nameView = itemView.findViewById<TextView>(R.id.name)
            val hoursView = itemView.findViewById<TextView>(R.id.hours)
            val cancelView = itemView.findViewById<Button>(R.id.cancel_button)
            nameView.text = items.name
            hoursView.text = "${items.hours} 시간"
            cancelView.setOnClickListener { onCancelClick(items) }
        }
    }

    inner class ButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val addButton = itemView.findViewById<Button>(R.id.add_class_button)
            addButton.setOnClickListener { onAddClassClick() }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == items.size) TYPE_BUTTON else TYPE_ITEM
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
            ScheduleViewHolder(v)
        } else {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.add_class_item, parent, false)
            ButtonViewHolder(v)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ScheduleViewHolder) {
            holder.bindItems(items[position])
        }
    }

    override fun getItemCount(): Int {
        return items.size + 1
    }

    fun updateData(newSchedules: List<Schedule>) {
        items = newSchedules
        notifyDataSetChanged() // 어댑터 데이터 변경 알림
    }
}