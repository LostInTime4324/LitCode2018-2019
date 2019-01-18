package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.AutoType.*
import org.firstinspires.ftc.robotcontroller.teamcode.LinearNavigation
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.enums
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars


@Autonomous(name = "AutoTest")
class AutoTest : LinearOpMode() {
    val nav by lazy {
        LinearNavigation(this)
    }

    override fun runOpMode() {
        when(enums<AutoType>()) {
            Depot_No_Totem -> {}

//             -> {
//                nav.driveByEncoder(vars[Test_Variable])
//            }
//            1.0 -> {
//                nav.driveByPID(vars[Test_Variable])
//            }
//            2.0 -> {
//                nav.driveByTime(vars[Test_Variable])
//            }
//            3.0 -> {
//                nav.turnByGyro(vars[Test_Variable])
//            }
//            4.0 -> {
//                nav.turnByTime(vars[Test_Variable])
//            }
//            4.0 -> {
//
//                nav.armMotor.power = vars[Drive_Power]
//                nav.wait(vars[Test_Variable])
//                nav.armMotor.power = 0.0
//            }
//            5.0 -> {
//                nav.extenderMotor.power = vars[Drive_Power]
//                nav.wait(vars[Test_Variable])
//                nav.extenderMotor.power = 0.0
//            }
//            6.0 -> {
//
//            }
        }
    }
}