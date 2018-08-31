package org.firstinspires.ftc.robotcontroller.teamcode

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import com.qualcomm.ftcrobotcontroller.R

class GraphActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        resetButton.setOnClickListener {
            graphView.removeAllSeries()
        }
        graphs.forEach { graph ->
            val button = Button(this)
            button.text = graph.name
            button.setOnClickListener {
                initGraph(graph)
            }
            graphButtonsLayout.addView(button)
        }
    }

    class Graph(val points: Array<Vector>, val name: String)

    fun initGraph(graph: Graph) {
        graphView.addSeries(
                LineGraphSeries<DataPoint>(graph.points.map {
                    DataPoint(it.x, it.y)
                }.toTypedArray()))
    }

    companion object {
        val graphs = ArrayList<Graph>()
    }
}
