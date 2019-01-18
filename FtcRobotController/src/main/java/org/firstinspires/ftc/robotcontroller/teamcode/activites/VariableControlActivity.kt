package org.firstinspires.ftc.robotcontroller.teamcode.activites

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import com.qualcomm.ftcrobotcontroller.R
import kotlinx.android.synthetic.main.activity_variable_control.*
import org.firstinspires.ftc.robotcontroller.teamcode.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.enumMap

class VariableControlActivity : Activity() {

    val preferences by lazy {
        getSharedPreferences(Variables.VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE)
    }

    var selectedVariable: NumberVariable? = null
    var savedNumber: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_variable_control)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        resetButton.setOnClickListener {
            Variables[selectedVariable!!] = savedNumber!!
        }

        saveButton.setOnClickListener {
            savedNumber = Variables[selectedVariable!!]
            updatePreferences()
        }

        Variables.variables.entries.forEachIndexed { row, (name, number) ->
            NumberField(name, number, row)
        }



        Variables.enumMap.toList().forEachIndexed { row, (enumClass, enum) ->
            EnumField(enumClass, enum, row)
        }
    }

    override fun onPause() {
        super.onPause()
        updatePreferences()
    }

    fun updatePreferences() {
        Variables.variables.forEach { (name, number) ->
            preferences.edit().putString(name, number.toString()).apply()
        }
    }

    val context = this

    inner class NumberField(val name: String, var number: Double, row: Int) {
        val nameText = TextView(context).apply {
            text = name.replace("_", " ")
        }

        val numberText = EditText(context).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            setText(number.toRoundedString())
            setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    selectedVariable = name
                    savedNumber = number
                }
            }

            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(numberText: Editable?) {
                    try {
                        number = numberText.toString().toDouble()
                    } catch (e: Exception) {
                        "Not a number".toast(context)
                    }
                }
            })
        }

        init {
            variableControlLayout.addView(nameText, createGridParams(row, 0))
            variableControlLayout.addView(numberText, createGridParams(row, 1))
        }
    }

    inner class EnumField(enumClass: Class<*>, enum: Enum<*>, row: Int) {
        val enums = enumClass.enumConstants!!.map { (it as Enum<*>) }

        val nameText = TextView(context).apply {
            text = enumClass.simpleName.replace("_", " ")
        }

        val spinner = createSpinner(context, enums.map { it.name }, enum) { view, position ->
            enumMap[enumClass] = enums[position]
        }

        init {
            variableControlLayout.addView(nameText, createGridParams(row, 3))
            variableControlLayout.addView(spinner, createGridParams(row, 4))
        }
    }
}