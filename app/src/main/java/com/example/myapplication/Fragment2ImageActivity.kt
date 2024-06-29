package com.example.myapplication

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.FullImageBinding

class Fragment2ImageActivity : AppCompatActivity() {
    private lateinit var binding: FullImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pos = intent.getIntExtra("pos", 0)
        val imgResId = getImageResource(pos)

        binding.imageFull.setImageResource(imgResId)
    }

    private fun getImageResource(pos: Int): Int {
        val img = arrayOf(
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
        )
        return img[pos]
    }
}