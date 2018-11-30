package org.firstinspires.ftc.robotcontroller.teamcode.activites


import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.qualcomm.ftcrobotcontroller.R
import kotlinx.android.synthetic.main.activity_graph.*
import org.firstinspires.ftc.robotcontroller.teamcode.activites.GraphActivity.Companion.seriesColors
import org.firstinspires.ftc.robotcontroller.teamcode.createSpinner

class GraphActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

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
        fun addGraphs(vararg newGraphs: Graph) {
            graphs += newGraphs
        }
    }
}


class Graph(name: String, vararg val graphSeries: LineGraphSeries<DataPoint>) {
    constructor(name: String, points: ArrayList<DataPoint>) : this(name, Series(points, ""))

    fun show(graphView: GraphView) {
        graphView.removeAllSeries()
        graphView.legendRenderer.isVisible = graphSeries.count() > 1
        graphSeries.forEach { series ->
            graphView.addSeries(series)
        }
    }

    init {
        graphSeries.forEachIndexed { i, series ->
            series.color = seriesColors[i]

        }
    }

    val name = name + " #${GraphActivity.graphs.count()}"
    override fun toString() = name

}

fun Series(points: Array<DataPoint>, name: String): LineGraphSeries<DataPoint> {
    return LineGraphSeries<DataPoint>(points).apply {
        title = name
    }
}

fun Series(points: ArrayList<DataPoint>, name: String) = Series(points.toTypedArray(), name)
