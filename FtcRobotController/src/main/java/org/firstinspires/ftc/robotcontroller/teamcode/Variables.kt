package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.EditText
import android.widget.SeekBar
import org.firstinspires.ftc.robotcontroller.teamcode.activites.VariableControlActivity.Companion.scrollBarRange
import java.math.BigDecimal
import java.math.MathContext
import java.util.*
import kotlin.collections.ArrayList


object Variables {

    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val values = HashMap<String, Variable>()

    lateinit var preferences: SharedPreferences

    @JvmStatic
    fun init(context: Context) {
        preferences = context.getSharedPreferences(VARIABLE_PREFRENCES_TAG, MODE_PRIVATE)
        Numbers.values().forEach {
            put(it.name)
        }
    }

    operator fun get(variable: Numbers): Double {
        return values[variable.name]!!.num
    }

    fun put(name: String) {
        var number = preferences.getString(name, "0.0")
        if (number == "") number = "0.0"
        values[name] = Variable(number.toDouble(), name)
    }

    val AutoPoints = ArrayList<Vector>()
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
                    number = BigDecimal(value).round(MathContext(3)).toDouble()
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
                if (name.contains("ANGLE")) return Range(rangeNum - 10, rangeNum + 10)
                if (name.contains("DISTANCE")) return Range(rangeNum - 15, rangeNum + 15)
            return Range(0.0, rangeNum * 2)
        }

    fun resetRange(): Range {
        rangeNum = num
        return range
    }
}