package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*

object Variables {
    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val variables = LinkedHashMap<String, Double>()

    operator fun get(numberVariable: NumberVariable) = variables[numberVariable.name] ?: numberVariable.default

    object vars {
        inline operator fun get(numberVariable: NumberVariable) = variables[numberVariable.name] ?: numberVariable.default
    }

    val enumMap = LinkedHashMap<Class<*>, Enum<*>>()
    object enums {
        inline operator fun <reified T: Enum<T>> invoke(): Enum<*> = enumMap[T::class.java]!! as Enum<T>

    }

    lateinit var preferences: SharedPreferences



    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE)
        NumberVariable.values().forEach {
            put(it.name)
        }
        EnumVariable::class.java.classes.filter { it is Enum<*>}.forEach { enumClass ->
            enumMap[enumClass] = enumClass.enumConstants!!.first() as Enum<*>
        }
        Log.i(VARIABLE_PREFRENCES_TAG, enumMap.toList().joinToString { (key, value) ->
            "Key: $key Value: $value\n"
        })
    }

    @JvmStatic
    operator fun set(name: String, number: Double) {
        variables[name] = number
    }

    fun put(name: String) {
        val default = NumberVariable.valueOf(name).default.toString()
        val number = if (preferences.contains(name))
            preferences.getString(name, default)
        else
            default
        variables[name] = number.toDouble()
    }



}