package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences


object Variables {

    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val values = LinkedHashMap<String, Variable>()

    lateinit var preferences: SharedPreferences

    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, MODE_PRIVATE)
        VariableName.values().forEach {
            put(it.name)
        }
    }

    @JvmStatic
    operator fun get(variable: VariableName) = values[variable.name]?.num ?: variable.default

    fun put(name: String) {
        val default = VariableName.valueOf(name).default.toString()
        var number = preferences.getString(name, default)
        if(number == "0.0") number = default
        values[name] = Variable(number.toDouble(), name)
    }

    val AutoPoints = ArrayList<Vector>()
}

data class Variable(var num: Double, val name: String)