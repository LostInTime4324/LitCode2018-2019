package org.firstinspires.ftc.robotcontroller.teamcode.opmodes

import com.qualcomm.robotcore.eventloop.opmode.*
import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.robotcontroller.teamcode.*

abstract class AutoOpMode: OpMode() {
    val timer = ElapsedTime()
    val nav by lazy {
        AutoNavigation(this)
    }

    init {
        msStuckDetectInit = 30000
        msStuckDetectInitLoop = 30000
        msStuckDetectLoop = 30000
        msStuckDetectStart = 30000
        msStuckDetectStop = 30000
    }

    override fun init() {

    }

    var firstLoop = true

    override fun loop() {
        if(firstLoop) {
            timer.reset()
            try {
                run()
            } catch(e: Exception) {
                nav.resetRobotPower()
                requestOpModeStop()
            } finally {
                nav.resetRobotPower()
                requestOpModeStop()
            }
            firstLoop = false
        }
    }

    fun checkForStop() {
        if(timer.time() > 29 || Thread.currentThread().isInterrupted) {
            throw InterruptedException()
        }
    }

    abstract fun run()
}