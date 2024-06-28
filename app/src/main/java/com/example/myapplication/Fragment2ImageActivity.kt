package com.example.myapplication

import android.app.ActivityOptions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class Fragment2ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.full_image)

        val img = when(intent.getIntExtra("pos", 0)) {
            0 -> R.drawable.ic_dashboard_black_24dp
            1 -> R.drawable.ic_dashboard_black_24dp
            2 -> R.drawable.ic_dashboard_black_24dp
            3 -> R.drawable.ic_dashboard_black_24dp
            4 -> R.drawable.ic_dashboard_black_24dp
            else -> R.drawable.ic_dashboard_black_24dp
        }
        val imageView = findViewById<ImageView>(R.id.image_full)
        imageView.setImageResource(img)

        imageView.setOnClickListener {
            supportFinishAfterTransition()
        }
    }
}