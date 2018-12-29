package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotor.*
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareName
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
import org.firstinspires.ftc.robotcontroller.teamcode.VariableName
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars


@Autonomous(name = "MecanumAuto")
class MecanumAuto : LinearOpMode() {
    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }

    val autoType get() = vars[VariableName.Auto_Type]
    val frontLeft = HardwareName.FRONT_LEFT_MOTOR
    val frontRight = HardwareName.FRONT_RIGHT_MOTOR
    val backLeft = HardwareName.BACK_LEFT_MOTOR
    val backRight = HardwareName.BACK_RIGHT_MOTOR

    override fun runOpMode() {
        waitForStart()

        nav.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER)
        nav.setMode(DcMotor.RunMode.RUN_TO_POSITION)

        nav.setTargetPosition(1070)
    }
}