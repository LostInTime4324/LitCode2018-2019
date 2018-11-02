package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.widget.SeekBar
import org.firstinspires.ftc.robotcontroller.teamcode.activites.VariableControlActivity.Companion.scrollBarRange
import java.util.*
import kotlin.collections.ArrayList


object Variables {

    const val VARIABLE_PREFRENCES_TAG = "Variables"

    val values = HashMap<String, Variable>()

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

class Variable(num: Double, val name: String) {
    var num = num
        set(value) {
            field = value
            scrollBar.progress = range.mapTo(value, scrollBarRange).toInt()
        }

    lateinit var scrollBar: SeekBar

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