package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.Variables
import java.lang.Math.abs

@TeleOp(name = "TeleOpMain")
class TeleOpMain : OpMode() {

    val leftX: Double get() = gamepad1.left_stick_x.toDouble()
    val leftY: Double get() = gamepad1.left_stick_y.toDouble()
    val rightX: Double get() = gamepad1.right_stick_x.toDouble()
    val rightY: Double get() = gamepad1.right_stick_y.toDouble()

    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }

    private val vars = Variables

    override fun init() {

    }

    override fun loop() {
        singleJoystickDrive()
    }

    fun singleJoystickDrive() {
        if (abs(leftY) >= 0.1) {
            nav.drive(Navigation.Orientation.Vertical, leftY)
        }
        if (abs(leftX) >= 0.1) {
            nav.turn(rightX)
        }
        if (abs(leftX) <= 0.1 && abs(leftY) <= 0.1) {
            nav.resetPower()
        }
    }

        //Turn right motor only
        if (abs(rightY) >= 0.1 && abs(leftY) <= 0.1) {
            nav.setPower(-rightY, -rightY, rightY, rightY)
        } else {
            nav.resetPower()
        }

        //Go straight or back
        if (abs(leftY) >= 0.1 && abs(rightY) >= 0.1) {
            nav.setPower(leftY, leftY, rightY, rightY)
        } else {
            nav.resetPower()
        }
    }
}
