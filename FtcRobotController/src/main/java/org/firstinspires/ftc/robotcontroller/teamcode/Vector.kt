package org.firstinspires.ftc.robotcontroller.teamcode

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Created by walker on 3/14/18.
 */
class Vector(var x: Double, var y: Double) {
    constructor(x: Float, y: Float) : this(x.toDouble(), y.toDouble())

    val mag = sqrt(x * x + y * y)
    val norm = this / this.mag
    val isUnit = this == norm

    operator fun plus(v: Vector) = Vector(x + v.x, y + v.y)
    operator fun minus(v: Vector) = Vector(x - v.x, y - v.y)
    operator fun times(s: Double) = Vector(s * x, s * y)
    operator fun div(s: Double) = Vector(x / s, y / s)

    operator fun times(v: Vector) = x * v.x + y * v.y

    fun angleBetween(v: Vector) = acos(this * v / this.mag / v.mag)
    fun normalize() {
        x = norm.x
        y = norm.y
    }
}