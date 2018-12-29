package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation
import org.firstinspires.ftc.robotcontroller.teamcode.VariableName.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars


@Autonomous(name = "AutoTest")
class AutoTest : LinearOpMode() {
    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }

    override fun runOpMode() {
        nav.driveByTime(vars[Auto_Power], 5.0)
    }
}