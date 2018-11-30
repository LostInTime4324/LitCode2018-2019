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

    

    var powerSign = 1.0

    var aPressed = false

    /* val intakeMotor by lazy {
        hardwareMap[HardwareName.INTAKE_MOTOR] as DcMotor
    }
     */

    val nav  by lazy {
        Navigation(hardwareMap, telemetry)
    }

    private val vars = Variables

    override fun init() {
    }

    override fun loop() {
        if(!aPressed && gamepad1.a) {
            powerSign *= -1.0
            aPressed = true
        } else if(aPressed && !gamepad1.a) {
            aPressed = false
        }
        //Go straight or back
        if (abs(leftY) >= 0.1 || abs(rightY) >= 0.1) {
            nav.setPower(leftY * powerSign, rightY * powerSign)
        } else {
            nav.resetPower()
        }


        //Set elevator power
       if(gamepad2.dpad_down){
            nav.elevatorMotor.power = -1.0
        }
        else if(gamepad2.dpad_up){
            nav.elevatorMotor.power = 1.0
        }
        else{
            nav.elevatorMotor.power = 0.0
        }

        if(gamepad1.y) {
            nav.armMotor.power = 1.0
        } else if(gamepad1.a) {
            nav.armMotor.power = -1.0
        } else {
            nav.armMotor.power = 0.0
        }

        if(gamepad1.x) {
            nav.mineralServo.position = 1.0
        } else if(gamepad1.b) {
            nav.mineralServo.position = 0.0
        } else {
            nav.mineralServo.position = 0.5
        }

    }
}
