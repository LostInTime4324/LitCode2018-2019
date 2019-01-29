package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcontroller.teamcode.BooleanVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.AUTO_SIDE.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.GOLD_LOCATION.*
import org.firstinspires.ftc.robotcontroller.teamcode.GoldDetector
import org.firstinspires.ftc.robotcontroller.teamcode.NumberVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.enums


@Autonomous(name = "AutoMain")
class AutoMain : AutoOpMode() {
    val detector = GoldDetector(this)

    override fun run() {

    }
}