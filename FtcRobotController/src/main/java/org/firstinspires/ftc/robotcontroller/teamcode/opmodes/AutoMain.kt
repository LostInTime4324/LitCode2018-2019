package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
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
        detector.start()

        waitForStart()

        detector.start()


        nav.moveElevator(1.0, vars[Elevator_Up_Time])

        nav.driveByTime(vars[Auto_Power], 0.3)

        nav.moveElevator(-1.0, vars[Elevator_Down_Time])
        nav.wait(0.3 + vars[Elevator_Down_Time])

        val order = detector.currentOrder

//        when (order) {
//            SamplingOrderDetector.GoldLocation.LEFT -> {
//                nav.turnByGyro(-vars[Turn_Angle])
//            }
//            SamplingOrderDetector.GoldLocation.RIGHT -> {
//                nav.turnByGyro(vars[Turn_Angle])
//            }
//        }

        nav.driveByTime(vars[Auto_Power], vars[Drive_Time])

//        when (order) {
//            SamplingOrderDetector.GoldLocation.LEFT -> {
//                nav.turnByGyro(vars[Turn_Angle] * 2)
//                nav.driveByTime(vars[Auto_Power], vars[Drive_Time_2])
//            }
//            SamplingOrderDetector.GoldLocation.RIGHT -> {
//                nav.turnByGyro(-vars[Turn_Angle] * 2)
//                nav.driveByTime(vars[Auto_Power], vars[Drive_Time_2])
//            }
//        }
        detector.disable()
    }
}