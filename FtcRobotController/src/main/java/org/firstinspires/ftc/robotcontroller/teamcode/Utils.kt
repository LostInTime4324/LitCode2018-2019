package org.firstinspires.ftc.robotcontroller.teamcode

import android.content.Context
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.qualcomm.ftcrobotcontroller.R
import org.firstinspires.ftc.robotcontroller.teamcode.UtilVars.mathContext
import org.firstinspires.ftc.robotcore.external.Telemetry
import java.math.BigDecimal
import java.math.MathContext

object UtilVars {
    val mathContext = MathContext(3)
}

fun <T> Spinner.init(items: Array<T>, spinnerText: Int, itemSelected: (view: View?, position: Int) -> Unit) {
    adapter = ArrayAdapter<T>(context, spinnerText, items)
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(view: AdapterView<*>?) {}
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            itemSelected(view, position)
        }
    }
}

fun <T> createSpinner(context: Context, items: Array<T>, itemSelected: (view: View?, position: Int) -> Unit): Spinner {
    val spinner = Spinner(context)
    spinner.init(items, R.layout.spinner_text_wrap, itemSelected)
    return spinner
}

fun String.toast(context: Context) = Toast.makeText(context, this, Toast.LENGTH_LONG).show()

fun Double.toRoundedString() = BigDecimal(this).round(mathContext).stripTrailingZeros().toPlainString()

operator fun Telemetry?.plusAssign(data: String) {
    this?.addData(data, "")
}