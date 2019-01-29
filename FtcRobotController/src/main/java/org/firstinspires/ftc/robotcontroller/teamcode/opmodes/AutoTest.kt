package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import org.firstinspires.ftc.robotcontroller.teamcode.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.AUTO_TEST.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.AUTO_TEST.DETECT
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.GOLD_LOCATION.*
import org.firstinspires.ftc.robotcontroller.teamcode.NumberVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.enums

@Autonomous(name = "AutoTest")
class AutoTest : AutoOpMode() {
//    val nav by lazy {
//                AutoNavigation(this)
//    }
    override fun run() {
        when (enums<AUTO_TEST>()) {
            WAIT -> {
                nav.wait(TEST_VARIABLE.number)
            }
            DETECT -> {
                val detector = GoldDetector(this)
                detector.start()
            }
            DRIVE_BY_ENCODERS -> {
                nav.driveByEncoder(TEST_VARIABLE.number)
            }
            DRIVE_BY_PID -> {
                nav.driveByPID(TEST_VARIABLE.number)
            }
            DRIVE_BY_TIME -> {
                nav.driveByTime(TEST_VARIABLE.number)
            }
            TURN_BY_GYRO -> {
                nav.turnByGyro(TEST_VARIABLE.number)
            }
            TURN_BY_TIME -> {
                nav.turnByTime(TEST_VARIABLE.number)
            }
            DROP_TOTEM -> {
                nav.dropTotem()
            }
            DROP_ELEVATOR -> {
                nav.dropElevator()
            }
            PUSH_MINERAL -> {
                val detector = GoldDetector(this)
                detector.start()
                val goldLocation = detector.goldLocation
                when (goldLocation) {
                    LEFT -> {
                        nav.turn(ANGLE_TO_CENTER_MINERAL.number - ANGLE_TO_SIDE_MINERAL.number)
                        nav.drive(DISTANCE_TO_SIDE_MINERAL.number)
                        nav.turn(2 * ANGLE_TO_SIDE_MINERAL.number)
                        nav.drive(DISTANCE_TO_DEPOT_ON_SIDE.number)
                    }
                    CENTER, GOLD_LOCATION.DETECT -> {
                        nav.turn(ANGLE_TO_CENTER_MINERAL.number)
                        nav.drive(DISTANCE_TO_CENTER_MINERAL.number)
                        nav.drive(DISTANCE_TO_DEPOT_ON_CENTER.number)
                    }
                    RIGHT -> {
                        nav.turn(ANGLE_TO_CENTER_MINERAL.number + ANGLE_TO_SIDE_MINERAL.number)
                        nav.drive(DISTANCE_TO_SIDE_MINERAL.number)
                        nav.turn(2 * -ANGLE_TO_SIDE_MINERAL.number)
                        nav.drive(DISTANCE_TO_DEPOT_ON_SIDE.number)
                    }
                }
                nav.dropTotem()
            }
        }
    }
}