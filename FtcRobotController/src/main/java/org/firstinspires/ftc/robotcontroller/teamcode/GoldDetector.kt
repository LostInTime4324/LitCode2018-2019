package org.firstinspires.ftc.robotcontroller.teamcode

import com.disnodeteam.dogecv.DogeCV
import com.disnodeteam.dogecv.detectors.DogeCVDetector
import com.disnodeteam.dogecv.filters.DogeCVColorFilter
import com.disnodeteam.dogecv.filters.LeviColorFilter
import com.disnodeteam.dogecv.scoring.MaxAreaScorer
import com.disnodeteam.dogecv.scoring.PerfectAreaScorer
import com.disnodeteam.dogecv.scoring.RatioScorer
import org.firstinspires.ftc.robotcontroller.teamcode.GoldDetector.GoldLocation.*

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

import java.util.ArrayList

/**
 * Created by Victo on 9/10/2018.
 */

class GoldDetector : DogeCVDetector() {

    // Defining Mats to be used.
    private val displayMat = Mat() // Display debug info to the screen (this is what is returned)
    private val workingMat = Mat() // Used for preprocessing and working with (blurring as an example)
    private val maskYellow = Mat() // Yellow Mask returned by color filter
    private val hierarchy = Mat() // hierarchy used by coutnours

    // Results of the detector
    private var found = false // Is the gold mineral found
    private var screenPosition = Point() // Screen position of the mineral
    private var foundRect = Rect() // Found rect

    var areaScoringMethod: DogeCV.AreaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA // Setting to decide to use MaxAreaScorer or PerfectAreaScorer

    //Create the default filters and scorers
    var yellowFilter: DogeCVColorFilter = LeviColorFilter(LeviColorFilter.ColorPreset.YELLOW) //Default Yellow filter

    var ratioScorer = RatioScorer(1.0, 3.0)          // Used to find perfect squares
    var maxAreaScorer = MaxAreaScorer(0.01)                    // Used to find largest objects
    var perfectAreaScorer = PerfectAreaScorer(5000.0, 0.05) // Used to find objects near a tuned area value


    private var leftCertainty = 0
    private var centerCertainty = 0
    private var rightCertainty = 0

    var telemetry: Telemetry? = null

    var running = false

    var currentOrder = UNKNOWN

    var currentPosition = CENTER

    // Enum to describe gold location
    enum class GoldLocation {
        UNKNOWN,
        LEFT,
        CENTER,
        RIGHT
    }

    init {
        detectorName = "Sampling Order Detector"
    }

    override fun process(input: Mat): Mat {

        // Copy the input mat to our working mats, then release it for memory
        input.copyTo(displayMat)
        input.copyTo(workingMat)
        input.release()


        //Preprocess the working Mat (blur it then apply a yellow filter)
        Imgproc.GaussianBlur(workingMat, workingMat, Size(5.0, 5.0), 0.0)
        yellowFilter.process(workingMat.clone(), maskYellow)

        //Find contours of the yellow mask and draw them to the display mat for viewing

        val contoursYellow = ArrayList<MatOfPoint>()
        Imgproc.findContours(maskYellow, contoursYellow, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        Imgproc.drawContours(displayMat, contoursYellow, -1, Scalar(230.0, 70.0, 70.0), 2)

        // Current result
        var bestRect: Rect? = null
        var bestDiffrence = java.lang.Double.MAX_VALUE // MAX_VALUE since less diffrence = better

        // Loop through the contours and score them, searching for the best result
        for (cont in contoursYellow) {
            val score = calculateScore(cont) // Get the diffrence score using the scoring API

            // Get bounding rect of contour
            val rect = Imgproc.boundingRect(cont)
            Imgproc.rectangle(displayMat, rect.tl(), rect.br(), Scalar(0.0, 0.0, 255.0), 2) // Draw rect

            // If the result is better then the previously tracked one, set this rect as the new best
            if (score < bestDiffrence) {
                bestDiffrence = score
                bestRect = rect
            }
        }

        if (bestRect != null) {
            // Show chosen result
            Imgproc.rectangle(displayMat, bestRect.tl(), bestRect.br(), Scalar(255.0, 0.0, 0.0), 4)
            Imgproc.putText(displayMat, "Chosen", bestRect.tl(), 0, 1.0, Scalar(255.0, 255.0, 255.0))

            screenPosition = Point(bestRect.x.toDouble(), bestRect.y.toDouble())
            foundRect = bestRect
            found = true
        } else {
            found = false
        }
        if (running && found) {
            telemetry?.addData("Gold Pos", foundRect.x)
            val width = workingMat.width()
            val x = foundRect.x
            if (x > width / 3 && x < width * 2 / 3) {
                when (currentPosition) {
                    LEFT -> leftCertainty++
                    CENTER -> centerCertainty++
                    RIGHT -> rightCertainty++
                }
            }
        }

        telemetry?.addData("Left", leftCertainty)
        telemetry?.addData("Center", centerCertainty)
        telemetry?.addData("Right", rightCertainty)
        telemetry?.update()

        if (leftCertainty > centerCertainty && leftCertainty > rightCertainty) {
            currentOrder = LEFT
        } else if (centerCertainty > rightCertainty) {
            currentOrder = CENTER
        } else {
            currentOrder = RIGHT
        }

        //Display Debug Information
        Imgproc.putText(displayMat, "Gold Position: " + currentOrder.toString(), Point(10.0, adjustedSize.height - 30), 0, 1.0, Scalar(255.0, 255.0, 0.0), 1)
        //        Imgproc.putText(displayMat,"Current Track: " + currentOrder.toString(),new Point(10,getAdjustedSize().height - 10),0,0.5, new Scalar(255,255,255),1);

        return displayMat
    }

    override fun enable() {
        super.enable()
        reset()
        start()
    }

    fun pause() {
        running = false
    }

    fun start() {
        running = true
    }

    fun reset() {
        leftCertainty = 0
        centerCertainty = 0
        rightCertainty = 0
        currentOrder = CENTER
    }

    override fun useDefaults() {
        if (areaScoringMethod == DogeCV.AreaScoringMethod.MAX_AREA) {
            addScorer(maxAreaScorer)
        }

        if (areaScoringMethod == DogeCV.AreaScoringMethod.PERFECT_AREA) {
            addScorer(perfectAreaScorer)
        }
        addScorer(ratioScorer)
    }
}
