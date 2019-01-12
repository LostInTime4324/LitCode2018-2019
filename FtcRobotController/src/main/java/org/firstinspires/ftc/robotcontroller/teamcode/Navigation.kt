package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.*
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.*
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareName.*
import org.firstinspires.ftc.robotcontroller.teamcode.Navigation.Orientation.*
import org.firstinspires.ftc.robotcontroller.teamcode.Variables.*
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import kotlin.math.sign
import org.firstinspires.ftc.robotcontroller.teamcode.Variables as vars

open class Navigation(opMode: OpMode) {
    val hardwareMap = opMode.hardwareMap

    val telemetry = opMode.telemetry

    val timer = ElapsedTime()

    val totemServo by lazy {
        hardwareMap[Totem_Servo] as CRServo
    }

    val elevatorMotor by lazy {
        MotorEx(hardwareMap[Elevator_Motor], zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    }

    val armMotor by lazy {
        MotorEx(hardwareMap[Arm_Motor], zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    }

    val extenderMotor by lazy {
        MotorEx(hardwareMap[Extender_Motor])
    }

    val intakeMotor by lazy {
        MotorEx(hardwareMap[Intake_Motor])
    }

    val frontLeftMotor: DcMotor by lazy {
        MotorEx(hardwareMap[Front_Left_Motor])
    }

//    val frontLeftPID = PID("Front Left", )

    val backLeftMotor by lazy {
        MotorEx(hardwareMap[Back_Left_Motor])
    }

//    val backLeftPID = PID("Back Left", )

    val frontRightMotor by lazy {
        MotorEx(hardwareMap[Front_Right_Motor], REVERSE)
    }

//    val frontLeftPID = PID("Front Left", )

    val backRightMotor by lazy {
        MotorEx(hardwareMap[Back_Right_Motor], REVERSE)
    }

//    val backLeftPID = PID("Back Left", )

    val motors by lazy { setOf(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor) }

    val averagePosition: Double get() = motors.sumBy { it.currentPosition } / (COUNTS_PER_INCH * motors.size)

    val imu: BNO055IMU by lazy {
        val params = BNO055IMU.Parameters()
        with(params) {
            mode = BNO055IMU.SensorMode.IMU
            angleUnit = BNO055IMU.AngleUnit.DEGREES
            accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
            loggingEnabled = false
        }
        (hardwareMap[Imu] as BNO055IMU).apply {
            initialize(params)
            angularOrientation.axesOrder = AxesOrder.ZXY
        }
    }

    val COUNTS_PER_MOTOR_REV = 1120.0    // eg: TETRIX Motor Encoder
    val DRIVE_GEAR_REDUCTION = 1.0     // This is < 1.0 if geared UP
    val WHEEL_DIAMETER_INCHES = 4.0     // For figuring circumference
    val COUNTS_PER_INCH get() = vars[Encoder_Correction_Factor] * COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * 3.1415)
    val DRIVE_SPEED = 1
    val TURN_SPEED = 1

    fun getHeading() = imu.angularOrientation.firstAngle.toDouble()

    fun logEncoderValues() {
        telemetry.addData("FLC", frontLeftMotor.currentPosition)
        telemetry.addData("BLC", backLeftMotor.currentPosition)
        telemetry.addData("FRC", frontRightMotor.currentPosition)
        telemetry.addData("BRC", backRightMotor.currentPosition)
        telemetry.addData("FLT", frontLeftMotor.targetPosition)
        telemetry.addData("BLT", backLeftMotor.targetPosition)
        telemetry.addData("FRT", frontRightMotor.targetPosition)
        telemetry.addData("BRT", backRightMotor.targetPosition)
        telemetry.update()
    }

    fun setPower(frontLeft: Double, backLeft: Double, frontRight: Double, backRight: Double) {
        frontLeftMotor.power = frontLeft
        backLeftMotor.power = backLeft
        frontRightMotor.power = frontRight
        backRightMotor.power = backRight
    }

    fun setPower(left: Double, right: Double) {
        setPower(left, left, right, right)
    }

    fun setPower(power: Double) {
        setPower(power, power)
    }

    fun setPower(orientation: Orientation, power: Double) {
        when (orientation) {
            Horizontal -> setPower(-power, power, power, -power)
            Vertical -> setPower(power)
            Rotational -> setPower(power, -power)
        }
    }

    fun unsetDrivePower() {
        motors.forEach {
            it.power = 0.0
        }
    }

    fun unsetRobotPower() {
        unsetDrivePower()
        elevatorMotor.power = 0.0
        extenderMotor.power = 0.0
        armMotor.power = 0.0
    }

    enum class Orientation {
        Vertical,
        Horizontal,
        Rotational
    }

    fun setMode(mode: DcMotor.RunMode) {
        motors.forEach { it.mode = mode }
    }

    fun setTargetPosition(target: Int) {
        motors.forEach { it.targetPosition = target }
    }
}

class LinearNavigation(val opMode: LinearOpMode) : Navigation(opMode) {

