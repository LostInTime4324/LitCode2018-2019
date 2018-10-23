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
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareNames.X_DISTANCE_SENSOR
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder

class Navigation(val hardwareMap: HardwareMap, val telemetry: Telemetry) {
    val vars = Variables
    val frontLeftMotor: DcMotor by lazy {
        hardwareMap[FRONT_LEFT_MOTOR] as DcMotor
    }
    val backLeftMotor by lazy {
        hardwareMap[BACK_LEFT_MOTOR] as DcMotor
    }
    val frontRightMotor by lazy {
        hardwareMap[FRONT_RIGHT_MOTOR] as DcMotor
    }
    val backRightMotor by lazy {
        hardwareMap[BACK_RIGHT_MOTOR] as DcMotor
    }

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
    val distanceSensor by lazy { hardwareMap[X_DISTANCE_SENSOR] as DistanceSensor }

    fun getHeading() = imu.getAngularOrientation().firstAngle.toDouble()

    fun turnByGyro(angle: Double) {
        val startHeading = getHeading()

        do {
            val target = angle - startHeading
            val err =  target - getHeading()
            val power = turnPID.getPower(err)
            telemetry.addData("Error", err)
            telemetry.addData("Target", target)
            telemetry.addData("Power", power)
            turn(power)
        } while(turnPID.isStillMoving())
        turnPID.createGraphs()
    }

    fun turn(power: Double) {
        setPower(power, power, power, power)
    }

    fun drive(power: Double) {
           setPower(power, power, -power, -power)
    }

    fun addTurnPower(power: Double) {
        addPower(power, power, power, power)
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