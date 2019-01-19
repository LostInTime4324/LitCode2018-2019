package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcontroller.teamcode.GoldDetector
import org.firstinspires.ftc.robotcontroller.teamcode.GoldDetector.GoldLocation.*
import org.firstinspires.ftc.robotcontroller.teamcode.LinearNavigation
import org.firstinspires.ftc.robotcontroller.teamcode.NumberVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.vars


@Autonomous(name = "AutoMain")
class AutoMain : LinearOpMode() {
    val nav by lazy {
        LinearNavigation(this)
    }

    val detector = GoldDetector(this)

    enum class State {
        STOP,
        MOVING_ELEVATOR,
        DETECTING_MINERALS,
        PUSHING_MINERAL,
        DROPPING_TOTEM,
        MOVING_TO_DEPOT,
        MOVING_TO_CRATOR
    }

    enum class MineralSide {
        DETETCT,
        LEFT,
        CENTER,
        RIGHT;
        fun Double.toMineralSide() = when(this) {
            0.0 -> DETETCT
            1.0 -> LEFT
            3.0 -> RIGHT
            else -> CENTER
        }
    }

    enum class StartSide {
        DEPOT,
        CRATOR
    }

    override fun runOpMode() {
        val mineralSide = vars[Mineral_Side]
        if(mineralSide == 0.0) {
            detector.start()
        }
        waitForStart()
        if (vars[Using_Elevator] == 1.0) {
            nav.dropElevator()
        }

        val goldLocation: GoldDetector.GoldLocation

        if (mineralSide == 0.0) {
            goldLocation = detector.goldLocation
        } else {
            goldLocation = when (mineralSide) {
                1.0 -> LEFT
                2.0 -> CENTER
                3.0 -> RIGHT
                else -> CENTER
            }
        }


        val doBothObjectives = vars[Do_Both_Objectives] == 1.0

        val onDepot = vars[On_Depot_Side] == 1.0

        val mineralTurnAngle = when (goldLocation) {
            LEFT -> -vars[Angle_To_Side_Mineral]
            RIGHT -> vars[Angle_To_Side_Mineral]
            CENTER -> 0.0
        }

        val mineralForwardDistance = when (goldLocation) {
            LEFT, RIGHT -> vars[Distance_To_Side_Mineral]
            CENTER -> vars[Distance_To_Center_Minteral]
        }

        val mineralBackwardDistance = when (goldLocation) {
            LEFT, RIGHT -> vars[Distance_Backwards_On_Side_Mineral]
            CENTER -> vars[Distance_Backwards_On_Center_Mineral]
        }

        nav.turn(mineralTurnAngle)

        if (onDepot) {
            nav.telemetry.addData("Driving", "Mineral")
            nav.drive(mineralForwardDistance)
            telemetry.addData("Turning", "Depot")
            nav.turn(-mineralTurnAngle * 2)
            telemetry.addData("Driving", "Depot")
            when (goldLocation) {
                LEFT, RIGHT -> nav.drive(vars[Distance_To_Depot_On_Side])
                CENTER -> nav.drive(vars[Distance_To_Depot_On_Center])
            }
            nav.dropTotem()
            if (doBothObjectives) {
                nav.turn(vars[Angle_To_Crater_On_Depot] + mineralTurnAngle)
                nav.drive(vars[Distance_To_Crater_On_Depot])
            }
        } else if (!onDepot) {
            if (doBothObjectives) {
                nav.drive(-mineralBackwardDistance)
                nav.turn(mineralTurnAngle - vars[Angle_To_Wall_On_Crater])
                nav.drive(vars[Distance_To_Wall_On_Crater])
                nav.turn(-vars[Angle_To_Depot_On_Crater])
                nav.drive(vars[Distance_To_Depot_On_Center])
                nav.dropTotem()
                nav.drive(-vars[Distance_To_Crater_On_Crater])
            }
        }
        detector.stop()
    }
}