package com.example.myapplication

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


data class PeopleData(val name: String, val phoneNum:String)

class CustomAdapter(val peopleList: ArrayList<PeopleData>) : RecyclerView.Adapter<CustomAdapter.Holder>() {
    //profileList의 Size를 return하여 몇 명의 data가 존재하는지 확인
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
    override fun onBindViewHolder(holder: CustomAdapter.Holder, position: Int) {
        holder.name.text = peopleList[position].name
        holder.phonenum.text = peopleList[position].phoneNum

    }
    //binding 객체를 통해 각 View 요소에 접근하게 한다
    inner class Holder(val binding: ItemRecyclerviewBinding) : RecyclerView.ViewHolder(binding.root) {
        val name = binding.rvName
        val phonenum = binding.rvPhonenum
    }
}

class Fragment1: Fragment() {
    private lateinit var binding: Fragment1Binding
    private lateinit var peopleListAdapter: CustomAdapter
    val peopleList = ArrayList<PeopleData>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = Fragment1Binding.inflate(inflater, container, false)


        val assetManager = resources.assets
        val inputStream = assetManager.open("PeopleData.json")
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val peopleArray: Array<PeopleData> = gson.fromJson(jsonString, Array<PeopleData>::class.java)
        Log.d("Fragment1", "Loaded People Data: ${peopleList.joinToString { it.phoneNum }}")

        peopleList.addAll(peopleArray)

        binding.rv.adapter = CustomAdapter(peopleList)
        binding.rv.layoutManager = LinearLayoutManager(requireContext())

        setupRecyclerView()

        binding.studentAddition.setOnClickListener {
            // 새로운 데이터 추가
            val newPerson = PeopleData("New Person", "123-456-7890")
            addPerson(newPerson)
        }

        return binding.root
    }
    private fun setupRecyclerView() {
        peopleListAdapter = CustomAdapter(peopleList)
        binding.rv.adapter = peopleListAdapter
        binding.rv.layoutManager = LinearLayoutManager(context)
    }

    private fun addPerson(person: PeopleData) {
        peopleList.add(person)
        peopleListAdapter.notifyItemInserted(peopleList.size - 1)
    }
}