package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcontroller.teamcode.activites.Graph
import org.firstinspires.ftc.robotcontroller.teamcode.activites.GraphActivity
import org.firstinspires.ftc.robotcontroller.teamcode.activites.GraphSeries
import org.firstinspires.ftc.robotcontroller.teamcode.activites.GraphType
import kotlin.math.max
import kotlin.math.min

/**
 * Created by walker on 2/22/18.
 */
class PID(val name: String, val Kp: Double, val Kd: Double, val Ki: Double) {
    val numberOfPoints get() = errorPoints.size

    val timer = ElapsedTime()
    val errorPoints = ArrayList<Vector>()
    val derivativePoints = ArrayList<Vector>()
    val integralPoints = ArrayList<Vector>()
    val powerPoints = ArrayList<Vector>()
    val zeros = ArrayList<Vector>()
    lateinit var aveErrorPoints: Array<Vector>
    lateinit var aveDerPoints: Array<Vector>
    lateinit var aveIntPoints: Array<Vector>

    val timeInterval = 0.05

    fun averagedArray(array: Array<Vector>, num: Int): Array<Vector> {
        return array.mapIndexed { index, vector ->
            val range = max(0, index - num) until min(array.size, index + num + 1)
            Vector(vector.x, array.slice(range).sumByDouble { it.y } / (range.last - range.first))
        }.toTypedArray()
    }

    fun createGraphs() {
        aveErrorPoints = averagedArray(errorPoints.toTypedArray(), 3)

        aveDerPoints = Array(numberOfPoints) {
            val x = aveErrorPoints[it].x
            if (it == 0) return@Array Vector(x, 0.0)
            val de = aveErrorPoints[it].y - aveErrorPoints[it - 1].y
            val dt = aveErrorPoints[it].x - aveErrorPoints[it - 1].x
            val y = de / dt
            Vector(x, y)
        }

        var intSum = 0.0
        aveIntPoints = Array(numberOfPoints) {
            if (it == 0) return@Array Vector(aveErrorPoints[it].x, 0.0)
            val t = aveErrorPoints[it].x
            val pt = aveDerPoints[it - 1].x
            val dt = t - pt
            val e = aveErrorPoints[it].y
            intSum += e * dt
            Vector(t, intSum)
        }

        GraphActivity.graphs +=
                Graph("$name: Points",
                        GraphSeries(errorPoints, "Error Points", GraphType.LineGraph),
                        GraphSeries(derivativePoints, "Derivative Points", GraphType.LineGraph),
                        GraphSeries(integralPoints, "Integral Points", GraphType.LineGraph),
                                GraphSeries(powerPoints, "Power Points", GraphType.LineGraph)
                )
        GraphActivity.graphs +=
                Graph("$name: Averaged Points}",
                        GraphSeries(aveErrorPoints, "Averaged Error Points", GraphType.LineGraph),
                        GraphSeries(aveDerPoints, "Derivative Points of Averaged Error Points", GraphType.LineGraph),
                        GraphSeries(aveIntPoints, "Integral Points of Averaged Error Points", GraphType.LineGraph),
                        GraphSeries(zeros, "Zeros", GraphType.PointGraph)
                )
    }

    var prevError = 0.0
    var prevTime = 0.0
    var error = 0.0
    var time = 0.0
    var errorIntegral = 0.0
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
        val errorDerivative = de / dt
        errorIntegral += if (prevError != 0.0) (error + prevError) / 2.0 * dt else error * dt
        val power = error * Kp + errorDerivative * Kd + errorIntegral * Ki
        errorPoints.add(Vector(time, error))
        derivativePoints.add(Vector(time, errorDerivative))
        integralPoints.add(Vector(time, errorIntegral))
        powerPoints.add(Vector(time, power))

        prevTime = time
        prevError = error
        return power
    }

    fun isMoving() = powerPoints.subList(numberOfPoints - 10, numberOfPoints).any {it.y >= 0.15}

    fun wait(seconds: Double) {
        val startTime = timer.time()
        while (timer.time() - startTime < seconds);
    }
}