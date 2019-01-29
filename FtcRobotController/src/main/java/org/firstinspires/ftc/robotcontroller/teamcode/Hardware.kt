package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.hardware.HardwareMap
import java.lang.Exception

/**
 * Created by walker on 3/4/18.
 */
enum class HardwareName {
    FRONT_RIGHT_MOTOR,
    BACK_RIGHT_MOTOR,
    FRONT_LEFT_MOTOR,
    BACK_LEFT_MOTOR,
    ELEVATOR_MOTOR,
    ARM_MOTOR,
    EXTENDER_MOTOR,
    INTAKE_MOTOR,
    TOTEM_SERVO,
    IMU
}

operator fun HardwareMap.get(hardwareName: HardwareName) =
        this[hardwareName.name
                .replace("_", "")
                .replace("MOTOR", "")
                .replace("SERVO", "")
                .toLowerCase()]