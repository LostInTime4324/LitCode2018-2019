package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import kotlin.collections.LinkedHashMap

object Variables {
    const val VARIABLE_PREFRENCES_TAG = "Variables"

    const val NUMBER_PREFERENCES = "Number Variable Preferences"
    const val BOOLEAN_PREFERENCES = "Boolean Variable Preferences"
    const val ENUM_PREFERENCES = "Enum Variable Preferences"

    val enumMap = LinkedHashMap<Class<*>, Enum<*>>()

    object enums : Map<Class<*>, Enum<*>> by LinkedHashMap<Class<*>, Enum<*>>() {
        inline operator fun <reified T : Enum<T>> invoke(): T = enumMap[T::class.java]!! as T
    }

    @JvmStatic
    fun init(context: Context) {
        context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE).edit().clear().apply()
        val numberPreferences = context.getSharedPreferences(NUMBER_PREFERENCES, Context.MODE_PRIVATE)
        NumberVariable.values().forEach { number ->
            val variableName = number.name
            val default = number.number
            if (numberPreferences.contains(variableName))
                number.number = numberPreferences.getFloat(variableName, default.toFloat()).toDouble()
        }
        val enumPreferences = context.getSharedPreferences(ENUM_PREFERENCES, Context.MODE_PRIVATE)
        EnumVariable::class.java.classes.forEach { enumClass ->
            val className = enumClass.simpleName
            val enumConstants = enumClass.enumConstants!!.map { it as Enum<*> }
            val savedEnumName = enumPreferences.getString(className, null)
            val default = enumConstants.first()
            enumMap[enumClass] = if (enumPreferences.contains(className))
                enumConstants.firstOrNull { it.name == savedEnumName!! } ?: default
            else
                default
        }
        val booleanPreferences = context.getSharedPreferences(BOOLEAN_PREFERENCES, Context.MODE_PRIVATE)
        BooleanVariable.values().forEach { variable ->
            val variableName = variable.name
            val default = variable.boolean
            if (booleanPreferences.contains(variableName))
                variable.boolean = booleanPreferences.getBoolean(variableName, default)
        }
    }
}