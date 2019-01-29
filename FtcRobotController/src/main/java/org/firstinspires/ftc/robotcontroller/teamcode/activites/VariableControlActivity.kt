package org.firstinspires.ftc.robotcontroller.teamcode.activites

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.*
import android.widget.EditText
import android.widget.TextView
import com.qualcomm.ftcrobotcontroller.R
import kotlinx.android.synthetic.main.activity_variable_control.*
import org.firstinspires.ftc.robotcontroller.teamcode.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.BOOLEAN_PREFERENCES
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.ENUM_PREFERENCES
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.NUMBER_PREFERENCES
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.enumMap

class VariableControlActivity : Activity() {
    val numberPreferences by lazy {getSharedPreferences(NUMBER_PREFERENCES, Context.MODE_PRIVATE) }
    val enumPreferences by lazy {getSharedPreferences(ENUM_PREFERENCES, Context.MODE_PRIVATE)}
    val booleanPreferences by lazy {getSharedPreferences(BOOLEAN_PREFERENCES, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_variable_control)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        NumberVariable.values().forEachIndexed { row, variable ->
            addNumberField(variable, row)
        }

        val enumStartRow = NumberVariable.values().size

        enumMap.toList().forEachIndexed { row, (enumClass, enum) ->
            addEnumField(enumClass, enum, row + enumStartRow)
        }

        val booleanStartRow = enumStartRow + enumMap.size

        BooleanVariable.values().forEachIndexed { row, booleanVariable ->
            addBooleanField(booleanVariable, row + booleanStartRow)
        }
    }

    override fun onPause() {
        super.onPause()
        updatePreferences()
    }

    fun updatePreferences() {
        NumberVariable.values().forEach { variable ->
            numberPreferences.edit().putFloat(variable.name, variable.number.toFloat()).apply()
        }
        BooleanVariable.values().forEach { variable ->
            booleanPreferences.edit().putBoolean(variable.name, variable.boolean).apply()
        }
        enumMap.forEach { (enumClass, enum) ->
            enumPreferences.edit().putString(enumClass.simpleName, enum.name).apply()
        }
    }

    val context = this

    fun addNumberField(variable: NumberVariable, row: Int) {
        val nameText = TextView(context).apply {
            text = variable.name.formattedEnumName()
        }

        val numberText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(variable.number.toRoundedString())
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(numberText: Editable?) {
                    try {
                        variable.number = numberText.toString().toDouble()
                    } catch (e: Exception) {
                        "Not a number".toast(context)
                    }
                }
            })
        }
        addViews(nameText, numberText, row)
    }

    fun addEnumField(enumClass: Class<*>, enum: Enum<*>, row: Int) {
        val enums = enumClass.enumConstants!!.map { (it as Enum<*>) }

        val nameText = TextView(context).apply {
            text = enumClass.simpleName.formattedEnumName()
        }

        val spinner = createSpinner(context, enums.map { it.name.formattedEnumName() }, enums.indexOf(enum)) { view, position ->
            enumMap[enumClass] = enums[position]
        }

        addViews(nameText, spinner, row)
    }

    fun addBooleanField(variable: BooleanVariable, row: Int) {
        val booleans = listOf(variable.boolean, !variable.boolean)

        val nameText = TextView(context).apply {
            text = variable.name.formattedEnumName()
        }

        val spinner = createSpinner(context, booleans.map { it.toString().capitalize() }) { view, position ->
            variable.boolean = booleans[position]
        }
            addViews(nameText, spinner, row)
    }

    fun addViews(first: View, second: View, row: Int) {
        variableControlLayout.addView(first, createGridParams(row, 0))
        variableControlLayout.addView(second, createGridParams(row, 1))
    }
}