package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.hardware.HardwareMap

/**
 * Created by walker on 3/4/18.
 */
enum class HardwareName {
    FRONT_RIGHT_MOTOR,
    BACK_RIGHT_MOTOR,
    FRONT_LEFT_MOTOR,
    BACK_LEFT_MOTOR,
    ELEVATOR_MOTOR,
    MINERAL_SERVO,
    ARM_MOTOR,
    INTAKE_MOTOR,
    IMU
}

operator fun HardwareMap.get(hardwareName: HardwareName) = this[hardwareName.name.replace("_", "").replace("MOTOR", "").replace("SERVO", "")]