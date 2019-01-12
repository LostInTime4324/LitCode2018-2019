package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation.Orientation.*
import kotlin.math.abs

@TeleOp(name = "TeleOpMecanum")
class TeleOpMecanum : OpMode() {
    val leftX: Double get() = gamepad1.left_stick_x.toDouble()
    val leftY: Double get() = gamepad1.left_stick_y.toDouble()
    val rightX: Double get() = gamepad1.right_stick_x.toDouble()
    val rightY: Double get() = gamepad1.right_stick_y.toDouble()

    val minPower = 0.15

    val nav by lazy {
        Navigation(this)
    }

    override fun init() {

    }

    override fun loop() {
//        if(gamepad1.a) {
//            nav.frontLeftMotor.power = 1.0
//        } else if(gamepad1.b) {
//            nav.frontRightMotor.power = 1.0
//        } else if(gamepad1.x) {
//            nav.backLeftMotor.power = 1.0
//        } else if(gamepad1.y) {
//            nav.backRightMotor.power = 1.0
//        }


        if (abs(leftY) > abs(leftX) && abs(leftY) > minPower) {
            nav.setPower(Vertical, leftY)
        } else if (abs(leftX) > minPower) {
            nav.setPower(Horizontal, leftX)
        } else if (abs(rightX) > minPower) {
            nav.setPower(Rotational, rightX)
        } else {
            nav.setPower(0.0)
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
        if (gamepad2.left_bumper || gamepad1.left_bumper) {
            nav.intakeMotor.power = 1.0
        } else {
            nav.intakeMotor.power = 0.0
        }


    }
}