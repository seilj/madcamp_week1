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
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.Fragment3Binding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class Fragment3 : Fragment() {
    private lateinit var binding: Fragment3Binding
    private val viewModel: ListDataViewModel by viewModels()
    private lateinit var totalSalaryTextView: TextView
    private var selectedDate: LocalDate? = null
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

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
            updateSchedules()
        }
        // Observe schedules LiveData
        viewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            updateRecyclerView(schedules)
        }

        // Observe schedules LiveData
        viewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            updateRecyclerView(schedules)
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSchedules() {
        val schedules = viewModel.schedules.value?.filter { it.date == selectedDate }
        updateRecyclerView(schedules ?: emptyList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateRecyclerView(schedules: List<Schedule>) {
        val adapter = ScheduleAdapter(schedules, { schedule ->
            viewModel.schedules.value?.remove(schedule)
            viewModel.schedules.value = viewModel.schedules.value
            updatePayment()
        }, {
            showAddClassDialog()
        })

        binding.schedule.adapter = adapter
        binding.schedule.layoutManager = LinearLayoutManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddClassDialog() {
        val studentNames = viewModel.schedules.value?.map { it.name }?.distinct()?.toTypedArray() ?: emptyArray()
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
                val selectedStudent = studentNames[selectedStudentIndex]
                val date = selectedDate ?: run{
                    return@setPositiveButton
                }
                val hours = hoursInput.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                viewModel.schedules.value?.add(Schedule(selectedStudent, hours, date))
                updateSchedules()
                updatePayment()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updatePayment() {
        val totalSalary = calculateMonthlyPayment()
        totalSalaryTextView.text = "이번 달 과외비: ${totalSalary}원"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun calculateMonthlyPayment(): Int {
        var totalPayment: Double = 0.0
        val schedules = viewModel.schedules.value ?: emptyList()
        val hourlyWageMap = viewModel.hourlyWage.value?.toMap() ?: emptyMap()

        for (schedule in schedules) {
            val pay = hourlyWageMap[schedule.name] ?: 0.0
            val hours = schedule.hours
            totalPayment += hours * pay * 10000
        }
        return totalPayment.toInt()
    }
}