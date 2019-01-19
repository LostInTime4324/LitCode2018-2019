package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.*

object Variables {
    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val numbers = LinkedHashMap<NumberVariable, Double>()

    object vars {
        inline operator fun get(numberVariable: NumberVariable) = numbers[numberVariable] ?: numberVariable.default
    }

    val enumMap = LinkedHashMap<Class<*>, Enum<*>>()

    object enums {
        inline operator fun <reified T : Enum<T>> invoke(): Enum<*> = enumMap[T::class.java]!! as Enum<T>

    }

    lateinit var preferences: SharedPreferences


    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE)
        NumberVariable.values().forEach { variable ->
            val variableName = variable.name
            val default = variable.default.toString()
            val number = if (preferences.contains(variableName))
                preferences.getString(variableName, default)
            else
                default
            numbers[variable] = number.toDouble()
        }
        EnumVariable::class.java.classes.forEach { enumClass ->
            val className = enumClass.simpleName
            val enumConstants = enumClass.enumConstants!!.map { it as Enum<*> }
            val savedEnumName = preferences.getString(className, null)
            enumMap[enumClass] = if (preferences.contains(className))
                enumConstants.first { it.name == savedEnumName!! }
            else
                enumConstants.first()

        }
        Log.i(VARIABLE_PREFRENCES_TAG, enumMap.toList().joinToString { (key, value) ->
            "Key: $key Value: $value\n"
        })
    }
}