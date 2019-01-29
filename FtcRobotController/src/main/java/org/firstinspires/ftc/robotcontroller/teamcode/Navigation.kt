package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.*
import com.qualcomm.robotcore.hardware.DcMotor.RunMode.*
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction.REVERSE
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.robotcontroller.teamcode.BooleanVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.Direction.*
import org.firstinspires.ftc.robotcontroller.teamcode.HardwareName.*
import org.firstinspires.ftc.robotcontroller.teamcode.NumberVariable.*
import org.firstinspires.ftc.robotcontroller.teamcode.opmodes.*
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder
import kotlin.math.*

enum class Direction(var sign: Int = 1) {
    FORWARD(-1),
    BACKWARD,
    RIGHT(-1),
    LEFT,
    CW,
    CCW(-1)
}


open class Navigation(opMode: OpMode) {
    val hardwareMap = opMode.hardwareMap

    val telemetry = opMode.telemetry

    val timer = ElapsedTime()

    val totemServo by lazy {
        hardwareMap[TOTEM_SERVO] as CRServo
    }

    val elevatorMotor by lazy {
        createMotor(hardwareMap[ELEVATOR_MOTOR], zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    }

    val armMotor by lazy {
        createMotor(hardwareMap[ARM_MOTOR], zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE)
    }

    val extenderMotor by lazy {
        createMotor(hardwareMap[EXTENDER_MOTOR])
    }

    val intakeMotor by lazy {
        createMotor(hardwareMap[INTAKE_MOTOR])
    }

    val frontLeftMotor by lazy {
        createMotor(hardwareMap[FRONT_LEFT_MOTOR])
    }

//    val frontLeftPID = PID("Front Left", )

    val backLeftMotor by lazy {
        createMotor(hardwareMap[BACK_LEFT_MOTOR])
    }

//    val backLeftPID = PID("Back Left", )

    val frontRightMotor by lazy {
        createMotor(hardwareMap[FRONT_RIGHT_MOTOR], REVERSE)
    }

//    val frontLeftPID = PID("Front Left", )

    val backRightMotor by lazy {
        createMotor(hardwareMap[BACK_RIGHT_MOTOR], REVERSE)
    }

//    val backLeftPID = PID("Back Left", )

    val motors by lazy { setOf(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor) }

    val averagePosition: Double get() = motors.sumBy { it.currentPosition } / (COUNTS_PER_INCH * motors.size)

    val drivePower = DrivePower()

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

    val COUNTS_PER_MOTOR_REV = 1120.0    // eg: TETRIX Motor Encoder
    val DRIVE_GEAR_REDUCTION = 1.0     // This is < 1.0 if geared UP
    val WHEEL_DIAMETER_INCHES = 4.0     // For figuring circumference
    val COUNTS_PER_INCH get() = ENCODER_CORRECTION_FACTOR.number * COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION / (WHEEL_DIAMETER_INCHES * 3.1415)

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

    inline fun setPower(powerSetter: DrivePower.() -> Unit) {
        with(drivePower) {
            powerSetter()
            setPower(frontLeft, backLeft, frontRight, backRight)
            resetPower()
        }
    }

    fun setPower(frontLeft: Double, backLeft: Double, frontRight: Double, backRight: Double) {
        frontLeftMotor.power = frontLeft
        backLeftMotor.power = backLeft
        frontRightMotor.power = frontRight
        backRightMotor.power = backRight
    }

    fun setPower(power: Double, direction: Direction) {
        val power = power * direction.sign
        when (direction) {
            FORWARD, BACKWARD -> setPower(power, power, power, power)
            RIGHT, LEFT -> setPower(-power, power, power, -power)
            CW, CCW -> setPower(-power, -power, power, power)
        }
    }

    fun resetDrivePower() {
        motors.forEach {
            it.power = 0.0
        }
    }

    fun resetRobotPower() {
        resetDrivePower()
        elevatorMotor.power = 0.0
        extenderMotor.power = 0.0
        armMotor.power = 0.0
    }


    fun reverseDirection() {
        FORWARD.sign *= -1
        BACKWARD.sign *= -1
        RIGHT.sign *= -1
        LEFT.sign *= -1
    }

    fun setMode(mode: DcMotor.RunMode) {
        motors.forEach { it.mode = mode }
    }

    fun setTargetPosition(target: Int) {
        motors.forEach { it.targetPosition = target }
    }

    class DrivePower {
        var frontLeft = 0.0
        var backLeft = 0.0
        var frontRight = 0.0
        var backRight = 0.0
        fun addPower(frontLeft: Double, backLeft: Double, frontRight: Double, backRight: Double) {
            this.frontLeft += frontLeft
            this.backLeft += backLeft
            this.frontRight += frontRight
            this.backRight += backRight
        }

        fun addPower(power: Double, direction: Direction) {
            val power = power * direction.sign
            when (direction) {
                FORWARD, BACKWARD -> addPower(power, power, power, power)
                RIGHT, LEFT -> addPower(-power, power, power, -power)
                CW, CCW -> addPower(-power, -power, power, power)
            }
        }

        fun resetPower() {
            frontLeft = 0.0
            backLeft = 0.0
            frontRight = 0.0
            backRight = 0.0
        }
    }
}

class AutoNavigation(val opMode: AutoOpMode) : Navigation(opMode) {
    val turnPID = PID("Turn", TURN_KP.number, TURN_KD.number, TURN_KI.number)
    val turnCorrectionPID = PID("Turn Correction", TURN_CORRECTION_KP.number, TURN_CORRECTION_KD.number, TURN_CORRECTION_KI.number)
    val drivePID = PID("Drive", DRIVE_KP.number, DRIVE_KD.number, DRIVE_KI.number)

