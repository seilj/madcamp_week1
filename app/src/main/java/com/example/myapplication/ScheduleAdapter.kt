package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScheduleAdapter(val items: MutableList<Schedule>, val onCancelClick: (Schedule) -> Unit): RecyclerView.Adapter<ScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bindItems(items: Schedule){
            val nameView = itemView.findViewById<TextView>(R.id.name)
            val hoursView = itemView.findViewById<TextView>(R.id.hours)
            val cancelView = itemView.findViewById<Button>(R.id.cancel_button)
            nameView.text = items.name
            hoursView.text = "${items.hours} 시간"
            cancelView.setOnClickListener { onCancelClick(items) }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.schedule_item, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ScheduleAdapter.ViewHolder, position: Int){
        holder.bindItems(items[position])
    }

    override fun getItemCount(): Int {
        return items.count()
    }
}