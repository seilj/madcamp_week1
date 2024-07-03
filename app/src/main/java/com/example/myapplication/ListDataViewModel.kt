package com.example.myapplication

import android.content.Context
import android.os.Build
import android.util.Log
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

data class Schedule(
    val name: String, val hours: Double, val date: LocalDate
){
    @RequiresApi(Build.VERSION_CODES.O)
    fun toScheduleData(): ScheduleData{
        return ScheduleData(
            name = this.name,
            hours = this.hours,
            date = this.date.format(DateTimeFormatter.ISO_DATE) // LocalDate를 String으로 변환
        )
    }
}

data class ScheduleData(
    val name: String,
    val hours: Double,
    val date: String
){
    @RequiresApi(Build.VERSION_CODES.O)
    fun toSchedule(): Schedule{
        return Schedule(name = this.name, hours = this.hours, date = LocalDate.parse(this.date, DateTimeFormatter.ISO_DATE))
    }
}

class ListDataViewModel : ViewModel() {
    val peopleList = MutableLiveData<MutableList<PeopleData>>(mutableListOf())
    val schedules = MutableLiveData<MutableList<Schedule>>(mutableListOf())
    val hourlyWage = MutableLiveData<MutableList<Pair<String, Double>>>(mutableListOf())

    @RequiresApi(Build.VERSION_CODES.O)
    fun listInitialize(context: Context) {
        loadPeopleData(context)
        loadSchedulesData(context)
        val students = peopleList.value ?: mutableListOf()
        val wage = mutableListOf<Pair<String, Double>>()
        for(student in students){
            wage.add(Pair(student.name, student.hourlyWage))
        }
        hourlyWage.value = wage
    }

    fun getPeopleList(): MutableList<PeopleData> {
        return peopleList.value ?: mutableListOf()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addStudent(context: Context, student: PeopleData) {
        // 새로운 학생 데이터를 추가
        val updatedList = peopleList.value ?: mutableListOf()
        updatedList.add(student)
        peopleList.value = updatedList
        updateStudentSchedule(student)
        // 업데이트된 데이터를 JSON 파일에 씀
        writeJsonToFile(context, "PeopleData.json", updatedList)
        writeSchedulesToFile(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteStudent(context: Context, student: PeopleData) {
        // 학생 데이터를 삭제
        val updatedList = peopleList.value ?: mutableListOf()
        updatedList.remove(student)
        peopleList.value = updatedList
        schedules.value = schedules.value?.filterNot { it.name == student.name }?.toMutableList()
        hourlyWage.value = hourlyWage.value?.filterNot { it.first == student.name }?.toMutableList()
        // 업데이트된 데이터를 JSON 파일에 씀
        writeJsonToFile(context, "PeopleData.json", updatedList)
        writeSchedulesToFile(context)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun writeSchedulesToFile(context: Context) {
        writeJsonToFile(context, "SchedulesData.json", schedules.value?.map { it.toScheduleData() })
    }


    // JSON 파일에 접근해서 값 저장
    private fun loadPeopleData(context: Context) {
        val data = readJsonFromFile(context, "PeopleData.json", PeopleData::class.java)
        peopleList.value = data.toMutableList()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadSchedulesData(context: Context) {
        val data = readJsonFromFile(context, "SchedulesData.json", ScheduleData::class.java)
        schedules.value = data.map { it.toSchedule() }.toMutableList()
    }

    private fun <T> readJsonFromFile(context: Context, fileName: String, clazz: Class<T>): List<T> {
        val jsonFile = File(context.filesDir, fileName)
        if (!jsonFile.exists()) {
            return emptyList()
        }
        return try {
            val json = jsonFile.readText()
            val gson = Gson()
            val listType = TypeToken.getParameterized(List::class.java, clazz).type
            gson.fromJson(json, listType) ?: emptyList()
        } catch (e: IOException) {
            e.printStackTrace()
            emptyList()
        }
    }

    // JSON 파일에 접근해서 값 저장
    private fun <T> writeJsonToFile(context: Context, fileName: String, data: List<T>?) {
        val jsonFile = File(context.filesDir, fileName)
        try {
            val gson = Gson()
            val jsonString = gson.toJson(data)
            jsonFile.writeText(jsonString)
            Log.d("FileWrite", "Successfully wrote JSON to file: $jsonFile")
        } catch (e: IOException) {
            Log.e("FileWrite", "Error writing JSON to file: $jsonFile", e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateStudentSchedule(student: PeopleData){
        Log.d("Basycsyntax","update Student Schedule")
        val yearMonth = YearMonth.now()
        val daysInMonth = yearMonth.lengthOfMonth()
        for (day in 1..daysInMonth) {
            val date = LocalDate.of(yearMonth.year, yearMonth.monthValue, day)
            val formatter = DateTimeFormatter.ofPattern("EEEE")
            val dayOfWeekString = date.format(formatter)
            Log.d("Basycsyntax", "day of week: $dayOfWeekString")
            if (student.week == dayOfWeekString) {
                Log.d("Basycsyntax","add regular schedule")
                val updatedSchedules = schedules.value ?: mutableListOf()
                updatedSchedules.add(Schedule(student.name, student.hourPerNumber, date))
                schedules.value = updatedSchedules
            }
        }

        val updatedWages = hourlyWage.value ?: mutableListOf()
        updatedWages.add(Pair(student.name, student.hourlyWage))
        hourlyWage.value = updatedWages
    }
}