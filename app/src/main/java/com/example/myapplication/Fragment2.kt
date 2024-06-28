package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.Fragment2Binding


class Fragment2: Fragment() {
    private lateinit var binding: Fragment2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment2Binding.inflate(inflater, container, false)
        val img = arrayOf(
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
            R.drawable.ic_home_black_24dp,
        )
        val txt = arrayOf(
            "txt1",
            "txt2",
            "txt3",
            "txt4",
            "txt5",
            "txt6"
        )

        val gridviewAdapter = GridViewAdapter(requireContext(), img, txt)
        binding.gridview.adapter = gridviewAdapter
        return binding.root
    }
}