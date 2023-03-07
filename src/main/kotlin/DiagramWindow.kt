import javafx.collections.FXCollections
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.layout.VBox
import org.controlsfx.control.CheckComboBox
import java.net.URL
import java.util.*
import javafx.event.ActionEvent
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.NumberAxis
import javafx.scene.chart.XYChart
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import kotlin.collections.ArrayList

class DiagramWindow: Initializable {
    @FXML
    lateinit var tabPane: TabPane
    lateinit var tabForDiagram: AnchorPane
    lateinit var paneForDiagram: Pane
    lateinit var showDiagramBtn: Button

    lateinit var boxForCheckCombos: VBox
    lateinit var db: DBwork
    lateinit var serialsList2: CheckComboBox<String>
    lateinit var yearsList2: CheckComboBox<String>
    lateinit var daysList2: CheckComboBox<String>
    lateinit var monthsList2: CheckComboBox<String>

    //todo create class for diagram making - average for day, month, year, several days, months, years,
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("Diagram window")
        db = DBwork()
        boxForCheckCombos.children.remove(showDiagramBtn)
        var checkComboBoxes = CheckComboBoxes(boxForCheckCombos, db)
        val serialNumbers = db.getSerialNumbers()
        serialsList2 = checkComboBoxes.getSerials()
        yearsList2 = checkComboBoxes.getYears().apply { isDisable = true }
        monthsList2 = checkComboBoxes.getMonths().apply { isDisable = true }
        daysList2 = checkComboBoxes.getDays().apply { isDisable = true }

        serialsList2.items.clear()
        serialsList2.items.add("0")
        serialsList2.items.addAll(serialNumbers)

        yearsList2.items.clear()
        yearsList2.items.add("0")
//        yearsList2.items.addAll(years)
        monthsList2.items.clear()
        monthsList2.items.add("0")
        daysList2.items.clear()
        boxForCheckCombos.children.add(showDiagramBtn)
    }

    fun checkBoxPaneAddAll(
        serialsList2: CheckComboBox<String>,
        yearsList2: CheckComboBox<String>,
        monthsList2: CheckComboBox<String>,
        daysList2: CheckComboBox<String>
    ) {
        boxForCheckCombos.children.addAll(serialsList2, yearsList2, monthsList2, daysList2)
        println("added")
    }

    fun showDiagram(actionEvent: ActionEvent) {
        //"0:01:00", "3:01:00", "6:01:00", "9:01:00", "12:01:00", "15:01:00", "18:01:00", "21:01:00"
        val title = "17.10.2019"
        val xLabel =  "Times"
        val xValues =  arrayOf("0:01:00", "3:01:00", "6:01:00", "9:01:00", "12:01:00", "15:01:00", "18:01:00", "21:01:00")
        val yLabel =  "Temperature"
//        val yValues =  arrayOf(4.19, 5.107, 5.68, 3.18, 4.478, 5.428, 2.485, 3.911, 5.05)
        val yValues =  mapOf(Pair("0:01:00", arrayOf(4.19, 5.107, 5.68)), Pair("3:01:00", arrayOf(3.18, 4.478, 5.428)), Pair("3:01:00", arrayOf(2.485, 3.911, 5.05)))

        val dataSeries = arrayOf("1000", "1001", "1002")
        createBarChartForDay(title, xLabel,xValues,yLabel, yValues, dataSeries)

        println("Show diagram")
        /*
        { var bc : BarChart<String, Number>? = null //объект-диаграмма
        var series1 : XYChart.Series<String, Number>? = null //серия данных
        //т.е. некоторый набор однотипных данных
        var data1 : XYChart.Data<String, Number>? = null //данные для столбцов
        val times = arrayOf("0:01:00", "3:01:00", "6:01:00", "9:01:00", "12:01:00", "15:01:00", "18:01:00", "21:01:00") //массив строк с подписями оси ОХ
        val xAxis = CategoryAxis() //создаем ось ОХ
        val yAxis = NumberAxis() //создаем ось OY
        //задаем формат подписей делений оси OY – со знаком $
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, null, "°C")
        bc = BarChart(xAxis, yAxis) //создаем столбчатую диаграмму с осями xAxis и yAxis
        bc.title = "17.10.2019" // задаем название диаграммы
        xAxis.label = "Times" //задаем общую подпись оси ОХ
        //задаем подписи категорий оси ОХ
        xAxis.categories = FXCollections.observableArrayList(listOf(*times))
        yAxis.label = "Temperature" //задаем общую подпись оси OY
        series1 = XYChart.Series() //создаем набор однотипных данных
        series1.name = "1000" //даем ему название и так еще 2 раза
        val series2 = XYChart.Series<String, Number>()
        series2.name = "1001"
        val series3 = XYChart.Series<String, Number>()
        series3.name = "1002"
        // задаем данные
        data1 = XYChart.Data(times[0], 4.19) //для 2007 года данные со значением 567
        series1!!.data.add(data1) //добавляем данные в серию
        series1!!.data.add(XYChart.Data(times[1], 5.107)) //остальные данные создаем и сразу добавляем
        series1!!.data.add(XYChart.Data(times[2], 5.68))
        series2.data.add(XYChart.Data(times[0],   3.18))
        series2.data.add(XYChart.Data(times[1],   4.478))
        series2.data.add(XYChart.Data(times[2],   5.428))
        series3.data.add(XYChart.Data(times[0],   2.485))
        series3.data.add(XYChart.Data(times[1],   3.911))
        series3.data.add(XYChart.Data(times[2],   5.05))
        bc!!.data.add(series1) //добавляем созданные наборы в диаграмму
        bc!!.data.add(series2)
        bc!!.data.add(series3)
        if (paneForDiagram.isVisible) paneForDiagram.children.add(bc)}
        */

    }

    private fun createBarChartForDay(title: String, xLabel: String, xValues: Array<String>, yLabel: String, yValues: Map<String, Array<Double>>, dataSeries: Array<String>) {
        var bc : BarChart<String, Number>? = null //объект-диаграмма

        var series1 : XYChart.Series<String, Number>? = null //серия данных
        //т.е. некоторый набор однотипных данных
        var data1 : XYChart.Data<String, Number>? = null //данные для столбцов
       // val times = arrayOf("0:01:00", "3:01:00", "6:01:00", "9:01:00", "12:01:00", "15:01:00", "18:01:00", "21:01:00") //массив строк с подписями оси ОХ
        val xAxis = CategoryAxis() //создаем ось ОХ
        val yAxis = NumberAxis() //создаем ось OY
//задаем формат подписей делений оси OY – со знаком $
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, null, "°C")
        bc = BarChart(xAxis, yAxis) //создаем столбчатую диаграмму с осями xAxis и yAxis
        bc.title = title // задаем название диаграммы
        xAxis.label = xLabel //задаем общую подпись оси ОХ
