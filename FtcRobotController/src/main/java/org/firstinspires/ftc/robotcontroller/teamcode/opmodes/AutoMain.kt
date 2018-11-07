package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.hardware.DcMotor
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames
import org.firstinspires.ftc.robotcontroller.teamcode.Variables
import com.qualcomm.robotcore.eventloop.opmode.OpMode


@Autonomous(name = "AutoMain")
class AutoMain : LinearOpMode() {
    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }
    val elevatorMotor by lazy {
        hardwareMap[HardwareNames.ELEVATOR_MOTOR] as DcMotor
    }

    val autoType get() = Variables[VariableNames.Auto_Type]

    var detector = FtcRobotControllerActivity.detector

    override fun runOpMode() {
        telemetry.addData("Status", "Init")
        waitForStart()
        telemetry.addData("Status", "Starting")
        telemetry.addData("Auto Type: ", autoType)
        telemetry.update()

        when (autoType) {
            0.0 -> {
                nav.turnByGyro(Variables[VariableNames.Right_Turn_Angle])
            }
            1.0 -> {
                nav.turnByGyro(Variables[VariableNames.Left_Turn_Angle])
            }
            2.0 -> {
                nav.driveByTime(Variables[VariableNames.Auto_Power], Variables[VariableNames.Auto_Drive_Time])
            }
            3.0 ->{
                elevatorMotor.power = 1.0
                nav.wait(Variables[VariableNames.Elevator_Up_Time])
                elevatorMotor.power = 0.0
                nav.driveByTime(Variables[VariableNames.Auto_Power], Variables[VariableNames.Auto_Drive_Time])
                elevatorMotor.power = 0.0
                nav.scoopMotor.power = -0.3
                nav.wait(Variables[VariableNames.Scoop_Lowering_Time])
                nav.scoopMotor.power = 1.0
                nav.wait(Variables[VariableNames.Scoop_Raising_Time])
                nav.scoopMotor.power = 0.0


            }
            else -> {
                telemetry.addData("Status", "Invalid auto type")
                telemetry.update()
            }
        }
    }
}