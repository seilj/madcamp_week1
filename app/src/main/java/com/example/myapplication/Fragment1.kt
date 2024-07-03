package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.Fragment1Binding
import com.example.myapplication.databinding.ItemRecyclerviewBinding
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class CustomAdapter(private var peopleList: MutableList<PeopleData>, private val onItemLongClicked: (Int) -> Unit) : RecyclerView.Adapter<CustomAdapter.Holder>() {
    override fun getItemCount(): Int {
        return peopleList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = peopleList[position]
        holder.bind(item)
        holder.itemView.setOnLongClickListener {
            onItemLongClicked(position)
            true
        }
    }

    inner class Holder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PeopleData) {
            binding.rvName.text = item.name
            binding.rvAge.text = "나이 : ${item.age}"
            binding.rvSchool.text = "학교 : ${item.school}"
            binding.rvSubject.text = "과목 : ${item.subject}"
            binding.rvPhonenum.text = "전화번호 : ${item.phoneNum}"
            binding.rvHourlywage.text = "시급 : ${item.hourlyWage}"
            binding.rvWeek.text = "요일 : ${item.week}"
            binding.rvHourpernumber.text = "시간/횟수 : ${item.hourPerNumber}"
        }
    }

    fun updateData(newPeopleList: MutableList<PeopleData>) {
        peopleList = newPeopleList
        notifyDataSetChanged()
    }
}

class Fragment1 : Fragment() {
    private lateinit var binding: Fragment1Binding
    private lateinit var peopleListAdapter: CustomAdapter
    private val listDataViewModel: ListDataViewModel by activityViewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment1Binding.inflate(inflater, container, false)
        setupRecyclerView()

        listDataViewModel.peopleList.observe(viewLifecycleOwner, Observer { peopleList ->
            peopleListAdapter.updateData(peopleList)
        })

