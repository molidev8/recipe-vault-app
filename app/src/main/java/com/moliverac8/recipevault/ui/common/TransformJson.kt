package com.moliverac8.recipevault.ui.common

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun List<String>.toJsonInstructions(): String {
    val gson = Gson()
    return gson.toJson(this)
}

fun String.toListOfInstructions(): List<String> {
    val gson = Gson()
    val type = object : TypeToken<List<String>>() {}.type
    return gson.fromJson(this, type)
}