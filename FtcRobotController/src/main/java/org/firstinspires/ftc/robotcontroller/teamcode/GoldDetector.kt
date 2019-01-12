package org.firstinspires.ftc.robotcontroller.teamcode

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import org.firstinspires.ftc.robotcontroller.teamcode.GoldDetector.GoldLocation.*
import org.firstinspires.ftc.robotcore.external.ClassFactory

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector

/**
 * Created by Victo on 9/10/2018.
 */

class GoldDetector(val opMode: LinearOpMode) {

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

    enum class GoldLocation {
        LEFT,
        CENTER,
        RIGHT
    }

    var goldLocation = CENTER

    @Volatile var stopped = false

    var thread: Thread? = null

    fun start() {
        /** Activate Tensor Flow Object Detection.  */
        tfod.activate()
        stopped = true
        var leftCertainty = 0
        var centerCertainty = 0
        var rightCertainty = 0
        thread = Thread {
            while (opMode.opModeIsActive() && !stopped) {
                // getUpdatedRecognitions() will return null if no new information is available since
                // the last time that call was made.
                val updatedRecognitions = tfod.updatedRecognitions
                if (updatedRecognitions != null) {
                    telemetry.addData("# Obj", updatedRecognitions.size.toByte())
                    if (updatedRecognitions.size == 2) {
                        var goldMineralY = -1f
                        var silverMineral1Y = -1f
                        var silverMineral2Y = -1f
                        for (recognition in updatedRecognitions) {
                            if (recognition.label == LABEL_GOLD_MINERAL) {
                                goldMineralY = recognition.top
                            } else if(silverMineral1Y == -1f){
                                silverMineral1Y = recognition.top
                            } else {
                                silverMineral2Y = recognition.top
                            }
                        }
                        if (goldMineralY != -1f) {
                            telemetry.addData("GY", goldMineralY.toInt())
                        }
                        if (silverMineral1Y != -1f) {
                            telemetry.addData("S1Y", silverMineral1Y.toInt())
                        }
                        if (silverMineral2Y != -1f) {
                            telemetry.addData("S2Y", silverMineral2Y.toInt())
                        }
                        if (goldMineralY != -1f && silverMineral1Y != -1f && silverMineral2Y != -1f) {
                            if (goldMineralY > silverMineral1Y && goldMineralY < silverMineral2Y) {
                                centerCertainty++
                            } else if(goldMineralY > silverMineral1Y && goldMineralY > silverMineral2Y){
                                rightCertainty++
                            } else {
                                leftCertainty++
                            }
                        }
                    }
                    telemetry.update()
                }
            }

            if (leftCertainty > rightCertainty && leftCertainty > centerCertainty) {
                goldLocation = LEFT
            } else if (rightCertainty > centerCertainty) {
                goldLocation = RIGHT
            } else {
                goldLocation = CENTER
            }
        }
        thread!!.start()
    }
    fun stop() {
        stopped = true
        thread = null
        tfod.shutdown()
    }
}
