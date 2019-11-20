package com.brndl.hourplan.subjects

import android.content.SharedPreferences
import com.brndl.hourplan.learning.LearningManager
import com.brndl.hourplan.timetable.SubjectItem
import com.brndl.hourplan.timetable.TimetableManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException
import java.lang.Exception

data class SubjectInfo(val name: String, val teacher: String, val color: Int)


object SubjectManager {

    lateinit var sharedPrefs: SharedPreferences

    private var subjects: MutableList<SubjectInfo> = ArrayList()

    fun addSubject(data: SubjectInfo, override: Int? = null): Boolean {
        if(subjects.size == 0) {
            subjects.add(data)
            LearningManager.subjectAdded(0)
            saveData()
            return true
        }
        if(override != null) {
            subjects[override] = data
            saveData()
            return true
        }
        for (i in 0..subjects.lastIndex) {
            val subj = subjects[i]
            val compared = subj.name.compareTo(data.name, true)
            println("compared ${subj.name} and ${data.name} to $compared")
            when {
                compared == 0 -> return false
                compared > 0 -> {
                    subjects.add(i, data)
                    TimetableManager.subjectAdded(i)
                    LearningManager.subjectAdded(i)
                    saveData()
                    return true
                }
            }
        }
        subjects.add(data)
        saveData()
        return true
    }

    fun getSubjectIndex(subject: String): Int? {
        for (i in 0..subjects.lastIndex) {
            if (subjects[i].name.equals(subject, true))
                return i
        }
        return null
    }

    fun getSubject(index: Int?): SubjectInfo? {
        return try {
            if (index != null) {
                subjects[index]
            } else {
                null
            }
        } catch (e : Exception) {
            null
        }
    }

    fun getSubjectsForAdapter(): MutableList<SubjectItem> {
        val returnList: MutableList<SubjectItem> = mutableListOf()
        for (subj in 0..subjects.lastIndex) {
            returnList.add(SubjectItem(subj))
            println("added ${getSubject(subj)?.name} to returnList")
        }
        return returnList
    }

    fun removeSubject(index: Int?): Boolean {
        return if(subjects.remove(getSubject(index))){
            if(index != null) {
                TimetableManager.subjectRemoved(index)
                LearningManager.subjectRemoved(index)
            }
            saveData()
            true
        } else {
            false
        }
    }

    private fun saveData() {
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(subjects)

        editor.putString("SUBJECTS", json)
        editor.apply()
    }

    private inline fun <reified T> Gson.fromJson(json: String) =
        this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun loadData() {
        val gson = Gson()
        val json = sharedPrefs.getString("SUBJECTS", "")
        if (json != null && json != "") {
            try {
                subjects = gson.fromJson<MutableList<SubjectInfo>>(json)


            } catch (e: Exception) {
                try {
                    val oldSubjects = gson.fromJson<HashMap<String, SubjectInfo>>(json)
                    for (old in oldSubjects.values) {
                        addSubject(old)
                    }
                    saveData()
                } catch (e:Exception) {
                    println("couldn't load anything")
                }
            }
        }
    }
}