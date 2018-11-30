package org.firstinspires.ftc.robotcontroller.teamcode

import com.disnodeteam.dogecv.DogeCV
import com.disnodeteam.dogecv.detectors.DogeCVDetector
import com.disnodeteam.dogecv.filters.DogeCVColorFilter
import com.disnodeteam.dogecv.filters.HSVRangeFilter
import com.disnodeteam.dogecv.filters.LeviColorFilter
import com.disnodeteam.dogecv.scoring.MaxAreaScorer
import com.disnodeteam.dogecv.scoring.PerfectAreaScorer
import com.disnodeteam.dogecv.scoring.RatioScorer

import org.firstinspires.ftc.robotcore.external.Telemetry
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

import java.util.ArrayList

/**
 * Created by Victo on 9/10/2018.
 */

class OrderDetector : DogeCVDetector() {

    // Which area scoring method to use
    var areaScoringMethod: DogeCV.AreaScoringMethod = DogeCV.AreaScoringMethod.MAX_AREA

    //Create the scorers used for the detector
    var ratioScorer = RatioScorer(1.0, 5.0)
    var maxAreaScorer = MaxAreaScorer(0.01)
    var perfectAreaScorer = PerfectAreaScorer(5000.0, 0.05)

    //Create the filters used
    var yellowFilter: DogeCVColorFilter = LeviColorFilter(LeviColorFilter.ColorPreset.YELLOW, 100.0)
    var whiteFilter: DogeCVColorFilter = HSVRangeFilter(Scalar(0.0, 0.0, 200.0), Scalar(50.0, 40.0, 255.0))


    // Results for the detector
    /**
     * Returns the current gold pos
     *
     * @return current gold pos (UNKNOWN, LEFT, CENTER, RIGHT)
     */
    var currentOrder = GoldLocation.UNKNOWN
        private set
    /**
     * Returns the last known gold pos
     *
     * @return last known gold pos (UNKNOWN, LEFT, CENTER, RIGHT)
     */
    val lastOrder = GoldLocation.UNKNOWN
    /**
     * Is both elements found?
     *
     * @return if the elements are found
     */
    val isFound = false

    // Create the mats used
    private val workingMat = Mat()
    private val displayMat = Mat()
    private val yellowMask = Mat()
    private val whiteMask = Mat()
    private val hiarchy = Mat()

    private var leftCertainty = 0
    private var centerCertainty = 0
    private var rightCertainty = 0

    var telemetry: Telemetry? = null

    var started = false

    // Enum to describe gold location
    enum class GoldLocation {
        UNKNOWN,
        LEFT,
        CENTER,
        RIGHT
    }

    init {
        this.detectorName = "Sampling Order Detector"
    }

    override fun process(input: Mat): Mat {

        // Copy input mat to working/display mats
        input.copyTo(displayMat)
        input.copyTo(workingMat)
        input.release()

        // Generate Masks
        yellowFilter.process(workingMat.clone(), yellowMask)
        whiteFilter.process(workingMat.clone(), whiteMask)


        // Blur and find the countours in the masks
        val contoursYellow = ArrayList<MatOfPoint>()
        val contoursWhite = ArrayList<MatOfPoint>()

        Imgproc.blur(whiteMask, whiteMask, Size(2.0, 2.0))
        Imgproc.blur(yellowMask, yellowMask, Size(2.0, 2.0))

        Imgproc.findContours(yellowMask, contoursYellow, hiarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        Imgproc.drawContours(displayMat, contoursYellow, -1, Scalar(230.0, 70.0, 70.0), 2)

        Imgproc.findContours(whiteMask, contoursWhite, hiarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)
        Imgproc.drawContours(displayMat, contoursWhite, -1, Scalar(230.0, 70.0, 70.0), 2)


        // Prepare to find best yellow (gold) results
        var chosenYellowRect: Rect? = null
        var chosenYellowScore = Integer.MAX_VALUE.toDouble()

        val approxCurve = MatOfPoint2f()

        for (c in contoursYellow) {
            val contour2f = MatOfPoint2f(*c.toArray())

            //Processing on mMOP2f1 which is in type MatOfPoint2f
            val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)

            //Convert back to MatOfPoint
            val points = MatOfPoint(*approxCurve.toArray())

            // Get bounding rect of contour
            val rect = Imgproc.boundingRect(points)

            val diffrenceScore = calculateScore(points)

            if (diffrenceScore < chosenYellowScore && diffrenceScore < maxDifference) {
                chosenYellowScore = diffrenceScore
                chosenYellowRect = rect
            }

            val area = Imgproc.contourArea(c)
            val x = rect.x.toDouble()
            val y = rect.y.toDouble()
            val w = rect.width.toDouble()
            val h = rect.height.toDouble()
            val centerPoint = Point(x + w / 2, y + h / 2)
            if (area > 500) {
                Imgproc.circle(displayMat, centerPoint, 3, Scalar(0.0, 255.0, 255.0), 3)
                Imgproc.putText(displayMat, "Area: $area", centerPoint, 0, 0.5, Scalar(0.0, 255.0, 255.0))
            }
        }

        // Prepare to find best white (silver) results
        val chosenWhiteRect = arrayOfNulls<Rect?>(2)
        val chosenWhiteScore = arrayOf(java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE)

        for (c in contoursWhite) {
            val contour2f = MatOfPoint2f(*c.toArray())

            //Processing on mMOP2f1 which is in type MatOfPoint2f
            val approxDistance = Imgproc.arcLength(contour2f, true) * 0.02
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true)

            //Convert back to MatOfPoint
            val points = MatOfPoint(*approxCurve.toArray())

            // Get bounding rect of contour
            val rect = Imgproc.boundingRect(points)

            val differenceScore = calculateScore(points)

            val area = Imgproc.contourArea(c)
            val x = rect.x.toDouble()
            val y = rect.y.toDouble()
            val w = rect.width.toDouble()
            val h = rect.height.toDouble()
            val centerPoint = Point(x + w / 2, y + h / 2)
            if (area > 1000) {
                Imgproc.circle(displayMat, centerPoint, 3, Scalar(0.0, 255.0, 255.0), 3)
                Imgproc.putText(displayMat, "Area: $area", centerPoint, 0, 0.5, Scalar(0.0, 255.0, 255.0))
                Imgproc.putText(displayMat, "Diff: $differenceScore", Point(centerPoint.x, centerPoint.y + 20), 0, 0.5, Scalar(0.0, 255.0, 255.0))
            }

            val good = true
            if (differenceScore < maxDifference && area > 1000) {

                if (differenceScore < chosenWhiteScore[0]) {
                    chosenWhiteRect[0] = rect
                    chosenWhiteScore[0] = differenceScore
                } else if (differenceScore < chosenWhiteScore[1] && differenceScore > chosenWhiteScore[0]) {
                    chosenWhiteRect[1] = rect
                    chosenWhiteScore[1] = differenceScore
                }
            }
        }

