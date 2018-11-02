package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.disnodeteam.dogecv.detectors.roverrukus.SamplingOrderDetector
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames
import org.firstinspires.ftc.robotcontroller.teamcode.Variables
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity.detector
import com.disnodeteam.dogecv.DogeCV
import com.disnodeteam.dogecv.CameraViewDisplay
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity.detector





@Autonomous(name="AutoMain")
class AutoMain : OpMode() {
    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }
    val elevatorMotor by lazy {
        hardwareMap[HardwareNames.ELEVATOR_MOTOR] as DcMotor
    }

    val AutoType by lazy {
        Variables[VariableNames.Auto_Type]
    }

    var detector = FtcRobotControllerActivity.detector

    override fun init() {
        telemetry.addData("Status", "DogeCV 2018.0 - Sampling Order Example")

        // Setup detector
        detector = SamplingOrderDetector() // Create the detector
        detector.init(hardwareMap.appContext, CameraViewDisplay.getInstance()) // Initialize detector with app context and camera
        detector.useDefaults() // Set detector to use default settings

        detector.downscale = 0.4 // How much to downscale the input frames

        // Optional tuning
        detector.areaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA // Can also be PERFECT_AREA
        //detector.perfectAreaScorer.perfectArea = 10000; // if using PERFECT_AREA scoring
        detector.maxAreaScorer.weight = 0.001

        detector.ratioScorer.weight = 15.0
        detector.ratioScorer.perfectRatio = 1.0

        detector.enable() // Start detector
    }

    override fun loop() {
        telemetry.addData("Current Order", detector.currentOrder.toString()) // The current result for the frame
        telemetry.addData("Last Order", detector.lastOrder.toString()) // The last known result
    }

    /*
     * Code to run ONCE after the driver hits STOP
     */
    override fun stop() {
        detector.disable()
    }

    //    override fun runOpMode() {
//        telemetry.addData("Status", "Resetting Encoders")
//        telemetry.update()
//
//        nav.frontLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
//        nav.backLeftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
//        nav.frontRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
//        nav.backRightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
//        nav.frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
//        nav.backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
//        nav.frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
//        nav.backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER)
//        telemetry.addData("Path0", "Starting at %7d :%7d",
//                nav.frontRightMotor.currentPosition,
//                nav.frontLeftMotor.currentPosition,
//                nav.backRightMotor.currentPosition,
//                nav.backLeftMotor.currentPosition)
//        telemetry.update()
//        while(!isStarted);
//        when(AutoType) {
//            0.0 -> {
//                nav.driveByEncoder(1.0, 12.0)
//            }
//            1.0 -> {
//                nav.driveByPID(12.0)
//            }
//            2.0 -> {
//                nav.turnByGyro(90.0)
//            }
//
//
//        }
//    }
}