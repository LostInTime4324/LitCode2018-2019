package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.util.ElapsedTime

/**
 * Created by walker on 2/22/18.
 */
class PID(var Kp: Double, var Kd: Double, var Ki: Double, val controller: (power: Double, setPoint: Double) -> Double) {
    constructor(controller: (power: Double, setPoint: Double) -> Double, testSetPoint: Double) : this(0.0001, 0.0, 0.0, controller) {
        var setPoint = testSetPoint
        val timeout = 3.0
        while (true) {
            gotoSetPoint(setPoint, timeout)
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

    val size get() = errorPoints.size

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
    val time get() = timer.time()
    val dt get() = time - prevTime
    val de get() = error - prevError
    val errorDerivative get() = de / dt
    var errorIntegral = 0.0

    private fun integrate() {
        if (prevError != 0.0)
            errorIntegral += (error + prevError) / 2.0 * dt
        else
            errorIntegral += error * dt
    }

    fun addPoints() {
        val constantTime = time
        errorPoints.add(Vector(constantTime, error))
        derivativePoints.add(Vector(constantTime, errorDerivative))
        integralPoints.add(Vector(constantTime, errorDerivative))
    }

    fun averagedArray(array: Array<Vector>, num: Int): Array<Vector> {
        return Array<Vector>(array.size) {
            val range = max(0, it - num) until min(array.size, it + num + 1)
            Vector(array[it].x, array.slice(range).sumByDouble { it.y } / array.size)
        }
    }

    fun createGraphs() {
        absErrorPoints = Array<Vector>(size) {
            Vector(aveErrorPoints[it].x, abs(aveErrorPoints[it].y))
        }

        aveErrorPoints = averagedArray(errorPoints.toTypedArray(), 1)

        aveDerPoints = Array<Vector>(size) {
            if (it == 0) Vector(aveErrorPoints[it].x, (aveErrorPoints[it].y - aveErrorPoints[it + 1].y) / (aveErrorPoints[it].x - aveErrorPoints[it + 1].x))
            else if (it == size - 1) Vector(aveErrorPoints[it].x, (aveErrorPoints[it - 1].y - aveErrorPoints[it].y) / (aveErrorPoints[it - 1].x - aveErrorPoints[it].x))
            else Vector(aveErrorPoints[it].x, (aveErrorPoints[it - 1].y - aveErrorPoints[it + 1].y) / (aveErrorPoints[it - 1].x - aveErrorPoints[it + 1].x))
        }

        var derivativeSign = 0.0
        var maxDe = 0.0

        for (i in 1 until size) {
            maxDe = max(maxDe, abs(aveErrorPoints[i].y - aveErrorPoints[i - 1].y))
        }

        absErrorPoints.forEachIndexed { index, Vector ->
            if (index != absErrorPoints.size - 1) {
                if (Vector.y <= maxDe && (derivativeSign == 0.0 || derivativeSign == aveDerPoints[index].y.sign)) {
                    zeros.add(Vector)
                    if (derivativeSign == 0.0) {
                        derivativeSign = -aveDerPoints[index].y.sign
                    } else {
                        derivativeSign *= -1
                    }
                }
            }
        }
    }

    fun addGraphs() {
        createGraphs()
        GraphActivity.graphs += GraphActivity.Graph(errorPoints.toTypedArray(), "Error Points")
        GraphActivity.graphs += GraphActivity.Graph(derivativePoints.toTypedArray(), "Derivative Points")
        GraphActivity.graphs += GraphActivity.Graph(integralPoints.toTypedArray(), "Integral Points")
        GraphActivity.graphs += GraphActivity.Graph(aveErrorPoints, "Averaged Error Points")
        GraphActivity.graphs += GraphActivity.Graph(aveDerPoints, "Averaged Derivative Points")
        GraphActivity.graphs += GraphActivity.Graph(zeros.toTypedArray(), "Zeros")
    }

    val period: Double
        get() {
            createGraphs()

            val zeroDistances = Array<Double>(zeros.size - 1) {
                zeros[it + 1].x - zeros[it].x
            }

            var halfPeriod = zeroDistances.average()

            zeroDistances.forEach {
                if (halfPeriod * 1.25 < it || halfPeriod * 0.75 > it) halfPeriod = 0.0
            }

            return halfPeriod * 2
        }

    fun reset() {
        timer.reset()
        errorPoints.clear()
        derivativePoints.clear()
        integralPoints.clear()
        errorIntegral = 0.0
        prevTime = 0.0
        prevError = 0.0
    }

    fun gotoSetPoint(setPoint: Double, timeout: Double): Boolean {
        reset()
        do {
            wait(timeInterval)
            integrate()
            addPoints()
            val power = error * Kp + errorDerivative * Kd + errorIntegral * Ki
            error = controller(power, setPoint)
            prevTime = time
            prevError = error
        } while (abs(power) > 0.15 && time < timeout)
        addGraphs()
        return !(time < timeout)
    }

    fun wait(ys: Double) {
        val startTime = time
        while (time - startTime < ys);
    }
}