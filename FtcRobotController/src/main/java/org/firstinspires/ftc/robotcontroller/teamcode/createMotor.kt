package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorSimple
import com.qualcomm.robotcore.hardware.HardwareDevice

fun createMotor(motor: HardwareDevice, direction: DcMotorSimple.Direction = DcMotorSimple.Direction.FORWARD, zeroPowerBehavior: DcMotor.ZeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT, mode: DcMotor.RunMode = DcMotor.RunMode.RUN_USING_ENCODER) =
        (motor as DcMotor).apply {
            this.direction = direction
            this.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
            this.mode = mode
            this.zeroPowerBehavior = zeroPowerBehavior
        }