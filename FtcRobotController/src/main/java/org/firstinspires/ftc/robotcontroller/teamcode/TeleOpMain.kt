package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import java.lang.Math.abs

@TeleOp(name = "TeleOp")
class TeleOpMain : OpMode() {

    val leftX: Double get() = gamepad1.left_stick_x.toDouble()
    val leftY: Double get() = gamepad1.left_stick_y.toDouble()
    val rightX: Double get() = gamepad1.right_stick_x.toDouble()
    val rightY: Double get() = gamepad1.right_stick_y.toDouble()

    var x = true
    var y = false

    private val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }

    private val vars = Variables

    override fun init() {

    }

    override fun loop() {
        omniStickDrive()
    }

    fun omniStickDrive() {
        if (abs(leftX) > abs(leftY)) {
            nav.drive(Navigation.Direction.Forward, leftX)
        } else {
            nav.drive(Navigation.Direction.Right, leftY)
        }
        nav.turn(rightX)
    }
}
