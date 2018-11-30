package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.Servo
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.TOTEM_SERVO
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars


@Autonomous(name = "AutoTest")
class AutoTest : LinearOpMode() {
    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }

    override fun runOpMode() {
        val servo = hardwareMap[TOTEM_SERVO] as Servo
        servo.position = 0.0
        nav.wait(0.5)
        servo.position = 1.0
    }
}