//задаем подписи категорий оси ОХ
        xAxis.categories = FXCollections.observableArrayList(listOf(*xValues))
        yAxis.label = yLabel //задаем общую подпись оси OY
        val series = ArrayList<XYChart.Series<String, Number>>()
        dataSeries.forEach {
            val seriesPart = XYChart.Series<String, Number>().apply { name = it}
            series.add(seriesPart)
        }
//        series1 = XYChart.Series() //создаем набор однотипных данных
//        series1.name = "1000" //даем ему название и так еще 2 раза
//        val series2 = XYChart.Series<String, Number>()
//        series2.name = "1001"
//        val series3 = XYChart.Series<String, Number>()
//        series3.name = "1002"
// задаем данные
//todo разобраться с добавлением данных в серии, почему пропускаются данные для 3:01:00
        xValues.forEach {
            val yArray = yValues[it]
            var i = 0
            yArray?.forEach { it2 ->
                series[i].data.add(XYChart.Data(it, it2))
                i++
            }
        }
//        series.forEach {
//            it.data.add(XYChart.Data(xValues[i], yValues[i]))
//            i++
//        }
//        data1 = XYChart.Data(xValues[0], 4.19) //для 2007 года данные со значением 567
//        series1!!.data.add(data1) //добавляем данные в серию
//        series1!!.data.add(XYChart.Data(xValues[1], 5.107)) //остальные данные создаем и сразу добавляем
//        series1!!.data.add(XYChart.Data(xValues[2], 5.68))
//        series2.data.add(XYChart.Data(xValues[0],   3.18))
//        series2.data.add(XYChart.Data(xValues[1],   4.478))
//        series2.data.add(XYChart.Data(xValues[2],   5.428))
//        series3.data.add(XYChart.Data(xValues[0],   2.485))
//        series3.data.add(XYChart.Data(xValues[1],   3.911))
//        series3.data.add(XYChart.Data(xValues[2],   5.05))
        bc!!.data.addAll(series) //добавляем созданные наборы в диаграмму
//        bc!!.data.add(series2)
//        bc!!.data.add(series3)
        if (paneForDiagram.isVisible) paneForDiagram.children.add(bc)
    }

    fun allInOneClick(actionEvent: ActionEvent) {
        paneForDiagram.isVisible = true
        tabPane.isVisible = false
    }

    fun inTabsClick(actionEvent: ActionEvent) {
        paneForDiagram.isVisible = false
        tabPane.isVisible = true
    }
}
