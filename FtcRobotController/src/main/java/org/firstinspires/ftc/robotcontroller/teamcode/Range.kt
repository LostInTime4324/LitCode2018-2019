package org.firstinspires.ftc.robotcontroller.teamcode

class Range(var min: Double, var max: Double) {
    fun mapTo(num: Double, start: Double, end: Double): Double {
        return (num - min) / (max - min) * (end - start) + start
    }

    fun mapTo(num: Double, range: Range): Double {
        return mapTo(num, range.min, range.max)
    }
}