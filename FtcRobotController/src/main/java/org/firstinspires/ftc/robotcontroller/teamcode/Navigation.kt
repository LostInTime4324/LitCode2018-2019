package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.hardware.bosch.BNO055IMU
import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.HardwareMap
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames.BACK_LEFT_MOTOR
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames.BACK_RIGHT_MOTOR
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames.FRONT_LEFT_MOTOR
import org.firstinspires.ftc.robotcontroller.teamcode.VariableNames.FRONT_RIGHT_MOTOR
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder

class Navigation(val hardwareMap: HardwareMap, val telemetry: Telemetry, var pos: Vector) {
    constructor(hardwareMap: HardwareMap, telemetry: Telemetry) : this(hardwareMap, telemetry, Vector())

    var vars = Variables

    val frontLeftMotor = hardwareMap[FRONT_LEFT_MOTOR] as DcMotor
    val backLeftMotor = hardwareMap[BACK_LEFT_MOTOR] as DcMotor
    val frontRightMotor = hardwareMap[FRONT_RIGHT_MOTOR] as DcMotor
    val backRightMotor by lazy {
        telemetry.addData("Abbdulla is gay", "")
        telemetry.update()
        hardwareMap[BACK_RIGHT_MOTOR] as DcMotor
    }

    val imu: BNO055IMU by lazy {
        val params = BNO055IMU.Parameters()
        params.mode = BNO055IMU.SensorMode.IMU
        params.angleUnit = BNO055IMU.AngleUnit.DEGREES
        params.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC
        params.loggingEnabled = false
        val IMU = hardwareMap.get(BNO055IMU::class.java, "imu")
        IMU.initialize(params)
        IMU.angularOrientation.axesOrder = AxesOrder.ZXY
        IMU
    }

    val turnPID = PID(vars[Names.Turn_Kp], vars[Names.Turn_Kd], vars[Names.Turn_Ki], ::turnController)
    val drivePID = PID(vars[Names.Drive_Kp], vars[Names.Drive_Kd], vars[Names.Drive_Ki], ::driveController)
    val frontLeftPID = PID(vars[Names.Front_Left_Motor_Kp], vars[Names.Front_Left_Motor_Kd], vars[Names.Front_Left_Motor_Ki], ::motorController)
    val frontRightPID = PID(vars[Names.Front_Right_Motor_Kp], vars[Names.Front_Right_Motor_Kd], vars[Names.Front_Right_Motor_Ki], ::motorController)
    val backLeftPID = PID(vars[Names.Back_Left_Motor_Kp], vars[Names.Back_Left_Motor_Kd], vars[Names.Back_Left_Motor_Ki], ::motorController)
    val backRightPID = PID(vars[Names.Back_Right_Motor_Kp], vars[Names.Back_Right_Motor_Kd], vars[Names.Back_Right_Motor_Ki], ::motorController)

    val heading = imu.getAngularOrientation().firstAngle

    fun motorController(power: Double, setPoint: Double) : Double{
        return 0.0
    }

    fun driveController(power: Double, setPoint: Double) : Double {
        return 0.0
    }

    fun turnController(power: Double, setPoint: Double) :Double {
        return 0.0
    }

    fun move(distance: Double) {

    }

    fun move(point: Vector) {

    }

    fun rotate(angle: Double) {

    }

    fun turn(power: Double) {
        setDriveMotors(power, power, -power, -power)
    }

    fun drive(direction: Direction, power: Double) {
        when (direction) {
            Direction.Forward -> setDriveMotors(power, power, power, power)
            Direction.Backward -> setDriveMotors(-power, -power, -power, -power)
            Direction.Right -> setDriveMotors(-power, power, -power, power)
            Direction.Left -> setDriveMotors(power, -power, power, -power)
        }
    }

    fun setDriveMotors(frontLeft: Double, backLeft: Double, frontRight: Double, backRight: Double) {
        frontLeftMotor.power = frontLeft
        backLeftMotor.power = backLeft
        frontRightMotor.power = frontRight
        backRightMotor.power = backRight
    }

    fun stopDriveMotors() {
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
}