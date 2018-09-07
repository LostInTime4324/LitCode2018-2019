package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DistanceSensor
import com.qualcomm.robotcore.hardware.HardwareMap
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.BACK_LEFT_MOTOR
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.BACK_RIGHT_MOTOR
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.FRONT_LEFT_MOTOR
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.FRONT_RIGHT_MOTOR
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.IMU
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit

class Navigation(val hardwareMap: HardwareMap, val telemetry: Telemetry) {
    val vars = Variables
    val frontLeftMotor = hardwareMap[FRONT_LEFT_MOTOR] as DcMotor
    val backLeftMotor = hardwareMap[BACK_LEFT_MOTOR] as DcMotor
    val frontRightMotor = hardwareMap[FRONT_RIGHT_MOTOR] as DcMotor
    val backRightMotor = hardwareMap[BACK_RIGHT_MOTOR] as DcMotor

    val imu: BNO055IMU by lazy {
        val params = BNO055IMU.Parameters()
        with(params) {
            mode = BNO055IMU.SensorMode.IMU
            angleUnit = BNO055IMU.AngleUnit.DEGREES
            accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
            loggingEnabled = false
        }
        (hardwareMap[IMU] as BNO055IMU).apply {
            initialize(params)
            angularOrientation.axesOrder = AxesOrder.ZXY
        }
    }


    val turnPID = PID(vars[Numbers.Turn_Kp], vars[Numbers.Turn_Kd], vars[Numbers.Turn_Ki])
    val drivePID = PID(vars[Numbers.Drive_Kp], vars[Numbers.Drive_Kd], vars[Numbers.Drive_Ki])

    lateinit var xDistanceSensor: DistanceSensor// = hardwareMap[X_DISTANCE_SENSOR] as DistanceSensor
    lateinit var yDistanceSensor: DistanceSensor// = hardwareMap[Y_DISTANCE_SENSOR] as DistanceSensor

    fun getXPos() = xDistanceSensor.getDistance(DistanceUnit.INCH)
    fun getYPos() = yDistanceSensor.getDistance(DistanceUnit.INCH)

    fun getHeading() = imu.getAngularOrientation().firstAngle.toDouble()

    fun moveToPoint(targetPos: Vector) {
        telemetry.addAction {
            "Error: ${getXPos() - targetPos.x} Y error: ${getYPos() - targetPos.y}"
        }
        var startHeading = getHeading()
        drivePID.reset()
        turnPID.reset()
        moveTo(Orientation.Horizontal, targetPos.x, startHeading)
        drivePID.createGraphs()
        startHeading = getHeading()
        drivePID.reset()
        turnPID.reset()
        moveTo(Orientation.Vertical, targetPos.y, startHeading)
        drivePID.createGraphs()
        resetPower()
    }

    fun moveTo(orientation: Orientation, target: Double, startHeading: Double) {

        do {
            val pos = when (orientation) {
                Orientation.Vertical -> getXPos()
                Orientation.Horizontal -> getYPos()
            }
            val err = pos - target
            val drivePower = drivePID.getPower(err)
            val curHeading = getHeading()
            val headErr = startHeading - curHeading
            val turnPower = turnPID.getPower(headErr)
            resetPower()
            addStraightPower(orientation, drivePower)
            addTurnPower(turnPower)
            wait(0.1)
        } while (drivePID.isStillMoving())
    }

    fun turn(power: Double) {
        setPower(power, power, power, power)
    }

    fun drive(orientation: Orientation, power: Double) {
        when (orientation) {
            Orientation.Vertical -> setPower(power, power, -power, -power)
            Orientation.Horizontal -> setPower(power, -power, power, -power)
        }
    }

    fun addTurnPower(power: Double) {
        addPower(power, power, power, power)
    }



    fun addStraightPower(orientation: Orientation, power: Double) {
        when (orientation) {
            Orientation.Horizontal -> addPower(power, power, -power, -power)
            Orientation.Vertical -> addPower(power, -power, power, -power)
        }
    }

    fun addPower(frontLeft: Double, backLeft: Double, frontRight: Double, backRight: Double) {
        frontLeftMotor.power += frontLeft
        backLeftMotor.power += backLeft
        frontRightMotor.power += frontRight
        backRightMotor.power += backRight
    }

    fun setPower(frontLeft: Double, backLeft: Double, frontRight: Double, backRight: Double) {
        frontLeftMotor.power = frontLeft
        backLeftMotor.power = backLeft
        frontRightMotor.power = frontRight
        backRightMotor.power = backRight
    }

    fun resetPower() {
        frontLeftMotor.power = 0.0
        backLeftMotor.power = 0.0
        frontRightMotor.power = 0.0
        backRightMotor.power = 0.0
    }

    enum class Direction {
        Forward,
        Backward,
        Right,
        Left,
    }

    enum class Orientation {
        Vertical,
        Horizontal
    }

    fun wait(seconds: Double) {
        val timer = ElapsedTime()
        while (timer.time() < seconds);
    }
}