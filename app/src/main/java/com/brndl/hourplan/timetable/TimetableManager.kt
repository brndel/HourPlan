package com.brndl.hourplan.timetable

import android.content.Context
import android.content.SharedPreferences
import com.brndl.hourplan.R
import com.brndl.hourplan.subjects.SubjectManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

data class SubjectItem(var subject: Int, var room: String? = null)

object TimetableManager {

    lateinit var sharedPrefs: SharedPreferences

    var timetable: ArrayList<MutableList<SubjectItem>> =
        arrayListOf(ArrayList(), ArrayList(), ArrayList(), ArrayList(), ArrayList())

    fun setSubjectAtDay(day: Int, index: Int, subject: SubjectItem): Boolean {
        if (timetable.lastIndex < day) return false
        if (timetable[day].lastIndex < day) return false
        timetable[day][index] = subject
        saveData()
        return true
    }

    fun addSubjectAtDay(day: Int, subject: SubjectItem) {
        timetable[day].add(subject)
        saveData()
    }

    fun removeSubjectAtDay(day: Int, position: Int) {
        timetable[day].removeAt(position)
        saveData()
    }

    fun switchSubjects(day: Int, from: Int, to: Int) {
        val list = timetable[day]
        Collections.swap(list, from, to)
        saveData()

    }

    fun subjectAdded(index: Int) {
        for (day in timetable)
            for (subj in day) {
                if (subj.subject >= index)
                    subj.subject += 1
            }
    }


    fun subjectRemoved(index: Int) {
        for (day in timetable)
            for (subj in day) {
                if (subj.subject == index)
                    subj.subject = -1
                if (subj.subject > index)
                    subj.subject -= 1
            }
    }


    fun saveData() {
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(timetable)

        editor.putString("TIMETABLE", json)
        editor.apply()
    }

    inline fun <reified T> Gson.fromJson(json: String) =
        this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun loadData() {
        val gson = Gson()
        val json = sharedPrefs.getString("TIMETABLE", "")
        if (json != null && json != "") {
            try {
                timetable = gson.fromJson<ArrayList<MutableList<SubjectItem>>>(json)
            } catch (e: Exception) {
                try {
                    data class OldSubjectItem(var subject: String, var room: String? = null)

                    val oldTimetable = gson.fromJson<ArrayList<MutableList<OldSubjectItem>>>(json)
                    for (oldDay in 0..oldTimetable.lastIndex) {
                        for (old in oldTimetable[oldDay]) {
                            if (old != null) {
                                val subjectIndex: Int? = SubjectManager.getSubjectIndex(old.subject)
                                if (subjectIndex != null)
                                    timetable[oldDay].add(SubjectItem(subjectIndex, old.room))
                                else
                                    println("something went wrong lul")
                            } else {
                                println("old == null")
                            }
                        }
                    }
                    saveData()
                } catch (e: Exception) {
                    println("couldn't load anything")
                }
            }
        }
    }

    fun indexOfDay(dayOfWeek: Int, returnWeekend: Boolean = false): Int {
        return when (dayOfWeek) {
            Calendar.MONDAY -> 0
            Calendar.TUESDAY -> 1
            Calendar.WEDNESDAY -> 2
            Calendar.THURSDAY -> 3
            Calendar.FRIDAY -> 4
            Calendar.SATURDAY -> if (returnWeekend) 5
            else 0
            Calendar.SUNDAY -> if (returnWeekend) 6
            else 0
            else -> 0
        }
    }

    fun dayString(dayOfWeek: Int, context: Context, short: Boolean = false): String {
        when (dayOfWeek) {
            0 -> return if (short) context.getString(R.string.monday_short)
            else context.getString(R.string.monday)
            1 -> return if (short) context.getString(R.string.tuesday_short)
            else context.getString(R.string.tuesday)
            2 -> return if (short) context.getString(R.string.wednesday_short)
            else context.getString(R.string.wednesday)
            3 -> return if (short) context.getString(R.string.thursday_short)
            else context.getString(R.string.thursday)
            4 -> return if (short) context.getString(R.string.friday_short)
            else context.getString(R.string.friday)
            5 -> return if (short) context.getString(R.string.saturday_short)
            else context.getString(R.string.saturday)
            6 -> return if (short) context.getString(R.string.sunday_short)
            else context.getString(R.string.sunday)

        }
        return "day not found D:"
    }
}