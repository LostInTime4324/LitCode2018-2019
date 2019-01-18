package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.view.View
import android.widget.*
import com.qualcomm.ftcrobotcontroller.R
import org.firstinspires.ftc.robotcontroller.teamcode.UtilVars.mathContext
import java.math.BigDecimal
import java.math.MathContext

object UtilVars {
    val mathContext = MathContext(3)
}

fun <T> Spinner.init(items: List<T>, spinnerText: Int, itemSelected: (view: View?, position: Int) -> Unit) {
    adapter = ArrayAdapter<T>(context, spinnerText, items)
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(view: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            itemSelected(view, position)
        }
    }
}

fun <T> createSpinner(context: Context, items: List<T>, initialSelection: T = items[0], itemSelected: (view: View?, position: Int) -> Unit) =
        Spinner(context).apply {
            setSelection(items.indexOf(initialSelection))
            init(items, R.layout.text_wrap, itemSelected)
        }

fun String.toast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_LONG).show()

fun Double.toRoundedString() = BigDecimal(this).round(mathContext).stripTrailingZeros().toPlainString()

fun createGridParams(row: Int, column: Int) = GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column))