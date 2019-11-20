package com.brndl.hourplan


import kotlin.collections.HashMap


data class SubstitutionInfo(var type: SubstitutionType? = null, var representedTeacher: String? = null, var representedRoom: String? = null, var movedInfo: MovedInfo? = null)
data class MovedInfo(var day: Int, var index: Int)

enum class SubstitutionType {
    CANCELED, SUBSTITUTED, MOVED
}

object SubstitutionManger {

    var substitutions: HashMap<Int, HashMap<Int, SubstitutionInfo>> = hashMapOf()

    var returnVal = ""

    fun init() {

    }



}