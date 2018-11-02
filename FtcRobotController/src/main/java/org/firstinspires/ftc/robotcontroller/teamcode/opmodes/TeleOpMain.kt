package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import android.hardware.Camera
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DcMotor
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

     /* val totemServo by lazy {
         hardwareMap[HardwareNames.TOTEM_SERVO] as Servo
    }
    */

    /* val intakeMotor by lazy {
        hardwareMap[HardwareNames.INTAKE_MOTOR] as DcMotor
    }
     */

    val nav  by lazy {
        Navigation(hardwareMap, telemetry)
    }

    private val vars = Variables

    override fun init() {
        // totemServo.setPosition(armServo.MIN_POSITION)
    }

    override fun loop() {
        //Go straight or back
        if (abs(leftY) >= 0.1 || abs(rightY) >= 0.1) {
            nav.setPower(leftY, rightY)
        } else {
            nav.resetPower()
        }


        //Set elevator power
       if(gamepad1.dpad_down){

            elevatorMotor.power = -1.0
        }
        else if(gamepad1.dpad_up){
            elevatorMotor.power = 1.0
        }
        else{
            elevatorMotor.power = 0.0
        }

        /* Open/close TOTEM servo (for knocking off totem)

        if (gamepad1.a) {
            if (totemServo.getPosition() == totemServo.MIN_POSITION) {
                totemServo.setPosition(totemServo.MAX_POSITION)
            }
            else {
                totemServo.setPosition(totemServo.MIN_POSITION)
            }
        }
        */

        /* Run intake (x: forward and y: reverse)
        if (gamepad1.x) {
            intakeMotor.power = 1.0
        }
        else if (gamepad1.y) {
            intakeMotor.power = -1.0
        }
        else {
            intakeMotor.power = 0.0
        }
        */
    }
}