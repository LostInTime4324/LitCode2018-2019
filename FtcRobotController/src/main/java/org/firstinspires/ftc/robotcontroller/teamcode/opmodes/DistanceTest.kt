package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.hardware.DistanceSensor
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.SENSOR


@TeleOp(name = "DistanceTest")
class DistanceTest : OpMode() {

    val sensor by lazy {
        hardwareMap[SENSOR] as DistanceSensor
    }

    override fun init() {}

    override fun loop() {
        telemetry.addData("Distance", sensor.getDistance(DistanceUnit.INCH))

        telemetry.update()

    }

}
