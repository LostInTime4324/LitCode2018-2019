package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.VariableName.*
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

        waitForStart()

//        nav.moveElevator(1.0, vars[Elevator_Up_Time])

//        nav.driveByTime(vars[Auto_Power], 0.3)

//        nav.moveElevator(-1.0, vars[Elevator_Up_Time])

        detector.reset()

        nav.wait(vars[Auto_Wait_Time])

        detector.pause()

        nav.turnByGyro(vars[Turn_Angle])

        detector.start()

        nav.wait(vars[Auto_Wait_Time])

        detector.pause()

        nav.turnByGyro(2 * -vars[Turn_Angle])

        detector.start()

        nav.wait(vars[Auto_Wait_Time])

        detector.pause()

        nav.wait(10.0)

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