package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.Fragment3Binding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale


class Fragment3: Fragment() {
    private lateinit var binding: Fragment3Binding
    //private val days = arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment3Binding.inflate(inflater, container, false)
        val calendarView = binding.calendarView
        val cancleBtn = binding.cancleBtn
        val makeupBtn = binding.makeupBtn
        val deleteBtn = binding.deleteBtn

        calendarView.setOnDateChangeListener{view, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month+1, dayOfMonth)
            val dayOfWeek = selectedDate.dayOfWeek
            val dayOfWeekText = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.KOREAN)
            if(dayOfWeekText == "일요일"){
                cancleBtn.visibility = View.VISIBLE
                makeupBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
            }
            else{
                cancleBtn.visibility = View.INVISIBLE
                makeupBtn.visibility = View.VISIBLE
                deleteBtn.visibility = View.INVISIBLE
            }
        }

        cancleBtn.setOnClickListener{
            //ToDo
        }

        makeupBtn.setOnClickListener{
            //ToDo
        }

        return binding.root
    }
}