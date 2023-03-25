import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.chart.XYChart.Series
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Ellipse
import javafx.scene.shape.Shape
import javafx.scene.text.Text
import javafx.stage.Stage
import kotlin.math.roundToInt

/**
 * Displays a bar with a single series whose bars are different colors depending upon the bar value.
 * A custom legend is created and displayed for the bar data.
 * Bars in the chart are customized to include a text label of the bar's data value above the bar.
 */
class BarChartWithLabel2 : Application() {
    override fun start(stage: Stage) {
        val xAxis = CategoryAxis()
        xAxis.label = "Bars"
        val yAxis = NumberAxis()
        yAxis.label = "Value"
        val bc = BarChart(xAxis, yAxis)
        bc.isLegendVisible = false
        val series1: Series<String?, Number?> = Series()
        for (i in 0..9) {
            val data: XYChart.Data<String?, Number?> = XYChart.Data("Value $i", i)
            data.nodeProperty().addListener { ov, oldNode, node ->
                if (node != null) {
//                    setNodeStyle(data)
                    displayLabelForData(data)
                }
            }
            series1.data.add(data)
        }
        bc.data.add(series1)
        val legend = LevelLegend()
        legend.alignment = Pos.CENTER
        val chartWithLegend = VBox()
        chartWithLegend.children.setAll(bc, legend)
        VBox.setVgrow(bc, Priority.ALWAYS)
       // chartWithLegend.stylesheets.add(javaClass.getResource("colored-chart.css").toExternalForm())
        stage.scene = Scene(chartWithLegend)
        stage.minHeight = 400.0
        stage.minWidth = 400.0
        stage.show()
    }

    /** Change color of bar if value of i is <5 then red, if >5 then green if i>8 then blue  */
    private fun setNodeStyle(data: XYChart.Data<String?, Number?>) {
//        val node = data.node
//        if (data.yValue!!.toInt() > 8) {
//            node.style = "-fx-bar-fill: -fx-exceeded;"
//        } else if (data.yValue!!.toInt() > 5) {
//            node.style = "-fx-bar-fill: -fx-achieved;"
//        } else {
//            node.style = "-fx-bar-fill: -fx-not-achieved;"
//        }
    }

    /** places a text label with a bar's value above a bar node for a given XYChart.Data  */
    private fun displayLabelForData(data: XYChart.Data<String?, Number?>) {
        val node = data.node
        val dataText = Text(data.yValue.toString() + "")
        node.parentProperty().addListener { ov, oldParent, parent ->
            val parentGroup = parent as Group
            parentGroup.children.add(dataText)
        }
        node.boundsInParentProperty().addListener { ov, oldBounds, bounds ->
            dataText.layoutX = (bounds!!.minX + bounds.width / 2 - dataText.prefWidth(-1.0) / 2).roundToInt().toDouble()
            dataText.layoutY = (bounds.minY - dataText.prefHeight(-1.0) * 0.5).roundToInt().toDouble()
        }

    }

    /** A simple custom legend for a three valued chart.  */
    internal inner class LevelLegend : GridPane() {
        init {
            hgap = 10.0
            vgap = 10.0
            addRow(0, createSymbol("-fx-exceeded"), Label("Exceeded"))
            addRow(1, createSymbol("-fx-achieved"), Label("Achieved"))
            addRow(2, createSymbol("-fx-not-achieved"), Label("Not Achieved"))
            styleClass.add("level-legend")
        }

        /** Create a custom symbol for a custom chart legend with the given fillStyle style string.  */
        private fun createSymbol(fillStyle: String): Node {
            val symbol: Shape = Ellipse(10.0, 5.0, 10.0, 5.0)
            symbol.style = "-fx-fill: $fillStyle"
            symbol.stroke = Color.BLACK
            symbol.strokeWidth = 2.0
            return symbol
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(BarChartWithLabel2::class.java)
        }
    }
}