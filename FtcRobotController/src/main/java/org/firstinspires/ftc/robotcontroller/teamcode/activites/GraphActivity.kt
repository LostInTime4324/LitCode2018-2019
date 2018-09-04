package org.firstinspires.ftc.robotcontroller.teamcode.activites


import android.app.Activity
import android.os.Bundle
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import com.jjoe64.graphview.series.PointsGraphSeries
import com.jjoe64.graphview.series.Series
import com.qualcomm.ftcrobotcontroller.R
import kotlinx.android.synthetic.main.activity_graph.*
import org.firstinspires.ftc.robotcontroller.teamcode.Vector
import org.firstinspires.ftc.robotcontroller.teamcode.createSpinner

class GraphActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        graphView.legendRenderer.isVisible = true
        val graphSpinner = createSpinner(this, graphs) { item, pos ->
            graphs[pos].show(graphView)
        }
        spinnerLayout.addView(graphSpinner)
    }

    companion object {
        val graphs = ArrayList<Graph>()
    }
}

class Graph(var name: String, vararg val graphSeries: GraphSeries) {
    fun show(graphView: GraphView) {
        graphView.removeAllSeries()
        graphSeries.forEach { graphView.addSeries(it.series) }
    }

    init {
        name += " #${GraphActivity.graphs.count()}"
    }

    override fun toString() = name
}

class GraphSeries(points: Array<Vector>, name: String, type: GraphType) {
    constructor(points: ArrayList<Vector>, name: String, type: GraphType) : this(points.toTypedArray(), name, type)

    val series: Series<DataPoint>

    init {
        val dataPoints = points.map { DataPoint(it.x, it.y) }.toTypedArray()
        series = when (type) {
            GraphType.LineGraph -> LineGraphSeries<DataPoint>(dataPoints)
            GraphType.PointGraph -> PointsGraphSeries<DataPoint>(dataPoints)
        }.apply {
            title = name
        }
    }
}

enum class GraphType {
    LineGraph,
    PointGraph
}