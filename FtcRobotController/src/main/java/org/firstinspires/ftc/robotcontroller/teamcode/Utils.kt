package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.view.View
import android.widget.*
import com.qualcomm.ftcrobotcontroller.R
import java.math.BigDecimal
import java.math.MathContext

fun <T> createSpinner(context: Context, items: List<T>, initialSelectionIndex: Int = 0, itemSelected: (view: View?, position: Int) -> Unit) =
        Spinner(context).apply {
            adapter = ArrayAdapter<T>(context, R.layout.text_wrap, items)
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(view: AdapterView<*>?) {}
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    itemSelected(view, position)
                }
            }
            setSelection(initialSelectionIndex)
        }

fun String.toast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_LONG).show()

val mathContext = MathContext(3)

fun Double.toRoundedString() = BigDecimal(this).round(mathContext).stripTrailingZeros().toPlainString()

fun createGridParams(row: Int, column: Int) = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column))

val enumNameRegex = Regex("([A-Z])([A-Z]+)")

fun String.formattedEnumName() = replace("_", " ").replace(enumNameRegex) {
    val (firstChar, followingChars) = it.destructured
    "$firstChar${followingChars.toLowerCase()}"
}