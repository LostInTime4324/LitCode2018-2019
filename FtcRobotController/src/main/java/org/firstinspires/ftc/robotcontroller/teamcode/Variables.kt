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
        VariableNames.values().forEach {
            put(it.name)
        }
    }

    @JvmStatic
    operator fun get(variable: VariableNames): Double {
        return values[variable.name]?.num ?: 0.0
    }

    fun put(name: String) {
        val number = preferences.getString(name, "0.0")
        values[name] = Variable(number.toDouble(), name)
    }

    val AutoPoints = ArrayList<Vector>()
}

data class Variable(var num: Double, val name: String)