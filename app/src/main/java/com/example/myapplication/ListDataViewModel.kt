package com.example.myapplication

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

data class PeopleData(
    val name: String,
    val age: Int,
    val school: String,
    val subject: String,
    val phoneNum: String,
    val hourlyWage: Double,
    val week: String,
    val hourPerNumber: Double
)

class ListDataViewModel : ViewModel() {
    val peopleList = MutableLiveData<MutableList<PeopleData>>(mutableListOf())
    val schedules = MutableLiveData<MutableList<Schedule>>(mutableListOf())
    val hourlyWage = MutableLiveData<MutableList<Pair<String, Double>>>(mutableListOf())

    @RequiresApi(Build.VERSION_CODES.O)
    fun listInitialize(context: Context) {
        loadPeopleData(context)
        val studentList = peopleList.value ?: emptyList<PeopleData>()
        for(student in studentList)
            updateStudentSchedule(student)
    }

    fun getPeopleList(): MutableList<PeopleData>? {
        return peopleList.value?: mutableListOf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addStudent(context: Context, student: PeopleData) {
        // 새로운 학생 데이터를 추가
        peopleList.value?.add(student)
        updateStudentSchedule(student)
        // 업데이트된 데이터를 JSON 파일에 씀
        writeJsonToFile(context, "PeopleData.json", peopleList.value)
    }

    fun deleteStudent(context: Context, student: PeopleData) {
        // 새로운 학생 데이터를 추가
        peopleList.value?.remove(student)
        schedules.value = schedules.value?.filterNot { it.name == student.name }?.toMutableList()
        hourlyWage.value = hourlyWage.value?.filterNot { it.first == student.name }?.toMutableList()
        // 업데이트된 데이터를 JSON 파일에 씀
        writeJsonToFile(context, "PeopleData.json", peopleList.value)
    }

    // JSON 파일에 접근해서 값 저장
    private fun loadPeopleData(context: Context) {
        val data = readJsonFromFile(context, "PeopleData.json")
        peopleList.value?.addAll(data)
    }

    private fun readJsonFromFile(context: Context, fileName: String): List<PeopleData> {
        val jsonFile = File(context.filesDir, fileName)
        if (!jsonFile.exists()) {
            return emptyList()
        }
        return try {
            val json = jsonFile.readText()
            val gson = Gson()
            val studentListType = object : TypeToken<List<PeopleData>>() {}.type
            gson.fromJson(json, studentListType)
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    private fun writeJsonToFile(context: Context, fileName: String, students: MutableList<PeopleData>?) {
        val jsonFile = File(context.filesDir, fileName)
        try {
            val gson = Gson()
            val jsonString = gson.toJson(students)
            jsonFile.writeText(jsonString)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateStudentSchedule(student: PeopleData){
        val yearMonth = YearMonth.now()
        val daysInMonth = yearMonth.lengthOfMonth()
        for (day in 1..daysInMonth) {
            val date = LocalDate.of(yearMonth.year, yearMonth.monthValue, day)
            val formatter = DateTimeFormatter.ofPattern("EEEE")
            val dayOfWeekString = date.format(formatter)
            if (student.week == dayOfWeekString)
                schedules.value?.add(Schedule(student.name, student.hourPerNumber, date))
        }

        hourlyWage.value?.add(Pair(student.name, student.hourlyWage))
    }
}
