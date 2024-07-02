package com.example.myapplication

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.Fragment1Binding
import com.example.myapplication.databinding.ItemRecyclerviewBinding
import android.widget.EditText
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

    private fun setupRecyclerView() {
        val peopleList = listDataViewModel.getPeopleList()
        peopleListAdapter = CustomAdapter(peopleList) { position ->
            showDeleteDialog(position)
        }
        binding.rv.adapter = peopleListAdapter
        binding.rv.layoutManager = LinearLayoutManager(context)
    }

    private fun showDeleteDialog(position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton("OK") { dialog, _ ->
                deleteItem(position)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteItem(position: Int) {
        val peopleList = listDataViewModel.getPeopleList()
        val student = peopleList[position]
        listDataViewModel.deleteStudent(requireContext(), student)
        peopleListAdapter.notifyItemRemoved(position)
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
        val weekInput = view.findViewById<EditText>(R.id.edit_week)
        val hourPerNumberInput = view.findViewById<EditText>(R.id.edit_hourPerNumber)

        AlertDialog.Builder(requireContext())
            .setTitle("Add New Person")
            .setView(view)
            .setPositiveButton("Add") { dialog, _ ->
                val daysOfWeek: List<String> = listOf(
                    "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
                )

                val name = nameInput.text.toString()
                var age = ageInput.text.toString().toIntOrNull()
                val school = schoolInput.text.toString()
                val subject = subjectInput.text.toString()
                val phoneNum = phoneNumInput.text.toString()
                var hourlyWage = hourlyWageInput.text.toString().toDoubleOrNull()
                var week = weekInput.text.toString()
                var hourPerNumber = hourPerNumberInput.text.toString().toDoubleOrNull()

                if (age == null) {
                    age = 0
                    Toast.makeText(context, "default value 0 is added", Toast.LENGTH_SHORT).show()
                }
                if (hourlyWage == null) {
                    hourlyWage = 0.0
                    Toast.makeText(context, "default value 0.0 is added", Toast.LENGTH_SHORT).show()
                }
                if (hourPerNumber == null) {
                    hourPerNumber = 2.0
                    Toast.makeText(context, "default value 2.0 is added", Toast.LENGTH_SHORT).show()
                }
                if (!(week in daysOfWeek)) {
                    week = "Monday"
                    Toast.makeText(context, "default value Monday is added, please change the first letter to capital letters ", Toast.LENGTH_SHORT).show()
                }

                val newPerson = PeopleData(name, age, school, subject, phoneNum, hourlyWage, week, hourPerNumber)
                listDataViewModel.addStudent(requireContext(), newPerson)
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
            .create()
            .show()
    }
}
