package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.Fragment3Binding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class Fragment3 : Fragment() {
    private lateinit var binding: Fragment3Binding
    private val viewModel: ListDataViewModel by activityViewModels()
    private lateinit var totalSalaryTextView: TextView
    private var selectedDate: LocalDate? = null
    private lateinit var calendarView: CalendarView
    private lateinit var adapter: ScheduleAdapter

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

        // 어댑터 초기화 및 RecyclerView 설정
        adapter = ScheduleAdapter(emptyList(), { schedule ->
            val updatedSchedules = viewModel.schedules.value?.toMutableList() ?: mutableListOf()
            updatedSchedules.remove(schedule)
            viewModel.schedules.value = updatedSchedules
            updatePayment()
        }, {
            showAddClassDialog()
        })

        binding.schedule.adapter = adapter
        binding.schedule.layoutManager = LinearLayoutManager(requireContext())

        updatePayment()
        // Observe schedules LiveData
        viewModel.schedules.observe(viewLifecycleOwner) { schedules ->
            updateSchedules()
            updatePayment()
        }

        viewModel.peopleList.observe(viewLifecycleOwner) {
            updateSchedules()
            updatePayment()
        }

        viewModel.hourlyWage.observe(viewLifecycleOwner){
            updatePayment()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateSchedules() {
        val schedules = viewModel.schedules.value?.filter { it.date == selectedDate }
        adapter.updateData(schedules ?: emptyList())
        viewModel.writeSchedulesToFile(requireContext()) // 스케줄 데이터 저장
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddClassDialog() {
        val studentNames = viewModel.peopleList.value?.map { it.name }?.distinct()?.toTypedArray() ?: emptyArray()
        if (studentNames.isEmpty()) {
            Toast.makeText(requireContext(), "학생이 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
            return
        }
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
                val date = selectedDate ?: run {
                    return@setPositiveButton
                }
                val hours = hoursInput.text.toString().toDoubleOrNull() ?: return@setPositiveButton
                val newSchedule = Schedule(selectedStudent, hours, date)
                val updatedSchedules = viewModel.schedules.value?.toMutableList() ?: mutableListOf()
                updatedSchedules.add(newSchedule)
                viewModel.schedules.value = updatedSchedules
                viewModel.writeSchedulesToFile(requireContext()) // 스케줄 데이터 저장
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