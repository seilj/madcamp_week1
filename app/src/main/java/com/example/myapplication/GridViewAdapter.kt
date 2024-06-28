package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class GridViewAdapter(val context: Context?, val image_list: Array<Int>, val text_list: Array<String>) : BaseAdapter() {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view : View = LayoutInflater.from(context).inflate(R.layout.gridview_item, null)

        val textView = view.findViewById<TextView>(R.id.gv_text)
        val imageView = view.findViewById<ImageView>(R.id.gv_img)

        textView.text = text_list[position]
        imageView.setImageResource(image_list[position])

        return view
    }

    override fun getItem(position: Int): Any{
        return text_list[position]
    }

    override fun getItemId(position: Int): Long{
        return position.toLong()
    }

    override fun getCount(): Int {
        return image_list.size
    }
}