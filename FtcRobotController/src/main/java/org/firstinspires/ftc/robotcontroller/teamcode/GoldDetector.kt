package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.util.*
import org.firstinspires.ftc.robotcontroller.teamcode.EnumVariable.GOLD_LOCATION.*
import org.firstinspires.ftc.robotcontroller.teamcode.opmodes.*
import org.firstinspires.ftc.robotcore.external.ClassFactory

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

/**
 * Created by Victo on 9/10/2018.
 */

class GoldDetector(val opMode: AutoOpMode) {

    private val TFOD_MODEL_ASSET = "RoverRuckus.tflite"
    private val LABEL_GOLD_MINERAL = "Gold Mineral"
    private val LABEL_SILVER_MINERAL = "Silver Mineral"
    private val VUFORIA_KEY = "AULANuj/////AAAAGXP1rGuspEfwmNTiYB89npgWAI6tNF/zt/+SirGvZoaHr/poHkRud0Pk2TPOXzH6tgtsCwUng6pGd9p7lfep/x6hTM7ypOfxXrWRaoe4sPUdqCcbi2uYNhpGpAIFrVo8dIQhOUV8k0qC92aUSCamX+kNBy/bI7ZVDICcl8xMuZsdVGlOn/VoBLIiuI1m3Mwn17vL02hvaydJpLJ5XYy61FPAE8rybDygjaQTRN6+te6USLSw8hWJErPRQPYLsWFxTLxAXhPFvqxuOXQq8U/glopqV7+SZ2zv0lCdwH1mOcl7YzTLGdBVebYfn1psHnniIChBQNDBTBTo4sKgwQGSoI6DkrTDjvZzEXJ06YmOF0Em"

    val vuforia: VuforiaLocalizer by lazy {
        val parameters = VuforiaLocalizer.Parameters()

        parameters.vuforiaLicenseKey = VUFORIA_KEY
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK
        ClassFactory.getInstance().createVuforia(parameters)
    }
    val tfod: TFObjectDetector by lazy {
        val tfodMonitorViewId = opMode.hardwareMap.appContext.resources!!.getIdentifier(
                "tfodMonitorViewId", "id", opMode.hardwareMap.appContext.packageName)
        val tfodParameters = TFObjectDetector.Parameters(tfodMonitorViewId)
        ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia).apply {
            loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL)
        }
    }

    val telemetry = opMode.telemetry

    var goldLocation = CENTER

    var thread: Thread? = null

    fun start() {
        /** Activate Tensor Flow Object Detection.  */
        tfod.activate()
        var leftCertainty = 0
        var centerCertainty = 0
        var rightCertainty = 0
        val timer = ElapsedTime()
        try {
            while (timer.time() < 10.0) {
                opMode.checkForStop()
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                val updatedRecognitions = tfod.updatedRecognitions

                if (updatedRecognitions != null) {
                    telemetry.addData("# Obj", updatedRecognitions.size.toByte())
                    if (updatedRecognitions.none { it.label == LABEL_GOLD_MINERAL }) {
                        leftCertainty++
                    } else if (updatedRecognitions.size == 2) {
                        var goldMineralY = -1f
                        var silverMineralY = -1f

                        for (recognition in updatedRecognitions) {
                            if (recognition.label == LABEL_GOLD_MINERAL) {
                                goldMineralY = recognition.top
                            } else {
                                silverMineralY = recognition.top
                            }
                            if (goldMineralY < silverMineralY) {
                                rightCertainty++
                            } else {
                                centerCertainty++
                            }
                        }
                    } else if (updatedRecognitions[0].label == LABEL_GOLD_MINERAL) {
                        val goldRecognition = updatedRecognitions[0]
                        if (goldRecognition.top < goldRecognition.imageHeight / 2) {
                            rightCertainty++
                        } else {
                            centerCertainty++
                        }
                    }
                }
                if (leftCertainty > rightCertainty && leftCertainty > centerCertainty) {
                    telemetry.addData("GL", "Left")
                } else if (rightCertainty > centerCertainty) {
                    telemetry.addData("GL", "RIGHT")
                } else {
                    telemetry.addData("GL", "Center")
                }
                telemetry.update()
            }

            if (leftCertainty > rightCertainty && leftCertainty > centerCertainty) {
                goldLocation = LEFT
            } else if (rightCertainty > centerCertainty) {
                goldLocation = RIGHT
            } else {
                goldLocation = CENTER
            }
        } finally {
            tfod.shutdown()
        }
    }

}
//    thread!!.start()

//fun stop() {
//    stopped = true
//    thread = null
//    tfod.shutdown()
//}
