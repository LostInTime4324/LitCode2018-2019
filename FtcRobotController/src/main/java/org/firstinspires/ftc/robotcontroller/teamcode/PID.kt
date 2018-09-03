package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.util.ElapsedTime
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

/**
 * Created by walker on 2/22/18.
 */
class PID(var Kp: Double, var Kd: Double, var Ki: Double, val controller: (power: Double, setPoint: Double) -> Double) {

    fun calibrate(testSetPoint: Double) {
        var setPoint = testSetPoint
        val timeout = 3.0
        while (true) {
            val period = gotoSetPoint(setPoint, timeout)
            setPoint *= -1
            if (period == 0.0) {
                Kp *= 2
            } else {
                val Tu = period
                Kp = 0.6 * Kp
                Ki = 1.2 * Kp / Tu
                Kd = 3.0 / 40.0 * Kp * Tu
                break
            }
        }
    }

    val numberOfPoints get() = errorPoints.size

    val timer = ElapsedTime()
    val errorPoints = ArrayList<Vector>()
    val derivativePoints = ArrayList<Vector>()
    val integralPoints = ArrayList<Vector>()
    val zeros = ArrayList<Vector>()
    lateinit var aveErrorPoints: Array<Vector>
    lateinit var aveDerPoints: Array<Vector>
    lateinit var absErrorPoints: Array<Vector>

    val timeInterval = 0.05
    var prevError = 0.0
    var prevTime = 0.0
    var error = 0.0
    var time = 0.0
    val dt get() = time - prevTime
    val de get() = error - prevError
    val errorDerivative get() = de / dt
    var errorIntegral = 0.0

    private fun integrate() {
        errorIntegral += if (prevError != 0.0) (error + prevError) / 2.0 * dt else error * dt
    }

    fun addPoints() {
        val t = time
        errorPoints.add(Vector(t, error))
        derivativePoints.add(Vector(t, errorDerivative))
        integralPoints.add(Vector(t, errorIntegral))
    }

    fun averagedArray(array: Array<Vector>, num: Int): Array<Vector> {
        return array.mapIndexed { index, vector ->
            val range = max(0, index - num) until min(array.size, index + num + 1)
            Vector(vector.x, array.slice(range).sumByDouble { it.y } / (range.last - range.first))
        }.toTypedArray()
    }

    fun createGraphs() {
        absErrorPoints = Array(numberOfPoints) {
            Vector(aveErrorPoints[it].x, abs(aveErrorPoints[it].y))
        }

        aveErrorPoints = averagedArray(errorPoints.toTypedArray(), 1)

        aveDerPoints = Array(numberOfPoints) {
            if (it == 0) Vector(aveErrorPoints[it].x, (aveErrorPoints[it].y - aveErrorPoints[it + 1].y) / (aveErrorPoints[it].x - aveErrorPoints[it + 1].x))
            else if (it == numberOfPoints - 1) Vector(aveErrorPoints[it].x, (aveErrorPoints[it - 1].y - aveErrorPoints[it].y) / (aveErrorPoints[it - 1].x - aveErrorPoints[it].x))
            else Vector(aveErrorPoints[it].x, (aveErrorPoints[it - 1].y - aveErrorPoints[it + 1].y) / (aveErrorPoints[it - 1].x - aveErrorPoints[it + 1].x))
        }

        var max_de = 0.0

        for (i in 1 until numberOfPoints) {
            max_de = max(max_de, abs(aveErrorPoints[i].y - aveErrorPoints[i - 1].y))
        }

        var derSign = 0.0

        absErrorPoints.forEachIndexed { index, vector ->
            if (index != absErrorPoints.size - 1) {
                if (vector.y <= max_de && (derSign == 0.0 || derSign == aveDerPoints[index].y.sign)) {
                    zeros.add(vector)
                    if (derSign == 0.0) {
                        derSign = -aveDerPoints[index].y.sign
                    } else {
                        derSign *= -1
                    }
                }
            }
        }
    }

    fun addGraphs() {
        createGraphs()
    }

    fun reset() {
        timer.reset()
        time = 0.0
        errorPoints.clear()
        derivativePoints.clear()
        integralPoints.clear()
        errorIntegral = 0.0
        prevTime = 0.0
        prevError = 0.0
    }

    fun gotoSetPoint(setPoint: Double, timeout: Double): Double {
        reset()
        do {
            wait(timeInterval)
            time = timer.time()
            integrate()
            addPoints()
            val power = error * Kp + errorDerivative * Kd + errorIntegral * Ki
            error = controller(power, setPoint)
            prevTime = time
            prevError = error
        } while (abs(power) > 0.15 && time < timeout)

        //If it didn't timeout then the power was too low so the period should be 0
        if(time < timeout) {
            return 0.0
        }

        createGraphs()

        val zeroDistances = Array(zeros.size - 1) {
            zeros[it + 1].x - zeros[it].x
        }

        val halfPeriod = zeroDistances.average()

        //If the distances between the zeros isn't approximately the same then the period should be zero
        zeroDistances.forEach {
            if (halfPeriod * 1.25 < it || halfPeriod * 0.75 > it) return 0.0
        }

        return halfPeriod * 2
    }

    fun wait(ys: Double) {
        val startTime = time
        while (time - startTime < ys);
    }
}