    val turnPID = PID("Turn", vars[Turn_Kp], vars[Turn_Kd], vars[Turn_Ki])
    val turnCorrectionPID = PID("Turn Correction", vars[Turn_Correction_Kp], vars[Turn_Correction_Kd], vars[Turn_Correction_Ki])
    val drivePID = PID("Drive", vars[Drive_Kp], vars[Drive_Kd], vars[Drive_Ki])

    fun DcMotor.move(seconds: Double, power: Double) {
        this.power = power
        wait(seconds)
        this.power = 0.0
    }

    fun dropElevator() {
        telemetry.addData("Dropping", "Elevator")
        elevatorMotor.move(vars[Elevator_Power], vars[Elevator_Move_Time])
        driveByTime(0.3, 0.5)
        elevatorMotor.move(-vars[Elevator_Power], vars[Elevator_Move_Time])
    }

    fun dropTotem() {
        telemetry.addData("Dropping", "Totem")
        wait(vars[Totem_Move_Time]) {
            totemServo.power = vars[Totem_Power]
        }
        totemServo.power = 0.0
    }

    fun turn(degrees: Double) {
        telemetry.addData("Turning", degrees)
        telemetry.update()
        wait(2.0)
        turnByGyro(degrees)
    }

    fun turnByGyro(degrees: Double) {
        val target = getHeading() - degrees
        val startTime = timer.time()
        try {
            do {
                val err = getHeading() - target
                val power = turnPID.getPower(err)
                telemetry.update()
                setPower(power, -power)
            } while (turnPID.isMoving() && timer.time() - startTime < 5)
        } finally {
            turnPID.createGraphs()
            unsetDrivePower()
        }
    }

    fun turnByTime(seconds: Double, power: Double = vars[Drive_Power]) {
        setPower(power * seconds.sign, -power * seconds.sign)
        wait(seconds)
    }

    fun drive(inches: Double) {
        telemetry.addData("Driving", inches)
        telemetry.update()
        wait(2.0)
        driveByEncoder(inches)
    }


    fun driveByPID(inches: Double) {
        val startHeading = getHeading()
        do {
            val correctionPower = turnCorrectionPID.getPower(startHeading - getHeading())
            val drivePower = drivePID.getPower(averagePosition - inches)
            setPower(drivePower + correctionPower, drivePower - correctionPower)
        } while (drivePID.isMoving() && opMode.opModeIsActive() == true)
        turnCorrectionPID.createGraphs()
        drivePID.createGraphs()
    }

    fun driveByEncoder(distance: Double, power: Double = vars[Drive_Power]) {
        motors.forEach {
            it.mode = RUN_TO_POSITION
            it.targetPosition = it.currentPosition + (distance * COUNTS_PER_INCH).toInt()
        }
        setPower(power)
        val heading = getHeading()
        val (workingMotors, brokenMotors) = motors.partition { it.targetPosition != 0 }
        while (motors.all { it.isBusy } && opMode.opModeIsActive() == true) {
            // Display it for the driver.
//            val correction = turnCorrectionPID.getPower(heading - getHeading())
//            setPower(power , power)
//            brokenMotors.forEach { it.power = workingMotors.first().power }
            logEncoderValues()
        }
        motors.forEach {
            it.mode = STOP_AND_RESET_ENCODER
            it.mode = RUN_USING_ENCODER
        }
        unsetDrivePower()
    }

    fun driveByTime(seconds: Double, speed: Double = vars[Drive_Power]) {

        wait(seconds) {
            setPower(speed * seconds.sign)
        }
        unsetDrivePower()
    }

    fun wait(seconds: Double, runnable: () -> Unit = {}) {
        val startTime = timer.time()
        while(timer.time() - startTime < seconds && opMode.opModeIsActive()) {
            try {
                Thread.sleep(10)
            } catch (e: InterruptedException) {
                unsetRobotPower()
                telemetry.addData("Interrupted", e.message)
                telemetry.update()
                Thread.sleep(3000)
                return
            }
        }
    }
}