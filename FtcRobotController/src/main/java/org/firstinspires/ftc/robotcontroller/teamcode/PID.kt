package org.firstinspires.ftc.robotcontroller.teamcode

import com.jjoe64.graphview.series.DataPoint
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcontroller.teamcode.activites.Graph
import org.firstinspires.ftc.robotcontroller.teamcode.activites.GraphActivity
import org.firstinspires.ftc.robotcontroller.teamcode.activites.Series

import kotlin.math.max
import kotlin.math.min

/**
 * Created by walker on 2/22/18.
 */
class PID(val name: String, val Kp: Double, val Kd: Double, val Ki: Double) {
    val numberOfPoints get() = errorPoints.size

    val timer = ElapsedTime()
    val errorPoints = ArrayList<DataPoint>()
    val derivativePoints = ArrayList<DataPoint>()
    val integralPoints = ArrayList<DataPoint>()
    val powerPoints = ArrayList<DataPoint>()
    val zeros = ArrayList<DataPoint>()
    lateinit var aveErrorPoints: Array<DataPoint>
    lateinit var aveDerPoints: Array<DataPoint>
    lateinit var aveIntPoints: Array<DataPoint>

    val timeInterval = 0.05

    fun averagedArray(array: Array<DataPoint>, num: Int): Array<DataPoint> {
        return array.mapIndexed { index, DataPoint ->
            val range = max(0, index - num) until min(array.size, index + num + 1)
            DataPoint(DataPoint.x, array.slice(range).sumByDouble { it.y } / (range.last - range.first))
        }.toTypedArray()
    }

    fun createGraphs() {
        aveErrorPoints = averagedArray(errorPoints.toTypedArray(), 3)

        aveDerPoints = Array(numberOfPoints) {
            val x = aveErrorPoints[it].x
            if (it == 0) return@Array DataPoint(x, 0.0)
            val de = aveErrorPoints[it].y - aveErrorPoints[it - 1].y
            val dt = aveErrorPoints[it].x - aveErrorPoints[it - 1].x
            val y = de / dt
            DataPoint(x, y)
        }

        var intSum = 0.0
        aveIntPoints = Array(numberOfPoints) {
            if (it == 0) return@Array DataPoint(aveErrorPoints[it].x, 0.0)
            val t = aveErrorPoints[it].x
            val pt = aveDerPoints[it - 1].x
            val dt = t - pt
            val e = aveErrorPoints[it].y
            intSum += e * dt
            DataPoint(t, intSum)
        }

        GraphActivity.addGraphs(
                Graph("$name: Error Points",
                        Series(errorPoints, "Error Points"),
                        Series(derivativePoints, "Derivative Points"),
                        Series(integralPoints, "Integral Points"),
                        Series(powerPoints, "Power Points")
                ),
                Graph("$name: Power Points", powerPoints),
                Graph("$name: Averaged Points",
                        Series(aveErrorPoints, "Error Points"),
                        Series(aveDerPoints, "Derivative Points"),
                        Series(aveIntPoints, "Integral Points")
                )
        )
    }

    var prevError = 0.0
    var prevTime = 0.0
    var error = 0.0
    var time = 0.0
    var errorIntegral = 0.0
    var errorDerivative = 0.0
    fun reset() {
        errorPoints.clear()
        derivativePoints.clear()
        integralPoints.clear()
        prevError = 0.0
        prevTime = 0.0
        error = 0.0
        time = 0.0
        errorIntegral = 0.0
        timer.reset()
    }

    fun getPower(error: Double): Double {
        time = timer.time()
        val dt = time - prevTime
        val de = error - prevError
        errorDerivative = de / dt
        errorIntegral += if (prevError != 0.0) (error + prevError) / 2.0 * dt else error * dt
        val power = error * Kp + errorDerivative * Kd + errorIntegral * Ki
        errorPoints.add(DataPoint(time, error))
        derivativePoints.add(DataPoint(time, errorDerivative))
        integralPoints.add(DataPoint(time, errorIntegral))
        powerPoints.add(DataPoint(time, power))
        prevTime = time
        prevError = error
        return power
    }

    fun isMoving() =
            if (numberOfPoints < 10) true
            else powerPoints.subList(numberOfPoints - 10, numberOfPoints).any { it.y >= 0.15 }

    fun wait(seconds: Double) {
        val startTime = timer.time()
        while (timer.time() - startTime < seconds);
    }
}