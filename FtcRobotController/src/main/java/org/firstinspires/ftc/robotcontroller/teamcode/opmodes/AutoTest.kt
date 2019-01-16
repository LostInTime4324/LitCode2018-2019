package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcontroller.teamcode.LinearNavigation
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars


@Autonomous(name = "AutoTest")
class AutoTest : LinearOpMode() {
    val nav by lazy {
        LinearNavigation(this)
    }

    override fun runOpMode() {
        when(org.firstinspires.ftc.robotcontroller.teamcode.Variables.enums<org.firstinspires.ftc.robotcontroller.teamcode.Variables.AutoType>()) {
             -> {
                nav.driveByEncoder(vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Test_Variable])
            }
            1.0 -> {
                nav.driveByPID(vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Test_Variable])
            }
            2.0 -> {
                nav.driveByTime(vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Test_Variable])
            }
            3.0 -> {
                nav.turnByGyro(vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Test_Variable])
            }
            4.0 -> {
                nav.turnByTime(vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Test_Variable])
            }
            4.0 -> {
                nav.armMotor.power = vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Drive_Power]
                nav.wait(vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Test_Variable])
                nav.armMotor.power = 0.0
            }
            5.0 -> {
                nav.extenderMotor.power = vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Drive_Power]
                nav.wait(vars[org.firstinspires.ftc.robotcontroller.teamcode.Variables.Number.Test_Variable])
                nav.extenderMotor.power = 0.0
            }
            6.0 -> {

            }
        }
    }
}