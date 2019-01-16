package org.firstinspires.ftc.robotcontroller.teamcode.activites

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.*
import com.qualcomm.ftcrobotcontroller.R
import kotlinx.android.synthetic.main.activity_variable_control.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables
import org.firstinspires.ftc.robotcontroller.teamcode.opmodes.VariableEnums
import org.firstinspires.ftc.robotcontroller.teamcode.opmodes.VariableEnums.*
import org.firstinspires.ftc.robotcontroller.teamcode.toRoundedString
import org.firstinspires.ftc.robotcontroller.teamcode.toast

class VariableControlActivity : Activity() {

    val preferences by lazy {
        getSharedPreferences(Variables.VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE)
    }

    var selectedVariable: String? = null
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

        Variables.variables.entries.forEachIndexed { index, (name, number) ->
            val field = NumberField(name, number)
            var params = GridLayout.LayoutParams(GridLayout.spec(index), GridLayout.spec(0)).also { it.marginStart = 50 }
            field.nameText.layoutParams = params
            params = GridLayout.LayoutParams(GridLayout.spec(index), GridLayout.spec(1)).also { it.marginStart = 50 }
            field.numberText.layoutParams = params
            variableControlLayout.addView(field.nameText)
            variableControlLayout.addView(field.numberText)
        }


        Variables.enums.toList().forEachIndexed { index, () }
    }

    override fun onPause() {
        super.onPause()
        updatePreferences()
    }

    fun updatePreferences() {
        Variables.variables.forEach {(name, number) ->
            preferences.edit().putString(name, number.toString()).apply()
        }
    }

    val context = this

    inner class NumberField(val name: String, var number: Double) {
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

        val nameText = TextView(context).apply {
            text = name.replace("_", " ")
        }
    }
    inner class EnumField<T: Enum<T>>(name: String, enum: Enum<T>) {
        val spinner = Spinner(context).apply {

        }
    }
}