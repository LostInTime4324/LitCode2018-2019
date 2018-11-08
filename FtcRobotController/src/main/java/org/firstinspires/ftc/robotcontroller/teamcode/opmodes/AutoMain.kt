package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector
import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.LEFT
import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector.GoldLocation.RIGHT
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

    var detector: SamplingOrderDetector = FtcRobotControllerActivity.detector

    override fun runOpMode() {
        detector.started = true;
        telemetry.addData("Status", "Init")
        waitForStart()
        telemetry.addData("Status", "Starting")
//        telemetry.addData("Auto Type: ", autoType)
        telemetry.update()

        val onDepotSide = autoType == 0.0

        nav.moveElevator(1.0, vars[Elevator_Up_Time])

        nav.turnByGyro(-vars[Correction_Turn_Angle])

        when (detector.currentOrder) {
            LEFT -> {
                nav.turnByGyro(-vars[Left_Turn_Angle])
            }
            RIGHT -> {
                nav.turnByGyro(vars[Right_Turn_Angle])
            }
        }

        nav.driveByTime(vars[Auto_Power], vars[Drive_Time])

        when (detector.currentOrder) {
            LEFT -> {
                nav.turnByGyro(vars[Left_Turn_Angle])
                if (onDepotSide)
                    nav.turnByGyro(vars[Right_Turn_Angle])
            }
            RIGHT -> {
                nav.turnByGyro(-vars[Right_Turn_Angle])
                if (onDepotSide)
                    nav.turnByGyro(-vars[Left_Turn_Angle])
            }
        }
        if (onDepotSide) {
            nav.driveByTime(vars[Auto_Power], vars[Drive_Time] / 2)
            nav.moveScoop(-0.3, vars[Scoop_Lowering_Time])
            nav.moveScoop(1.0, vars[Scoop_Raising_Time])
        } else {
            nav.driveByTime(vars[Auto_Power], vars[Drive_Time] / 4)
        }


//        when (autoType) {
//            0.0 -> {
//                nav.turnByGyro(vars[Right_Turn_Angle])
//            }
//            1.0 -> {
//                nav.turnByGyro(vars[Left_Turn_Angle])
//            }
//            2.0 -> {
//                nav.driveByTime(vars[Auto_Power], vars[Drive_Time])
//            }
//            3.0 -> {
//
//            }
//            else -> {
//                telemetry.addData("Status", "Invalid auto type")
//                telemetry.update()
//            }
//        }
    }
}