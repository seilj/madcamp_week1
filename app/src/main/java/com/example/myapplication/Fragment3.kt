package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.Fragment3Binding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class Student(val name: String, val classDates: MutableList<LocalDate>, val hours: Double, val hourlyWage: Double)

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

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment3Binding.inflate(inflater, container, false)
        calendarView = binding.calendarView
        totalSalaryTextView = binding.totalSalary

        listInitialize()

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            updateSchedules()
        }
        updatePayment()

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSchedules() {
        studentsOnSelectedDate = students.filter { it.classDates.contains(selectedDate) }
        val schedules = studentsOnSelectedDate.map { Schedule(it.name, it.hours) }.toMutableList()
        val adapter = ScheduleAdapter(schedules, { schedule ->
            cancelSchedule(schedule)
        }, {
            showAddClassDialog()
        })
        binding.schedule.adapter = adapter
        binding.schedule.layoutManager = LinearLayoutManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun cancelSchedule(schedule: Schedule) {
        val student = students.find { it.name == schedule.name }
        student?.classDates?.remove(selectedDate)
        updateSchedules()
        updatePayment()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddClassDialog() {
        val studentNames = students.map { it.name }.toTypedArray()
        var selectedStudentIndex = 0

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_class, null)
        val hoursInput = dialogView.findViewById<EditText>(R.id.hours_input)

        AlertDialog.Builder(requireContext())
            .setTitle("보강 추가")
            .setSingleChoiceItems(studentNames, 0) { _, which ->
                selectedStudentIndex = which
            }
            .setView(dialogView)
            .setPositiveButton("추가") { _, _ ->
                val selectedStudent = students[selectedStudentIndex]
                val hours = hoursInput.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                selectedStudent.classDates.add(selectedDate!!)
                updateSchedules()
                updatePayment()
            }
            .setNegativeButton("취소", null)
            .show()
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
            students.add(Student(person.name, regularClass, person.hourPerNumber, person.hourlyWage))
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
            val hourlyWage = student.hourlyWage
            val hours = student.hours
            totalPayment += student.classDates.size.toDouble() * hours * hourlyWage * 10000
        }
        return totalPayment.toInt()
    }
}