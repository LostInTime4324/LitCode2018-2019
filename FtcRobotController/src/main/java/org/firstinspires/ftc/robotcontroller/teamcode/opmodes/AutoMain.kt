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
    val elevatorMotor by lazy {
        hardwareMap[HardwareNames.ELEVATOR_MOTOR] as DcMotor
    }

    val autoType get() = vars[Auto_Type]
    
    var detector = FtcRobotControllerActivity.detector

    override fun runOpMode() {
        telemetry.addData("Status", "Init")
        waitForStart()
        telemetry.addData("Status", "Starting")
        telemetry.addData("Auto Type: ", autoType)
        telemetry.update()

        nav.moveElevator(1.0, vars[Elevator_Up_Time])

        detector.started = true


        nav.driveByTime(vars[Auto_Power], 0.3)

        nav.moveElevator(-1.0, vars[Elevator_Down_Time])

        nav.turnByGyro(-vars[Turn_Correction_Angle])

        val order = detector.currentOrder

        telemetry.addData("Order", order)
        telemetry.addData("Left Certainty", detector.leftCertainty)
        telemetry.addData("Center Certainty", detector.centerCertainty)
        telemetry.addData("Right Certainty", detector.rightCertainty)
        telemetry.update()

        when(order) {
            SamplingOrderDetector.GoldLocation.LEFT -> {
                nav.turnByGyro(-vars[Turn_Angle])
            }
            SamplingOrderDetector.GoldLocation.RIGHT -> {
                nav.turnByGyro(vars[Turn_Angle])
            }
        }


        if(autoType == 0.0) {
            nav.driveByTime(vars[Auto_Power], vars[Drive_Time])
            nav.moveScoop(-1.0, vars[Scoop_Lowering_Time])
        } else {
            nav.driveByTime(vars[Auto_Power], vars[Drive_Time_2])
        }
        if(autoType == 0.0) {
            when (order) {
                SamplingOrderDetector.GoldLocation.LEFT -> {
                    nav.turnByGyro(vars[Turn_Angle] * 2)
                }
                SamplingOrderDetector.GoldLocation.RIGHT -> {
                    nav.turnByGyro(-vars[Turn_Angle] * 2)
                }
            }

            if (order != SamplingOrderDetector.GoldLocation.CENTER) {
                nav.driveByTime(vars[Auto_Power], vars[Drive_Time_2])

                when(order){
                    SamplingOrderDetector.GoldLocation.LEFT -> {
                        nav.turnByGyro(-vars[Turn_Angle] * 2)
                    }
                    SamplingOrderDetector.GoldLocation.RIGHT -> {
                        nav.turnByGyro(vars[Turn_Angle] * 2)
                    }
                }
                nav.driveByTime(vars[Auto_Power], vars[Drive_Time_2])

            }
        }


    }
}