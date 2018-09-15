package org.firstinspires.ftc.robotcontroller.teamcode.activites

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.WindowManager
import android.widget.EditText
import android.widget.GridLayout
import android.widget.SeekBar
import android.widget.TextView
import com.qualcomm.ftcrobotcontroller.R
import kotlinx.android.synthetic.main.activity_variable_control.*
import org.firstinspires.ftc.robotcontroller.teamcode.Range
import org.firstinspires.ftc.robotcontroller.teamcode.Variable
import org.firstinspires.ftc.robotcontroller.teamcode.Variables

class VariableControlActivity : Activity() {

    val TAG = "Variables"

    val preferences by lazy {
        getSharedPreferences(Variables.VARIABLE_PREFRENCES_TAG, Context.MODE_PRIVATE)
    }

    var selectedVariable: Variable? = null

    var selectedVariableRange: Range? = null

    var savedVariableValue: Double? = null

    companion object {
        val scrollBarRange = Range(0.0, 100.0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_variable_control)

        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        scrollBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, newProgress: Int, fromUser: Boolean) {
                if (selectedVariable != null) {
                    if (fromUser) {
                        selectedVariable!!.num = scrollBarRange.mapTo(newProgress.toDouble(), selectedVariableRange!!)
                        Log.i(TAG, scrollBarRange.mapTo(newProgress.toDouble(), selectedVariableRange!!).toString())
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }
        })

        scrollBarLeftButton.setOnClickListener {
            if (scrollBar.progress == 0) {
                selectedVariableRange = selectedVariable!!.resetRange()
                scrollBar.progress = 50
            } else {
                selectedVariable!!.num = scrollBarRange.mapTo(selectedVariableRange!!.mapTo(selectedVariable!!.num, scrollBarRange) - 1, selectedVariableRange!!)
            }
        }

        scrollBarRightButton.setOnClickListener {
            if (scrollBar.progress == 100) {
                selectedVariableRange = selectedVariable!!.resetRange()
                scrollBar.progress = 50
            } else {
                selectedVariable!!.num = scrollBarRange.mapTo(selectedVariableRange!!.mapTo(selectedVariable!!.num, scrollBarRange) + 1, selectedVariableRange!!)
            }
        }

        resetButton.setOnClickListener {
            selectedVariable!!.num = savedVariableValue!!
            selectedVariableRange = selectedVariable!!.resetRange()
        }

        saveButton.setOnClickListener {
            selectedVariableRange = selectedVariable!!.resetRange()
            savedVariableValue = selectedVariable!!.num
            scrollBar.progress = 50
            updatePreferences()
        }

        Variables.values.asIterable().forEachIndexed { index, variableEntry ->
            val field = NumberField(variableEntry.key, variableEntry.value)
            var params = GridLayout.LayoutParams(GridLayout.spec(index), GridLayout.spec(0)).also { it.marginStart = 50 }
            field.textView.layoutParams = params
            params = GridLayout.LayoutParams(GridLayout.spec(index), GridLayout.spec(1)).also { it.marginStart = 50 }
            field.editText.layoutParams = params
            variableEntry.value.editText = field.editText
            variableEntry.value.scrollBar = scrollBar
            variableControlLayout.addView(field.textView)
            variableControlLayout.addView(field.editText)
        }
    }

    override fun onPause() {
        super.onPause()
        updatePreferences()
    }

    fun updatePreferences() {
        Variables.values.forEach {
            preferences.edit().putString(it.key, it.value.num.toString()).apply()
        }
    }

    fun getContext(): Context {
        return this
    }

    inner class NumberField(val name: String, val variable: Variable) {
        val editText = EditText(getContext()).also {
            it.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            it.setText(variable.num.toString())
            it.setOnFocusChangeListener { view, hasFocus ->
                if (hasFocus) {
                    scrollBar.progress = 50
                    selectedVariable = variable
                    selectedVariableRange = variable.range
                    savedVariableValue = variable.num
                }
            }
            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
                override fun afterTextChanged(number: Editable?) {
                    try {
                        val num = number.toString().toDouble()
                        if (variable.num != num) {
                            variable.num = num
                        }
                    } catch (e: Exception) {

                    }
                }
            })
        }

        val textView = TextView(getContext()).also {
            it.text = name.replace("_", " ")
        }
    }
}