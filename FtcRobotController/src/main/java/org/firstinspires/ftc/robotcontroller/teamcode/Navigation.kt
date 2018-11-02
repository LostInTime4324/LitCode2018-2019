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
import kotlin.math.abs

class Navigation(val hardwareMap: HardwareMap, val telemetry: Telemetry) {
    val vars = Variables

    val frontLeftMotor: DcMotor by lazy {
        hardwareMap[FRONT_LEFT_MOTOR] as DcMotor
    }

//    val frontLeftPID = PID("Front Left", )

    val backLeftMotor by lazy {
        hardwareMap[BACK_LEFT_MOTOR] as DcMotor
    }

//    val backLeftPID = PID("Back Left", )

    val frontRightMotor by lazy {
        hardwareMap[FRONT_RIGHT_MOTOR] as DcMotor
    }

//    val frontLeftPID = PID("Front Left", )

    val backRightMotor by lazy {
        hardwareMap[BACK_RIGHT_MOTOR] as DcMotor
    }

//    val backLeftPID = PID("Back Left", )

    val motors by lazy { arrayOf(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor) }

    val averagePosition: Double get() = motors.sumBy { it.currentPosition } / (COUNTS_PER_INCH * motors.size)

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


    val turnPID = PID("Turn", vars[VariableNames.Turn_Kp], vars[VariableNames.Turn_Kd], vars[VariableNames.Turn_Ki])
    val turnCorrectionPID = PID("Turn Correction", vars[VariableNames.Turn_Correction_Kp], vars[VariableNames.Turn_Correction_Kd], vars[VariableNames.Turn_Correction_Ki])
    val drivePID = PID("Drive", vars[VariableNames.Drive_Kp], vars[VariableNames.Drive_Kd], vars[VariableNames.Drive_Ki])
    val distanceSensor by lazy { hardwareMap[X_DISTANCE_SENSOR] as DistanceSensor }

    val COUNTS_PER_MOTOR_REV = 1120.0    // eg: TETRIX Motor Encoder
    val DRIVE_GEAR_REDUCTION = 1.0     // This is < 1.0 if geared UP
    val WHEEL_DIAMETER_INCHES = 4.0     // For figuring circumference
    val COUNTS_PER_INCH = COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * 3.1415)
    val DRIVE_SPEED = 1
    val TURN_SPEED = 1


    fun getHeading() = imu.getAngularOrientation().firstAngle.toDouble()

    fun turnByGyro(angle: Double) {
        val target = angle - getHeading()
        do {
            val err = target - getHeading()
            val power = turnPID.getPower(err)
            telemetry.addData("Error", err)
            telemetry.addData("Target", target)
            telemetry.addData("Power", power)
            turnByEncoder(power)
        } while (turnPID.isMoving())
        turnPID.createGraphs()
    }

    fun turnByEncoder(power: Double) {

    }

    fun driveByPID(inches: Double) {
        val startHeading = getHeading()
        do {
            val correctionPower = turnCorrectionPID.getPower(startHeading - getHeading())
            val drivePower = drivePID.getPower(averagePosition - inches)
            setPower(drivePower + correctionPower, drivePower - correctionPower)
        } while (drivePID.isMoving())
        turnCorrectionPID.createGraphs()
        drivePID.createGraphs()
    }

    fun driveByEncoder(speed: Double, inches: Double) {

        motors.forEach {
            it.targetPosition = it.currentPosition + (inches * COUNTS_PER_INCH).toInt()
            it.mode = DcMotor.RunMode.RUN_USING_ENCODER
            it.power = abs(speed)
        }

        while (motors.all { it.isBusy() }) {

            // Display it for the driver.
            telemetry.addData("Path1", "Running to ${inches}")
            motors.forEach {
                telemetry.addData("Path2", "Running at ${it.currentPosition}")
            }
            telemetry.update()

        }
        resetPower()
    }

    fun setPower(left: Double, right: Double) {
        frontLeftMotor.power = left
        backLeftMotor.power = left
        frontRightMotor.power = -right
        backRightMotor.power = -right
    }

    fun resetPower() {
        motors.forEach {
            it.power = 0.0
        }
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