        binding.studentAddition.setOnClickListener {
            showAddPersonDialog()
        }
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupRecyclerView() {
        val peopleList = listDataViewModel.getPeopleList()
        peopleListAdapter = CustomAdapter(peopleList) { position ->
            showOptionsDialog(position)
        }
        binding.rv.adapter = peopleListAdapter
        binding.rv.layoutManager = LinearLayoutManager(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showOptionsDialog(position: Int) {
        val studentName = listDataViewModel.getPeopleList()[position].name
        AlertDialog.Builder(requireContext())
            .setTitle("옵션 선택")
            .setItems(arrayOf("수정", "삭제")) { dialog, which ->
                when (which) {
                    0 -> showEditPersonDialog(position)
                    1 -> showDeleteDialog(position, studentName)
                }
                dialog.dismiss()
            }
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDeleteDialog(position: Int, studentName:String) {
        AlertDialog.Builder(requireContext())
            .setTitle("학생 삭제")
            .setMessage("$studentName 학생을 지우시겠습니까?")
            .setPositiveButton("OK") { dialog, _ ->
                deleteItem(position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun deleteItem(position: Int) {
        val peopleList = listDataViewModel.getPeopleList()
        val student = peopleList[position]
        listDataViewModel.deleteStudent(requireContext(), student)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showAddPersonDialog() {
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_person, null)
        val nameInput = view.findViewById<EditText>(R.id.edit_name)
        val ageInput = view.findViewById<EditText>(R.id.edit_age)
        val schoolInput = view.findViewById<EditText>(R.id.edit_school)
        val subjectInput = view.findViewById<EditText>(R.id.edit_subject)
        val phoneNumInput = view.findViewById<EditText>(R.id.edit_phoneNum)
        val hourlyWageInput = view.findViewById<EditText>(R.id.edit_hourlyWage)
        val weekSpinner = view.findViewById<Spinner>(R.id.spinner_week)
        val hourPerNumberInput = view.findViewById<EditText>(R.id.edit_hourPerNumber)

        setupEditTextFocusChange(nameInput, ageInput)
        setupEditTextFocusChange(ageInput, schoolInput)
        setupEditTextFocusChange(schoolInput, subjectInput)
        setupEditTextFocusChange(subjectInput, phoneNumInput)
        setupEditTextFocusChange(phoneNumInput, hourlyWageInput)
        setupEditTextFocusChange(hourlyWageInput, hourPerNumberInput)


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("새 학생 추가")
            .setView(view)
            .setPositiveButton("Add", null)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()

        dialog.setOnShowListener {
            val addButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            addButton.setOnClickListener {
                val name = nameInput.text.toString()
                var age = ageInput.text.toString().toIntOrNull()
                val school = schoolInput.text.toString()
                val subject = subjectInput.text.toString()
                val phoneNum = phoneNumInput.text.toString()
                var hourlyWage = hourlyWageInput.text.toString().toDoubleOrNull()
                val week = weekSpinner.selectedItem.toString()
                var hourPerNumber = hourPerNumberInput.text.toString().toDoubleOrNull()

                if (name.isEmpty()) {
                    Toast.makeText(context, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val existingPerson = listDataViewModel.getPeopleList().find { it.name == name }
                if (existingPerson != null) {
                    Toast.makeText(context, "이미 같은 이름의 학생이 존재합니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (age == null) {
                    age = 10
                    Toast.makeText(context, "기본 나이 10 설정", Toast.LENGTH_SHORT).show()
                }
                if (hourlyWage == null) {
                    hourlyWage = 3.0
                    Toast.makeText(context, "기본 시급 3 설정", Toast.LENGTH_SHORT).show()
                }
                if (hourPerNumber == null) {
                    hourPerNumber = 2.0
                    Toast.makeText(context, "기본 시간 2 설정", Toast.LENGTH_SHORT).show()
                }

                val newPerson = PeopleData(name, age, school, subject, phoneNum, hourlyWage, week, hourPerNumber)
                listDataViewModel.addStudent(requireContext(), newPerson)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showEditPersonDialog(position: Int) {
        val person = listDataViewModel.getPeopleList()[position]

        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_person, null)
        val nameInput = view.findViewById<EditText>(R.id.edit_name)
        val ageInput = view.findViewById<EditText>(R.id.edit_age)
        val schoolInput = view.findViewById<EditText>(R.id.edit_school)
        val subjectInput = view.findViewById<EditText>(R.id.edit_subject)
        val phoneNumInput = view.findViewById<EditText>(R.id.edit_phoneNum)
        val hourlyWageInput = view.findViewById<EditText>(R.id.edit_hourlyWage)
        val weekSpinner = view.findViewById<Spinner>(R.id.spinner_week)
        val hourPerNumberInput = view.findViewById<EditText>(R.id.edit_hourPerNumber)

        nameInput.setText(person.name)
        ageInput.setText(person.age.toString())
        schoolInput.setText(person.school)
        subjectInput.setText(person.subject)
        phoneNumInput.setText(person.phoneNum)
        hourlyWageInput.setText(person.hourlyWage.toString())
        // weekSpinner 설정 필요
        hourPerNumberInput.setText(person.hourPerNumber.toString())

        setupEditTextFocusChange(nameInput, ageInput)
        setupEditTextFocusChange(ageInput, schoolInput)
        setupEditTextFocusChange(schoolInput, subjectInput)
        setupEditTextFocusChange(subjectInput, phoneNumInput)
        setupEditTextFocusChange(phoneNumInput, hourlyWageInput)
        setupEditTextFocusChange(hourlyWageInput, hourPerNumberInput)

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("학생 정보 수정")
            .setView(view)
            .setPositiveButton("수정", null)
            .setNegativeButton("취소") { dialog, _ -> dialog.cancel() }
            .create()

        dialog.setOnShowListener {
            val editButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            editButton.setOnClickListener {
                val name = nameInput.text.toString()
                var age = ageInput.text.toString().toIntOrNull()
                val school = schoolInput.text.toString()
                val subject = subjectInput.text.toString()
                val phoneNum = phoneNumInput.text.toString()
                var hourlyWage = hourlyWageInput.text.toString().toDoubleOrNull()
                val week = weekSpinner.selectedItem.toString()
                var hourPerNumber = hourPerNumberInput.text.toString().toDoubleOrNull()

                if (name.isEmpty()) {
                    Toast.makeText(context, "이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val existingPerson = listDataViewModel.getPeopleList().find { it.name == name && it != person }
                if (existingPerson != null) {
                    Toast.makeText(context, "이미 같은 이름의 학생이 존재합니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (age == null) {
                    age = 10
                    Toast.makeText(context, "기본 나이 10 설정", Toast.LENGTH_SHORT).show()
                }
                if (hourlyWage == null) {
                    hourlyWage = 3.0
                    Toast.makeText(context, "기본 시급 3 설정", Toast.LENGTH_SHORT).show()
                }
                if (hourPerNumber == null) {
                    hourPerNumber = 2.0
                    Toast.makeText(context, "기본 시간 2 설정", Toast.LENGTH_SHORT).show()
                }

                val updatedPerson = PeopleData(name, age, school, subject, phoneNum, hourlyWage, week, hourPerNumber)
                listDataViewModel.updateStudent(requireContext(), updatedPerson, position)
                dialog.dismiss()
            }
        }

        dialog.show()
    }

    private fun setupEditTextFocusChange(currentEditText: EditText, nextEditText: EditText) {
        currentEditText.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT || event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                nextEditText.requestFocus()
                true
            } else {
                false
            }
        }
    }
}
