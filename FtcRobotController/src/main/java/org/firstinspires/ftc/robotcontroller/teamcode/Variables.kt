package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object Variables {
    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val variables = LinkedHashMap<String, Double>()

    val enums = LinkedHashMap<String, Enum<*>>()

    lateinit var preferences: SharedPreferences

    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE)
        Number.values().forEach {
            put(it.name)
        }
        Variables::class.java.classes.filter { it !is Number }.forEach { enumClass ->
            enumClass.enumConstants.map { (it as Enum<*>).name }.forEach {

            }
        }
    }

    @JvmStatic
    operator fun get(name: String) = variables[name] ?: Number.valueOf(name).default

    operator fun <T: Enum<T>> get(enum: Enum<T>) {

    }

    @JvmStatic
    operator fun set(name: String, number: Double) {
        variables[name] = number
    }

    fun put(name: String) {
        val default = Number.valueOf(name).default.toString()
        val number = if (preferences.contains(name))
            preferences.getString(name, default)
        else
            default
        variables[name] = number.toDouble()
//        java.lang.Enum.valueOf()
    }


    enum class AutoType {

    }

    enum class Number(val default: Double = 0.0) {
        On_Depot_Side(1.0),
        Do_Both_Objectives(0.0),
        Using_Elevator(1.0),
        Mineral_Side,
        Test_Variable,
        Auto_Type,
        Drive_Power(0.5),
        Distance_To_Side_Mineral,
        Distance_To_Center_Minteral(29.0),
        Distance_Backwards_On_Center_Mineral(20.0),
        Distance_Backwards_On_Side_Mineral,
        Distance_To_Depot_On_Side,
        Distance_To_Depot_On_Center,
        Distance_To_Crater_On_Depot,
        Distance_To_Crater_On_Depot_No_Totem,
        Distance_To_Wall_On_Crater,
        Distance_To_Depot_On_Crater,
        Distance_To_Crater_On_Crater,
        Angle_To_Side_Mineral,
        Angle_To_Crater_On_Depot,
        Angle_To_Crater_On_Depot_No_Totem,
        Angle_To_Wall_On_Crater,
        Angle_To_Depot_On_Crater,
        Elevator_Move_Time(5.0),
        Elevator_Power(0.5),
        Totem_Move_Time(3.0),
        Totem_Power,
        Intake_Power,
        Encoder_Correction_Factor(1.0),
        Turn_Correction_Kd,
        Turn_Correction_Ki,
        Turn_Correction_Kp,
        Turn_Kd(0.001),
        Turn_Ki(0.0001),
        Turn_Kp(0.05),
        Drive_Kd,
        Drive_Kp,
        Drive_Ki;
    }
}