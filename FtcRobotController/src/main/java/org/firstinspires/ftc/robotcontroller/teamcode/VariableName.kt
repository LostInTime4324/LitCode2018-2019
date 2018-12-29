package org.firstinspires.ftc.robotcontroller.teamcode

enum class VariableName(val default: Double = 0.0) {
    Auto_Power(0.5),
    Auto_Type,
    Servo_Pos_Center,
    Drive_Time(3.5),
    Drive_Time_2(3.0),
    Drive_Time_3(27.0),
    Turn_Angle,
    Turn_Angle_2,
    Auto_Wait_Time(5.0),
    Elevator_Up_Time(5.0),
    Scoop_Lowering_Time(3.0),
    Scoop_Raising_Time(0.3),
    Turn_Correction_Kd,
    Turn_Correction_Ki,
    Turn_Correction_Kp,
    Turn_Kd(0.001),
    Turn_Ki(0.0001),
    Turn_Kp(0.05),
    Drive_Kd,
    Drive_Kp,
    Drive_Ki
}