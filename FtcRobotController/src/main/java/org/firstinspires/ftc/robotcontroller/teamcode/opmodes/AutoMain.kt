package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation

@Autonomous(name="AutoMain")
class AutoMain : OpMode() {
    val nav by lazy {
        Navigation(hardwareMap, telemetry)
    }
    override fun init() {

    }

    override fun loop() {
        nav.turn(90.0)
    }
}