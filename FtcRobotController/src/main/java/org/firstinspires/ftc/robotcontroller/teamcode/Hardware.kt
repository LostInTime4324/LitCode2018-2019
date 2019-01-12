package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * Created by walker on 3/4/18.
 */
enum class HardwareName {
    Front_Right_Motor,
    Back_Right_Motor,
    Front_Left_Motor,
    Back_Left_Motor,
    Elevator_Motor,
    Arm_Motor,
    Extender_Motor,
    Intake_Motor,
    Totem_Servo,
    Imu
}

operator fun HardwareMap.get(hardwareName: HardwareName) = this[hardwareName.name.replace("_", "").replace("Motor", "").replace("Servo", "").toLowerCase()]