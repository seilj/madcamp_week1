package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.FullImageBinding

class Fragment2ImageActivity : AppCompatActivity() {
    private lateinit var binding: FullImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pos = intent.getIntExtra("pos", 0)

        Glide.with(this)
            .load(ImageResources.images[pos])
            .into(binding.imageFull)
    }
}