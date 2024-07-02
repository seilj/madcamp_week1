package com.example.myapplication

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import android.content.Context as Context

data class PeopleData(val name: String,val age : Int,  val school:String,  val subject : String, val phoneNum:String, val hourlyWage : Double, val week : String, val hourPerNumber : Double)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var peopleList = ArrayList<PeopleData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        peopleList = readJsonFromFile(this,"PeopleData.json").toMutableList() as ArrayList<PeopleData>
//        loadPeopleData()
    }

    private fun initView() {
        val viewPager = binding.viewpager
        val tabLayout = binding.tabLayout

        val fragmentList = ArrayList<Fragment>()
        fragmentList.add(Fragment1())
        fragmentList.add(Fragment2())
        fragmentList.add(Fragment3())

        viewPager.adapter = ViewPagerAdapter(fragmentList, this)

        val iconList = ArrayList<Drawable?>()
        iconList.add(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground))
        iconList.add(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground))
        iconList.add(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "학생목록"
                1 -> "필기노트"
                else -> "과외비계산기"
            }
            tab.icon = iconList[position]
        }.attach()
    }
    //json 파일에 접근해서 값 저장
    private fun loadPeopleData() {
        val json = assets.open("PeopleData.json").bufferedReader().use { it.readText() }
        val gson = Gson()
        val listType = object : TypeToken<ArrayList<PeopleData>>() {}.type
        val data = gson.fromJson<ArrayList<PeopleData>>(json, listType)
        peopleList.addAll(data)
    }
    fun getPeopleList(): ArrayList<PeopleData> {
        return peopleList
    }
    fun readJsonFromFile(context: Context, fileName: String): List<PeopleData> {
        val jsonFile = File(context.filesDir, fileName)
        if (!jsonFile.exists()) {
            return emptyList()
        }

        val json = jsonFile.readText()
        val gson = Gson()
        val studentListType = object : TypeToken<List<PeopleData>>() {}.type
        return gson.fromJson(json, studentListType)
    }

    fun writeJsonToFile(context: Context, fileName: String, students: List<PeopleData>) {
        val jsonFile = File(context.filesDir, fileName)
        val gson = Gson()
        val jsonString = gson.toJson(students)
        jsonFile.writeText(jsonString)
    }
    fun addStudent(context: Context, fileName: String, student: PeopleData) {
        // JSON 파일에서 기존 데이터를 읽어옴
        val students = readJsonFromFile(context, fileName).toMutableList()

        // 새로운 학생 데이터를 추가
        students.add(student)

        // 업데이트된 데이터를 JSON 파일에 씀
        writeJsonToFile(context, fileName, students)
    }
}