package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide

class GridViewAdapter(
    val context: Context?,
    val imageUris: List<Uri>,  // URI 목록으로 변경
    val texts: List<String>    // MutableList가 아닌 List 사용
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: LayoutInflater.from(context).inflate(R.layout.gridview_item, parent, false)

        val textView = view.findViewById<TextView>(R.id.gv_text)
        val imageView = view.findViewById<ImageView>(R.id.gv_img)

        textView.text = texts[position]
        if (context != null) {
            // Glide를 사용하여 이미지 로드
            Glide.with(context)
                .load(imageUris[position])
                .into(imageView)
        }
        notifyDataSetChanged()
        return view
    }

    override fun getItem(position: Int): Any {
        return texts[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return texts.size
    }
}