        //Draw found gold element
        if (chosenYellowRect != null) {
            Imgproc.rectangle(displayMat,
                    Point(chosenYellowRect.x.toDouble(), chosenYellowRect.y.toDouble()),
                    Point((chosenYellowRect.x + chosenYellowRect.width).toDouble(), (chosenYellowRect.y + chosenYellowRect.height).toDouble()),
                    Scalar(255.0, 0.0, 0.0), 2)

            Imgproc.putText(displayMat,
                    "Gold: " + String.format("%.2f X=%.2f", chosenYellowScore, chosenYellowRect.x.toDouble()),
                    Point((chosenYellowRect.x - 5).toDouble(), (chosenYellowRect.y - 10).toDouble()),
                    Core.FONT_HERSHEY_PLAIN,
                    1.3,
                    Scalar(0.0, 255.0, 255.0),
                    2)

        }
        if (started) {
            if (chosenYellowRect != null) {
                telemetry += "Gold Pos: ${chosenYellowRect.x}"
                if (chosenYellowRect.x < workingMat.width() / 2) {
                    leftCertainty++
                } else {
                    centerCertainty++
                }
            } else {
                rightCertainty++
            }
        }

        if (chosenWhiteRect[0] != null) {
            telemetry += "White Pos 1: ${chosenWhiteRect[0]?.x}"
        }

        if (chosenWhiteRect[1] != null) {
            telemetry += "White Pos 2: ${chosenWhiteRect[1]?.x}"
        }

        telemetry += "Left: $leftCertainty"
        telemetry += "Center: $centerCertainty"
        telemetry += "Rigth: $rightCertainty"
        telemetry?.update()

        if (leftCertainty > centerCertainty && leftCertainty > rightCertainty) {
            currentOrder = GoldLocation.LEFT
        } else if (centerCertainty > rightCertainty) {
            currentOrder = GoldLocation.CENTER
        } else {
            currentOrder = GoldLocation.RIGHT
        }

        //Draw found white elements
        for (i in chosenWhiteRect.indices) {
            val rect = chosenWhiteRect[i]
            if (rect != null) {
                val score = chosenWhiteScore[i]
                Imgproc.rectangle(displayMat,
                        Point(rect.x.toDouble(), rect.y.toDouble()),
                        Point((rect.x + rect.width).toDouble(), (rect.y + rect.height).toDouble()),
                        Scalar(255.0, 255.0, 255.0), 2)
                Imgproc.putText(displayMat,
                        "Silver: " + String.format("Score %.2f ", score),
                        Point((rect.x - 5).toDouble(), (rect.y - 10).toDouble()),
                        Core.FONT_HERSHEY_PLAIN,
                        1.3,
                        Scalar(255.0, 255.0, 255.0),
                        2)
            }


        }


        // If enough elements are found, compute gold position
        //        if(chosenWhiteRect.get(0) != null && chosenWhiteRect.get(1) != null  && chosenYellowRect != null){
        //            int leftCount = 0;
        //            for(int i=0;i<chosenWhiteRect.size();i++){
        //                Rect rect = chosenWhiteRect.get(i);
        //                if(chosenYellowRect.x > rect.x){
        //                    leftCount++;
        //                }
        //            }
        //            if(leftCount == 0){
        //                currentOrder = OrderDetector.GoldLocation.LEFT;
        //            }
        //
        //            if(leftCount == 1){
        //                currentOrder = OrderDetector.GoldLocation.CENTER;
        //            }
        //
        //            if(leftCount >= 2){
        //                currentOrder = OrderDetector.GoldLocation.RIGHT;
        //            }
        //            isFound = true;
        //            lastOrder = currentOrder;
        //
        //        }else{
        //            currentOrder = OrderDetector.GoldLocation.UNKNOWN;
        //            isFound = false;
        //        }

        //Display Debug Information
        Imgproc.putText(displayMat, "Gold Position: " + currentOrder.toString(), Point(10.0, adjustedSize.height - 30), 0, 1.0, Scalar(255.0, 255.0, 0.0), 1)
        //        Imgproc.putText(displayMat,"Current Track: " + currentOrder.toString(),new Point(10,getAdjustedSize().height - 10),0,0.5, new Scalar(255,255,255),1);

        return displayMat
    }


    fun start() {
        leftCertainty = 0
        centerCertainty = 0
        rightCertainty = 0
        currentOrder = GoldLocation.RIGHT
        started = true
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
