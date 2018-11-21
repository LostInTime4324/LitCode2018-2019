package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars


@Autonomous(name = "AutoMain")
class AutoMain : LinearOpMode() {
    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }

    val autoType get() = vars[Auto_Type]

    val detector = FtcRobotControllerActivity.detector

    override fun runOpMode() {
        detector.telemetry = telemetry
        detector.enable()
        telemetry.addData("Status", "Init")
        waitForStart()
        telemetry.addData("Status", "Starting")
        telemetry.addData("Auto Type: ", autoType)
        telemetry.update()

//        nav.moveElevator(1.0, vars[Elevator_Up_Time])

        detector.started = true

//        nav.driveByTime(vars[Auto_Power], 0.3)
//
//        nav.moveElevator(-1.0, vars[Elevator_Down_Time])
        nav.wait(0.3 + vars[Elevator_Down_Time])

        val order = detector.currentOrder

        telemetry.addData("Order", order)
        telemetry.addData("Left Certainty", detector.leftCertainty)
        telemetry.addData("Center Certainty", detector.centerCertainty)
        telemetry.addData("Right Certainty", detector.rightCertainty)
        telemetry.update()

        when (order) {
            SamplingOrderDetector.GoldLocation.LEFT -> {
                nav.turnByGyro(-vars[Turn_Angle])
            }
            SamplingOrderDetector.GoldLocation.RIGHT -> {
                nav.turnByGyro(vars[Turn_Angle])
            }
        }

        nav.driveByTime(vars[Auto_Power], vars[Drive_Time])

        //nav.moveScoop(-1.0, vars[Scoop_Lowering_Time])

        when (order) {
            SamplingOrderDetector.GoldLocation.LEFT -> {
                nav.turnByGyro(vars[Turn_Angle] * 2)
                nav.driveByTime(vars[Auto_Power], vars[Drive_Time_2])
            }
            SamplingOrderDetector.GoldLocation.RIGHT -> {
                nav.turnByGyro(-vars[Turn_Angle] * 2)
                nav.driveByTime(vars[Auto_Power], vars[Drive_Time_2])
            }
        }
        detector.reset()
    }
}