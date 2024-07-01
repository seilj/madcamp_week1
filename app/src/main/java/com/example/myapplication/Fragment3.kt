package com.example.myapplication

import android.R
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.Fragment3Binding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class Student(val name: String,val regularClass: MutableList<LocalDate>, val offDays: MutableList<LocalDate>, val makeupDays: MutableList<Pair<LocalDate, Int>>)
class Fragment3 : Fragment() {
    private lateinit var binding: Fragment3Binding
    private val students = mutableListOf<Student>()
    private lateinit var totalSalaryTextView: TextView
    private val peopleList: List<PeopleData>
        get() {
            val mainActivity = activity as? MainActivity
            return mainActivity?.getPeopleList() ?: emptyList()
        }
    private var selectedDate: LocalDate? = null
    private var studentsOnSelectedDate: List<Student> = emptyList()
    private lateinit var calendarView: CalendarView
    private lateinit var spinner: Spinner
    private lateinit var cancleBtn: Button
    private lateinit var makeupBtn: Button
    private lateinit var deleteBtn: Button

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment3Binding.inflate(inflater, container, false)
        calendarView = binding.calendarView
        spinner = binding.spinner
        cancleBtn = binding.cancleBtn
        makeupBtn = binding.makeupBtn
        deleteBtn = binding.deleteBtn
        totalSalaryTextView = binding.totalSalary

        listInitialize()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            updateStudentsOnSelectedDate()
        }

        cancleBtn.setOnClickListener {
            val selectedStudentName = spinner.selectedItem as? String
            selectedStudentName?.let { name ->
                val student = students.find { it.name == name }
                selectedDate?.let { date ->
                    student?.let {
                        it.offDays.add(date)
                        updateStudentsOnSelectedDate()
                        updatePayment()
                    }
                }
            }
        }

        updatePayment()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun listInitialize() {
        for (person in peopleList) {
            val regularClass = mutableListOf<LocalDate>()
            val yearMonth = YearMonth.now()
            val daysInMonth = yearMonth.lengthOfMonth()
            for (day in 1..daysInMonth) {
                val date = LocalDate.of(yearMonth.year, yearMonth.monthValue, day)
                val formatter = DateTimeFormatter.ofPattern("EEEE")
                val dayOfWeekString = date.format(formatter)
                if (person.week == dayOfWeekString)
                    regularClass.add(date)
            }
            students.add(Student(person.name, regularClass, mutableListOf(), mutableListOf()))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateStudentsOnSelectedDate() {
        selectedDate?.let { date ->
            studentsOnSelectedDate = students.filter {
                it.regularClass.contains(date) && !it.offDays.contains(date)
            }
            if (studentsOnSelectedDate.isNotEmpty()) {
                spinner.visibility = View.VISIBLE
                cancleBtn.visibility = View.VISIBLE
                val studentNames = studentsOnSelectedDate.map { it.name }
                spinner.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, studentNames)
            } else {
                spinner.visibility = View.GONE
                cancleBtn.visibility = View.GONE
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePayment() {
        val totalSalary = calculateMonthlyPayment()
        totalSalaryTextView.text = "이번 달 과외비: ${totalSalary}원"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthlyPayment(): Int {
        var totalPayment: Double = 0.0
        for (student in students) {
            val person = peopleList.find { it.name == student.name }
            val hourlyWage = person?.hourlyWage ?: 0.0
            val hours = person?.hourPerNumber ?: 0.0
            totalPayment += (student.regularClass.size - student.offDays.size).toDouble() * hours * hourlyWage * 10000
        }
        return totalPayment.toInt()
    }
}