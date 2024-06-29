package com.example.myapplication

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.Fragment1Binding
import com.example.myapplication.databinding.ItemRecyclerviewBinding
import com.google.gson.Gson
import android.util.Log
import android.widget.EditText
import android.widget.Toast

class CustomAdapter(val peopleList: ArrayList<PeopleData>,private val onItemLongClicked : (Int)->Unit) : RecyclerView.Adapter<CustomAdapter.Holder>() {
    //peopleList의 Size를 return하여 몇 명의 data가 존재하는지 확인
    override fun getItemCount(): Int {
        return peopleList.size
    }

    //새로운 ViewHolder 객체 생성 및 ViewHolder의 레이아웃 inflate
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomAdapter.Holder {
        val binding = ItemRecyclerviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }
    //RecyclerView에서 각 value를 Binding 할 때 호출되는 함수로 position 매개변수를 통해
    //profileList에서 value를 꺼내고 각 View에 설정해준다.
    //각 item들이 길게 눌리면 position을 넘겨줌
    override fun onBindViewHolder(holder: CustomAdapter.Holder, position: Int) {
        val item = peopleList[position]
        holder.bind(item)
        holder.itemView.setOnLongClickListener {
            onItemLongClicked(position)
            true
        }
    }
    //binding 객체를 통해 각 View 요소에 접근하게 한다
    inner class Holder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PeopleData){
            binding.rvName.text = item.name
            binding.rvAge.text = "나이 : ${item.age}"
            binding.rvSchool.text = "학교 : ${item.school}"
            binding.rvSubject.text = "과목 : ${item.subject}"
            binding.rvPhonenum.text = "전화번호 : ${item.phoneNum}"
        }
    }
}

class Fragment1: Fragment() {
    private lateinit var binding: Fragment1Binding
    private lateinit var peopleListAdapter: CustomAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment1Binding.inflate(inflater, container, false)
        setupRecyclerView()
        //add button이 눌리는 걸 감지
        binding.studentAddition.setOnClickListener {
            // 새로운 데이터 추가
            showAddPersonDialog()
        }
        return binding.root
    }
    //recyclerview 관련 함수
    private fun setupRecyclerView() {
        val peopleList = (activity as MainActivity).getPeopleList()
        //꾹 눌리는 거 감지해서 삭제(넘겨준 position 사용)
        peopleListAdapter = CustomAdapter(peopleList) { position ->
            showDeleteDialog(position)
        }
        binding.rv.adapter = peopleListAdapter
        binding.rv.layoutManager = LinearLayoutManager(context)
    }
    //요소 delete 관련 함수
    private fun showDeleteDialog(position: Int) {
        //진짜 지울거임? 이라고 묻는 창
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
        val peopleList = (activity as MainActivity).getPeopleList()
        peopleList.removeAt(position)
        binding.rv.adapter?.notifyItemRemoved(position)
    }
    //요소 add 관련 함수
    private fun showAddPersonDialog() {
        //view라는 변수에 dialog_add_person.xml 파일을 담는 과정
        //layoutInflater를 통해 xml을 앱에서 사용할 수 있는 뷰 객체로 변환
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.dialog_add_person, null)

        //xml 파일에 있는 요소들을 각 요소의 id를 통해 접근한다
        val nameInput = view.findViewById<EditText>(R.id.edit_name)
        val ageInput = view.findViewById<EditText>(R.id.edit_age)
        val schoolInput = view.findViewById<EditText>(R.id.edit_school)
        val subjectInput = view.findViewById<EditText>(R.id.edit_subject)
        val phoneNumInput = view.findViewById<EditText>(R.id.edit_phoneNum)
        val hourlyWageInput = view.findViewById<EditText>(R.id.edit_hourlyWage)
        val weekInput = view.findViewById<EditText>(R.id.edit_week)
        val hourPerNumberInput = view.findViewById<EditText>(R.id.edit_hourPerNumber)

        AlertDialog .Builder(requireContext())
            .setTitle("Add New Person")
            .setView(view)
            //데이터를 넘겨주는 setPositiveButton에 대한 코드
            //데이터를 넘겨주지 않는 setNegativeButton에 대한 코드
            .setPositiveButton("Add") { dialog, _ ->
                //xml 파일의 edittext에 담긴 text들을 각 변수의 자료형에 맞게 변환하여 저장후

                val name = nameInput.text.toString()
                var age = ageInput.text.toString().toIntOrNull()
                val school = schoolInput.text.toString()
                val subject = subjectInput.text.toString()
                val phoneNum = phoneNumInput.text.toString()
                var hourlyWage = hourlyWageInput.text.toString().toDoubleOrNull()
                val week = weekInput.text.toString()
                var hourPerNumber = hourPerNumberInput.text.toString().toDoubleOrNull()

                //숫자 값들을 아무값이 안담기면 강제 종료되는데 이를 방지하기 위해 미입력시 default value를 설정
                if (age == null) {
                    age=15
                    Toast.makeText(context, "default value 15 is added", Toast.LENGTH_SHORT).show()
                }
                if (hourlyWage == null) {
                    hourlyWage=3.5
                    Toast.makeText(context, "default value 3.5 is added", Toast.LENGTH_SHORT).show()
                }
                if (hourPerNumber == null) {
                    hourPerNumber=2.0
                    Toast.makeText(context, "default value 2.0 is added", Toast.LENGTH_SHORT).show()
                }

                //새로운 클래스로 선언하여 arr에 추가
                val newPerson = PeopleData(name, age, school, subject, phoneNum,hourlyWage, week, hourPerNumber)
                addPerson(newPerson)
                //dialog 닫기
                dialog.dismiss()
            }
                //cancel누르면 닫는 로직
            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                //위에 있는걸 creat하고 show해준다
            .create()
            .show()
    }
    private fun addPerson(person: PeopleData) {
        val peopleList = (activity as MainActivity).getPeopleList()
        peopleList.add(person)
        peopleListAdapter.notifyItemInserted(peopleList.size - 1)

    }
}