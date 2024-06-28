package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class GridViewAdapter(private var context: Context?, private var image_list: Array<Int>, private var txt_list: Array<String>) : BaseAdapter() {
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {

        val view : View = LayoutInflater.from(context).inflate(R.layout.gridview_item, null)

        view.gv_text.text = txt_list[p0]
        view.gv_img.setImageResource(image_list[p0])

        return view
    }
}