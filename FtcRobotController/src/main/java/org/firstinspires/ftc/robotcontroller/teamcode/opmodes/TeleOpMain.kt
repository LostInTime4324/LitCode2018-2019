package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import android.hardware.Camera
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.Variables
import java.lang.Math.abs

@TeleOp(name = "TeleOpMain")
class TeleOpMain : OpMode() {

    val leftX: Double get() = gamepad1.left_stick_x.toDouble()
    val leftY: Double get() = gamepad1.left_stick_y.toDouble()
    val rightX: Double get() = gamepad1.right_stick_x.toDouble()
    val rightY: Double get() = gamepad1.right_stick_y.toDouble()

    val elevatorMotor by lazy {
       hardwareMap[HardwareNames.ELEVATOR_MOTOR] as DcMotor
    }
    val scoopMotor by lazy {
        hardwareMap[HardwareNames.SCOOP_MOTOR] as DcMotor
    }

      val totemServo by lazy {
         hardwareMap[HardwareNames.TOTEM_SERVO] as Servo
    }



    /* val intakeMotor by lazy {
        hardwareMap[HardwareNames.INTAKE_MOTOR] as DcMotor
    }
     */

    val nav  by lazy {
        Navigation(hardwareMap, telemetry)
    }
    private operator fun Double.invoke(position: Double) {

    }

    private val vars = Variables

    override fun init() {

        totemServo.position(0.0)
    }

    override fun loop() {
        //Go straight or back
        if (abs(leftY) >= 0.1 || abs(rightY) >= 0.1) {
            nav.setPower(leftY, rightY)
        } else {
            nav.resetPower()
        }


        //Set elevator power
       if(gamepad2.dpad_down){

            elevatorMotor.power = -1.0
        }
        else if(gamepad2.dpad_up){
            elevatorMotor.power = 1.0
        }
        else{
            elevatorMotor.power = 0.0
        }





//        if (gamepad1.a) {
//            totemServo.position(1.0)
//        }
//        else {
//            totemServo.position(0.0)
//        }


        // Run scoop (x: forward and y: reverse)
        if (gamepad2.x) {
            scoopMotor.power = 0.3
        }
        else if (gamepad2.y) {
            scoopMotor.power = -0.3
        }
        else {
            scoopMotor.power = 0.0
        }

    }
}


