package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.firstinspires.ftc.robotcontroller.teamcode.opmodes.VariableEnums
import org.firstinspires.ftc.robotcontroller.teamcode.opmodes.VariableEnums.*
import java.util.*

object Variables {
    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val variables = LinkedHashMap<String, Double>()

    val enumMap = LinkedHashMap<Class<*>, Enum<*>>()

    val enumClasses = VariableEnums::class.java.classes.filter { it !is Number && it is Enum<*>}.forEach { enumClass ->
        enumMap[enumClass] = enumClass.enumConstants.first() as Enum<*>
    }

    object enums {
        inline operator fun <reified T: Enum<T>> invoke() = enumMap[T::class.java]
    }

    lateinit var preferences: SharedPreferences

    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE)
        Variable.values().forEach {
            put(it.name)
        }
        enums<VariableEnums.AutoType>()
        Log.i(VARIABLE_PREFRENCES_TAG, enumMap.toList().joinToString { (key, value) ->
            "Key: $key Value: $value\n"
        })
    }

    @JvmStatic
    operator fun get(name: String) = variables[name] ?: Variable.valueOf(name).default

    operator fun <T: Enum<T>> get(enum: Enum<T>) {

    }

    @JvmStatic
    operator fun set(name: String, number: Double) {
        variables[name] = number
    }

    fun put(name: String) {
        val default = Variable.valueOf(name).default.toString()
        val number = if (preferences.contains(name))
            preferences.getString(name, default)
        else
            default
        variables[name] = number.toDouble()
//        java.lang.Enum.valueOf()
    }



}