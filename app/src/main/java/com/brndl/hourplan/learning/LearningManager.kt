package com.brndl.hourplan.learning


import android.content.SharedPreferences
import android.net.Uri
import com.brndl.hourplan.TextItem
import com.brndl.hourplan.subjects.SubjectManager
import com.brndl.hourplan.timetable.SubjectItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

data class Topic(
    val name: String,
    val tooltip: String = "",
    val color: Int? = null,
    val learnables: MutableList<Learnable> = mutableListOf()
)

data class Learnable(val question: String, val answer: String, val extraInfo: String? = null, val ImageUri: String? = null) :
    Serializable

fun toTextItemArrayList(list: ArrayList<Learnable>): ArrayList<TextItem> {
    val returnList: ArrayList<TextItem> = ArrayList()
    for (item in list) {
        returnList.add(TextItem(item.question, item.answer, item.extraInfo))
    }
    return returnList
}

object LearningManager {

    lateinit var sharedPrefs: SharedPreferences


    private var learnables: HashMap<Int, MutableList<Topic>> = HashMap()


    fun getTopicIndex(subject: Int, topicName: String): Int? {
        val list: MutableList<Topic>? = learnables[subject]
        if (list != null) {
            for (t in 0..list.lastIndex) {
                if (list[t].name == topicName)
                    return t
            }
        }
        return null
    }

    fun getLearnableSubjectArray(): ArrayList<SubjectItem> {
        val list: ArrayList<SubjectItem> = ArrayList()
        for (subject in learnables.keys) {
            list.add(SubjectItem(subject))
        }
        return list
    }

    fun getTopicsOfSubjectAsTextItems(subject: Int): ArrayList<TextItem> {
        val list: ArrayList<TextItem> = ArrayList()
        val topics: MutableList<Topic>? = learnables[subject]
        if (topics != null)
            for (topic in topics) {
                list.add(
                    TextItem(
                        topic.name,
                        topic.learnables.size.toString(),
                        topic.tooltip,
                        topic.color
                    )
                )
            }
        return list
    }

    fun getLearnablesAsTextItems(subject: Int, topic: String): ArrayList<TextItem> {
        val list: ArrayList<TextItem> = ArrayList()
        val index = getTopicIndex(subject, topic)
        if (index != null) {
            val learnable: MutableList<Learnable>? = learnables[subject]?.get(index)?.learnables
            if (learnable != null) {
                for (learn in learnable) {
                    list.add(TextItem(learn.question, learn.answer, learn.extraInfo))
                }
            }
        }
        return list
    }

    fun getLearnablesOfTopic(subject: Int, topic: String): ArrayList<Learnable> {
        val list: ArrayList<Learnable> = ArrayList()
        val index = getTopicIndex(subject, topic)
        if (index != null) {
            val learnable: MutableList<Learnable>? = learnables[subject]?.get(index)?.learnables
            if (learnable != null) {
                for (learn in learnable) {
                    list.add(learn.copy())
                }
            }
        }
        return list
    }

    fun getLearnableOfTopicWithIndex(subject: Int, topic: String, index: Int): Learnable? {
        val Index = getTopicIndex(subject, topic)
        if (Index != null) {
            return learnables[subject]?.get(Index)?.learnables?.get(index)
        }
        return null
    }

    fun addSubject(subject: Int) {
        learnables[subject] = ArrayList()
        saveData()
    }

    fun removeSubject(subject: Int) {
        learnables.remove(subject)
        saveData()
    }

    fun addTopic(
        subject: Int,
        topic: String,
        tooltip: String = "",
        color: Int? = null,
        index: Int? = null
    ): Boolean {
        if (getTopicIndex(subject, topic) == null) {
            if (index == null)
                learnables.get(subject)?.add(Topic(topic, tooltip, color))
            else
                learnables.get(subject)?.add(index, Topic(topic, tooltip, color))
            saveData()
            return true
        } else {
            return false
        }

    }

    fun removeTopic(subject: Int, topic: String) {
        var index = getTopicIndex(subject, topic)
        if (index != null) {
            learnables[subject]?.removeAt(index)
            saveData()
        }
    }

    fun addLearnable(subject: Int, topic: String, learnable: Learnable, setIndex: Int? = null) {
        val index = getTopicIndex(subject, topic)
        if (index != null) {
            if (setIndex == null || setIndex == -1) {
                learnables[subject]?.get(index)?.learnables?.add(learnable)
            } else {
                learnables[subject]?.get(index)?.learnables?.set(setIndex, learnable)
            }
            saveData()
        }
    }

    fun removeLearnable(subject: Int, topic: String, learnableIndex: Int) {
        val index = getTopicIndex(subject, topic)
        if (index != null) {
            learnables[subject]?.get(index)?.learnables?.removeAt(learnableIndex)
            saveData()
        }
    }

    fun addLearnables(subject: Int, topic: String, learnablesList: MutableList<Learnable>) {
        val index = getTopicIndex(subject, topic)
        if (index != null) {
            for (learnable in learnablesList) {
                learnables[subject]?.get(index)?.learnables?.add(learnable)
            }
        }
        saveData()
    }

    fun switchTopics(subject: Int, position_dragged: Int, position_target: Int) {
        val list = learnables[subject]
        if (list != null) {
            Collections.swap(list, position_dragged, position_target)
            saveData()
        }
    }

    fun switchLearnables(subject: Int, topic: String, position_dragged: Int, position_target: Int) {
        val index = getTopicIndex(subject, topic)
        if (index != null) {
            val list = learnables[subject]?.get(index)?.learnables
            if (list != null) {
                Collections.swap(list, position_dragged, position_target)
                saveData()
            }
        }
    }

    fun subjectAdded(index: Int) {
        for (l in learnables.keys.reversed()) {
            if (l >= index) {
                learnables[l + 1] = learnables[l]!!
                learnables.remove(l)
            }
        }
        saveData()
    }

    fun subjectRemoved(index: Int) {
        for (l in learnables.keys.toList()) {
            if (l > index) {
                learnables[l - 1] = learnables[l]!!
            }
            if (l >= index) {
                learnables.remove(l)
            }
        }
        saveData()
    }


    private fun saveData() {
        val editor = sharedPrefs.edit()
        val gson = Gson()
        val json: String = gson.toJson(learnables)

        editor.putString("LEARNABLES", json)
        editor.apply()
    }

    inline fun <reified T> Gson.fromJson(json: String) =
        this.fromJson<T>(json, object : TypeToken<T>() {}.type)

    fun loadData() {
        val gson = Gson()
        val json = sharedPrefs.getString("LEARNABLES", "")
        if (json != null && json != "") {
            try {
                learnables = gson.fromJson<HashMap<Int, MutableList<Topic>>>(json)
            } catch (e: Exception) {
                try {
                    val oldLearnables = gson.fromJson<HashMap<String, MutableList<Topic>>>(json)
                    for (old in oldLearnables.keys) {
                        learnables[SubjectManager.getSubjectIndex(old)!!] = oldLearnables[old]!!
                    }
                    saveData()
                } catch (e: Exception) {
                    println("couldn't load anything")
                }
            }
        }
    }
}