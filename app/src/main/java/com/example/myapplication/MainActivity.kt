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

data class PeopleData(val name: String,val age : Int,  val school:String,  val subject : String, val phoneNum:String, val hourlyWage : Double, val week : String, val hourPerNumber : Double)

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val peopleList = ArrayList<PeopleData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        loadPeopleData()
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
}