    fun DcMotorSimple.move(seconds: Double, power: Double) {
        this.power = power
        wait(seconds)
        this.power = 0.0
    }

    fun dropElevator() {
        telemetry.addData("Dropping", "Elevator")
        elevatorMotor.move(ELEVATOR_MOVE_TIME.number, ELEVATOR_POWER.number)
//        driveByTime(0.5, -0.5, BACKWARD)
//        elevatorMotor.move(ELEVATOR_MOVE_TIME.number, -ELEVATOR_POWER.number)
    }

    fun dropTotem() {
        telemetry.addData("Dropping", "Totem")
        if(USE_SERVO.boolean) {
            totemServo.move(TOTEM_MOVE_TIME.number, TOTEM_POWER.number)
            driveByTime(2.0, DRIVE_SERVO_POWER.number)
        } else {
            driveByTime(2.0, DRIVE_SERVO_POWER.number)
            driveByTime(2.0, -DRIVE_SERVO_POWER.number)
        }
    }

    fun turn(degrees: Double) {
//        telemetry.addData("Turning", degrees)
//        telemetry.update()
//        wait(2.0)
        turnByTime(degrees)
    }

    fun turnByGyro(degrees: Double) {
        val target = getHeading() - degrees
        val startTime = timer.time()
        do {
            val err = getHeading() - target
            val power = turnPID.getPower(err)
            setPower(power, CW)
            opMode.checkForStop()
        } while (turnPID.isMoving() && timer.time() - startTime < 5)

        turnPID.createGraphs()
        resetDrivePower()
    }

    fun turnByTime(seconds: Double, power: Double = DRIVE_POWER.number) {
        setPower(power * seconds.sign, CW)
        wait(abs(seconds))
    }

    fun drive(inches: Double) {
//        telemetry.addData("Driving", inches)
//        telemetry.update()
//        wait(2.0)
        driveByEncoder(inches)
    }


    fun driveByPID(inches: Double) {
        val startHeading = getHeading()
        do {
            val correctionPower = turnCorrectionPID.getPower(startHeading - getHeading())
            val drivePower = drivePID.getPower(averagePosition - inches)
            setPower {
                addPower(drivePower, FORWARD)
                addPower(correctionPower, CW)
            }
            opMode.checkForStop()
        } while (drivePID.isMoving())
        turnCorrectionPID.createGraphs()
        drivePID.createGraphs()
    }

    fun driveByEncoder(distance: Double, power: Double = DRIVE_POWER.number) {
        motors.forEach {
            it.mode = RUN_TO_POSITION
            it.targetPosition = it.currentPosition + (distance * COUNTS_PER_INCH).toInt()
        }
        setPower(power, FORWARD)
        val heading = getHeading()
        while (motors.all { it.isBusy }) {
            val correction = turnCorrectionPID.getPower(heading - getHeading())
            setPower {
                addPower(power, FORWARD)
                addPower(correction, CW)
            }
            logEncoderValues()
            opMode.checkForStop()
        }
        motors.forEach {
            it.mode = STOP_AND_RESET_ENCODER
            it.mode = RUN_USING_ENCODER
        }
        resetDrivePower()
    }

    fun driveByTime(seconds: Double, speed: Double = DRIVE_POWER.number, direction: Direction = FORWARD) {
        setPower(speed * seconds.sign, direction)
        wait(seconds)
        resetDrivePower()
    }

    fun wait(seconds: Double) {
        val startTime = timer.time()
        while (timer.time() - startTime < seconds) {
            opMode.checkForStop()
        }
    }
}