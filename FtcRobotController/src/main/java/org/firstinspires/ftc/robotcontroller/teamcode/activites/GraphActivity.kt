package org.firstinspires.ftc.robotcontroller.teamcode.activites


import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.Series
import com.qualcomm.ftcrobotcontroller.R
import kotlinx.android.synthetic.main.activity_graph.*
import org.firstinspires.ftc.robotcontroller.teamcode.PID
import org.firstinspires.ftc.robotcontroller.teamcode.Vector
import org.firstinspires.ftc.robotcontroller.teamcode.activites.GraphActivity.Companion.seriesColors
import org.firstinspires.ftc.robotcontroller.teamcode.createSpinner
import kotlin.math.sin

class GraphActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val pid = PID("Test", 1.0, 0.0, 0.0)

        for (i in 0..100) {
            pid.getPower(sin(i.toDouble()))
        }

        pid.createGraphs()

        graphView.legendRenderer.isVisible = true

        val graphSpinner = createSpinner(this, graphs.toArray()) { item, pos ->
            graphs[pos].show(graphView)
        }
        spinnerLayout.addView(graphSpinner)
    }

    companion object {
        val graphs = ArrayList<Graph>()
        val seriesColors = arrayOf(
                Color.parseColor("#cc0000"),
                Color.parseColor("#339933"),
                Color.parseColor("#ff9900"),
                Color.parseColor("#0066ff"),
                Color.parseColor("#ffff00"),
                Color.parseColor("#993399")
        )
    }
}

class Graph(var name: String, vararg val graphSeries: LineGraphSeries<DataPoint>) {
    fun show(graphView: GraphView) {
        graphView.removeAllSeries()
        graphSeries.forEachIndexed { i, series ->
            graphView.addSeries(series)
        }
    }

    init {
        name += " #${GraphActivity.graphs.count()}"
        graphSeries.forEachIndexed { i, series ->
            series.color = seriesColors[i]
        }
    }

    override fun toString() = name
}

fun createSeries(points: Array<Vector>, name: String): LineGraphSeries<DataPoint> {
    val dataPoints = points.map { DataPoint(it.x, it.y) }.toTypedArray()
    return LineGraphSeries<DataPoint>(dataPoints).apply {
        title = name
    }
}

fun createSeries(points: ArrayList<Vector>, name: String) = createSeries(points.toTypedArray(), name)
