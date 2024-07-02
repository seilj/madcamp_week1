package com.example.myapplication

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import android.content.Context as Context


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val listDataViewModel: ListDataViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setStatusBarColor()

        listDataViewModel.listInitialize(this)
        initView()
    }
    private fun setStatusBarColor() {
        // 상태창 색상을 원하는 색상으로 변경
        window.statusBarColor = ContextCompat.getColor(this, R.color.green)

        // 상태창 텍스트 색상 변경 (다크 모드에서는 밝은 텍스트를, 라이트 모드에서는 어두운 텍스트를 표시)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
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
        iconList.add(ContextCompat.getDrawable(this, R.drawable.list))
        iconList.add(ContextCompat.getDrawable(this, R.drawable.imagegallery))
        iconList.add(ContextCompat.getDrawable(this, R.drawable.calendar))

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when(position){
                0 -> "학생목록"
                1 -> "필기노트"
                else -> "일정관리"
            }
            tab.icon = iconList[position]
        }.attach()
    }

}