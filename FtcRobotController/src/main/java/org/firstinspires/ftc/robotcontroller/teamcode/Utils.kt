package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.qualcomm.ftcrobotcontroller.R
import org.firstinspires.ftc.robotcontroller.teamcode.UtilVars.mathContext
import java.math.BigDecimal
import java.math.MathContext

object UtilVars {
    val mathContext = MathContext(3)
}

fun <T> Spinner.init(items: Array<T>, spinnerText: Int, itemSelected: (view: View?, position: Int) -> Unit) {
    adapter = ArrayAdapter<T>(context, spinnerText, items)
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        var isEditable = true
        override fun onNothingSelected(view: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            if (isEditable) {
                isEditable = false
                itemSelected(view, position)
                isEditable = true
            }
        }
    }
}

fun <T> createSpinner(context: Context, vararg items: T, itemSelected: (view: View?, position: Int) -> Unit) =
        Spinner(context).apply {
            init(items, R.layout.spinner_text_wrap, itemSelected)
        }


fun String.toast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_LONG).show()

fun Double.toRoundedString() = BigDecimal(this).round(mathContext).stripTrailingZeros().toPlainString()