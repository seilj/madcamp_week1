package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.Fragment3Binding
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth


class Fragment3: Fragment() {
    private lateinit var binding: Fragment3Binding
    //private val days = arrayListOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    private val offDays = mutableListOf<LocalDate>()
    private val makeupDays = mutableListOf<LocalDate>()
    private val hourly_wage = 30000
    private val class_hour = 4
    private lateinit var totalSalaryTextView: TextView

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
        totalSalaryTextView = binding.totalSalary

        calendarView.setOnDateChangeListener{view, year, month, dayOfMonth ->
            val selectedDate = LocalDate.of(year, month+1, dayOfMonth)
            val dayOfWeek = selectedDate.dayOfWeek

            if(dayOfWeek == DayOfWeek.SUNDAY){
                if(offDays.contains(selectedDate)){
                    cancleBtn.visibility = View.INVISIBLE
                    makeupBtn.visibility = View.INVISIBLE
                    deleteBtn.visibility = View.VISIBLE
                    deleteBtn.setOnClickListener {
                        offDays.remove(selectedDate)
                    }
                }
                else{
                    cancleBtn.visibility = View.VISIBLE
                    makeupBtn.visibility = View.INVISIBLE
                    deleteBtn.visibility = View.INVISIBLE
                    cancleBtn.setOnClickListener{
                        offDays.add(selectedDate)
                    }
                }
            }
            else{
                if(makeupDays.contains(selectedDate)){
                    cancleBtn.visibility = View.INVISIBLE
                    makeupBtn.visibility = View.INVISIBLE
                    deleteBtn.visibility = View.VISIBLE
                    deleteBtn.setOnClickListener {
                        makeupDays.remove(selectedDate)
                    }
                }
                else{
                    cancleBtn.visibility = View.INVISIBLE
                    makeupBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.INVISIBLE
                    makeupBtn.setOnClickListener{
                        makeupDays.add(selectedDate)
                    }
                }
            }

            updatePayment()
        }
        updatePayment()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePayment(){
        val yearMonth = YearMonth.now()
        val totalSalary = calculateMonthlyPayment(yearMonth, hourly_wage, class_hour, offDays, makeupDays)
        totalSalaryTextView.text = "이번 달 과외비: ${totalSalary}원"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthlyPayment(
        yearMonth: YearMonth,
        hourlyWage: Int,
        classHour: Int,
        offDays: List<LocalDate>,
        makeupDays: List<LocalDate>
    ): Int {
        var totalPayment = 0
        val daysInMonth = yearMonth.lengthOfMonth()
        for (day in 1..daysInMonth) {
            val date = LocalDate.of(yearMonth.year, yearMonth.monthValue, day)
            if (date.dayOfWeek == DayOfWeek.SUNDAY && !offDays.contains(date)) {
                totalPayment += hourlyWage * classHour
            }
        }
        totalPayment += hourlyWage * classHour * makeupDays.size

        return totalPayment
    }
}