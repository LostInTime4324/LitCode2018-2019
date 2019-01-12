package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars
import java.lang.Math.abs

@TeleOp(name = "TeleOpTank")
class TeleOpMain : OpMode() {

    val leftX: Double get() = gamepad1.left_stick_x.toDouble()
    val leftY: Double get() = gamepad1.left_stick_y.toDouble()
    val rightX: Double get() = gamepad1.right_stick_x.toDouble()
    val rightY: Double get() = gamepad1.right_stick_y.toDouble()


    var switched = false

    var aPressed = false

    val nav by lazy {
        Navigation(this)
    }

    override fun init() {
    }

    override fun loop() {
        if (!aPressed && gamepad1.a) {
            switched = !switched
            aPressed = true
        } else if (aPressed && !gamepad1.a) {
            aPressed = false
        }

        //Go straight or back
        if (abs(leftY) >= 0.1 || abs(rightY) >= 0.1) {
            if (switched) {
                nav.setPower(-leftY, -rightY)
            } else {
                nav.setPower(rightY, leftY)
            }
        } else {
            nav.unsetDrivePower()
        }

//        nav.logEncoderValues()
//
//        if (abs(gamepad2.left_stick_y) > 0.1) {
//            nav.intakeMotor.power = gamepad2.left_stick_y.toDouble()
//        } else{
//            nav.intakeMotor.power = 0.0
//        }

//        //Set elevator power

        if(gamepad2.dpad_down || gamepad1.dpad_down) {
            nav.elevatorMotor.power = 1.0
        } else if(gamepad2.dpad_up || gamepad1.dpad_up) {
            nav.elevatorMotor.power = -1.0
        } else {
            nav.elevatorMotor.power = 0.0
        }

        if(gamepad2.dpad_right) {
            nav.totemServo.power = 1.0
        } else if(gamepad2.dpad_left) {
            nav.totemServo.power = -1.0
        } else {
            nav.totemServo.power = 0.0
        }

//        if (abs(gamepad2.right_stick_y) > 0.1) {
//            nav.armMotor.power = gamepad2.right_stick_y.toDouble()
//        } else{
//            nav.armMotor.power = 0.0
//        }
    }


//        if (gamepad2.b) {
//            nav.moveServo(RIGHT)
//        } else if (gamepad2.x) {
//            nav.moveServo(LEFT)
//        } else if (gamepad2.y) {
//            nav.moveServo(EMPTY)
//        } else {
//            nav.moveServo(CENTER)
//        }
//    }
}
