package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.SeekBar
import org.firstinspires.ftc.robotcontroller.teamcode.VariableControlActivity.Companion.scrollBarRange
import java.math.BigDecimal
import java.math.MathContext
import java.util.*


object Variables {

    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val values = HashMap<String, Variable>()

    var preferences: SharedPreferences? = null

    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, MODE_PRIVATE)
        Names.values().forEach {
            put(it.name)
        }
    }

    operator fun get(variable: Names): Double {
        return values[variable.name]!!.num
    }

    internal fun put(name: String) {
        var number = preferences!!.getString(name, "0.0")
        if (number == "") number = "0.0"
        values.put(name, Variable(number.toDouble(), name))
    }

}

enum class Names {
    Turn_Kp,
    Turn_Kd,
    Turn_Ki,
    Drive_Kp,
    Drive_Kd,
    Drive_Ki,
    Front_Left_Motor_Kp,
    Front_Left_Motor_Kd,
    Front_Left_Motor_Ki,
    Back_Left_Motor_Kp,
    Back_Left_Motor_Kd,
    Back_Left_Motor_Ki,
    Front_Right_Motor_Kp,
    Front_Right_Motor_Kd,
    Front_Right_Motor_Ki,
    Back_Right_Motor_Kp,
    Back_Right_Motor_Kd,
    Back_Right_Motor_Ki,
}

class Variable(num: Double, val name: String) {
    var num = num
        set(value) {
            if (value != num) {
                val text = value.toString()
                val number: Double
                if(text.indexOf(".") == text.length - 2) {
                    number = text.replace(".0", "").toDouble()
                } else if(value < 1) {
                    number= BigDecimal(value).round(MathContext(3)).toDouble()
                } else {
                    number = (Math.round(value * 100.0) / 100.0)
                }

                field = number

                editText!!.text!!.replace(0, editText!!.text!!.length, number.toString())

                scrollBar!!.progress = range.mapTo(number, scrollBarRange).toInt()
            }
        }

    var editText: EditText? = null

    var scrollBar: SeekBar? = null

    private var rangeNum = num

    val range: Range
        get() {
            with(name) {
                if (contains("ANGLE")) return Range(rangeNum - 10, rangeNum + 10)
                if (contains("DISTANCE")) return Range(rangeNum - 15, rangeNum + 15)
            }

            return Range(0.0, rangeNum * 2)
        }

    fun resetRange(): Range {
        rangeNum = num
        return range
    }
}