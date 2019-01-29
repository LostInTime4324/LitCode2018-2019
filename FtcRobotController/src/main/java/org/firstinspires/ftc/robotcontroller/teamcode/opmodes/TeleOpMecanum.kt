package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcontroller.teamcode.Direction.*
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import kotlin.math.abs

@TeleOp(name = "TeleOpMecanum")
class TeleOpMecanum : OpMode() {
    val leftX: Double get() = gamepad1.left_stick_x.toDouble()
    val leftY: Double get() = gamepad1.left_stick_y.toDouble()
    val rightX: Double get() = gamepad1.right_stick_x.toDouble()
    val rightY: Double get() = gamepad1.right_stick_y.toDouble()

    val minPower = 0.15

    var aPressed = false

    val nav by lazy {
        Navigation(this)
    }

    override fun init() {

    }

    override fun loop() {
        if (!aPressed && gamepad1.a) {
            aPressed = true
            nav.reverseDirection()
        }
        if (!gamepad1.a) {
            aPressed = false
        }
        if (abs(leftY) > minPower || abs(leftX) > minPower) {
            nav.setPower {
                addPower(leftY, FORWARD)
                addPower(leftX, RIGHT)
            }

        } else if (abs(rightX) > minPower) {
            nav.setPower(rightX, CW)
        } else if (gamepad1.right_bumper) {
            nav.setPower(0.5, RIGHT)
        } else if (gamepad1.left_bumper) {
            nav.setPower(0.5, LEFT)
        } else {
            nav.resetDrivePower()
        }

        if (abs(leftY) > abs(leftX) && abs(leftY) > minPower) {
            nav.setPower(leftY, FORWARD)
        } else if (abs(leftX) > minPower) {
            nav.setPower(leftX, RIGHT)
        } else if (abs(rightX) > minPower) {
            nav.setPower(rightX, CW)
        } else {
            nav.resetDrivePower()
        }

        nav.logEncoderValues()


        if (gamepad2.dpad_down || gamepad1.dpad_down) {
            nav.elevatorMotor.power = 1.0
        } else if (gamepad2.dpad_up || gamepad1.dpad_up) {
            nav.elevatorMotor.power = -1.0
        } else {
            nav.elevatorMotor.power = 0.0
        }

        //Rev Extrusion
        if (abs(gamepad2.right_stick_y) > 0.1) {
            nav.extenderMotor.power = gamepad2.right_stick_y.toDouble()
        } else {
            nav.extenderMotor.power = 0.0
        }

        //Tilt Arm Structure
        if (abs(gamepad2.right_stick_y) > 0.1) {
            nav.armMotor.power = gamepad2.left_stick_y.toDouble()
        } else {
            nav.armMotor.power = gamepad2.left_stick_y.toDouble()
        }

        //Run the intakeasdf
        if (gamepad2.left_bumper) {
            nav.intakeMotor.power = 1.0
        } else if (gamepad2.right_bumper) {
            nav.intakeMotor.power = -1.0
        } else {
            nav.intakeMotor.power = 0.0
        }
    }
}