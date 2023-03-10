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
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import kotlin.collections.ArrayList

class DiagramWindow: Initializable {
    @FXML
    lateinit var tabPane: TabPane
    lateinit var tabForDiagram: AnchorPane
    lateinit var paneForDiagram: HBox
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
        val title = "17.10.2019"
        val xLabel =  "Times"
        val xValues =  arrayOf("0:01:00", "3:01:00", "6:01:00", "9:01:00", "12:01:00", "15:01:00", "18:01:00", "21:01:00")
        val yLabel =  "Temperature"
        val yValues =  mapOf(Pair("0:01:00", arrayOf(-4.19, 5.107, 5.68)), Pair("3:01:00", arrayOf(3.18, -4.478, 5.428)), Pair("6:01:00", arrayOf(2.485, 3.911, -5.05)))
        val dataSeries = arrayOf("1000", "1001", "1002")
        if (paneForDiagram.isVisible) {
            val bc = createBarChartForDay(title, xLabel, xValues, yLabel, yValues, dataSeries)
            paneForDiagram.children.add(bc)
        }
        if (tabPane.isVisible){
            dataSeries.forEach {
                val tab = Tab(it)
                tabPane.tabs.add(tab)
        //todo поправить работу ф-ии для одного значения
                tab.contentProperty().set(createBarChartForDay(it, xLabel, xValues, yLabel, yValues, arrayOf(it)))
            }
        }
        println("Show diagram")
    }

    private fun createBarChartForDay(title: String, xLabel: String, xValues: Array<String>, yLabel: String, yValues: Map<String, Array<Double>>, dataSeries: Array<String>): BarChart<String, Number> {
//        var bc : BarChart<String, Number>? = null //объект-диаграмма
        val xAxis = CategoryAxis() //создаем ось ОХ
        val yAxis = NumberAxis() //создаем ось OY
//задаем формат подписей делений оси OY – со знаком °C
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, null, "°C")
        var bc = BarChart<String, Number>(xAxis, yAxis) //создаем столбчатую диаграмму с осями xAxis и yAxis
        bc.title = if (!title.contains(".")) ((title.toInt() - 1000.0) / 10).toString()+" м" else title// задаем название диаграммы
        xAxis.label = xLabel //задаем общую подпись оси ОХ
        xAxis.categories = FXCollections.observableArrayList(listOf(*xValues))//задаем подписи категорий оси ОХ
        yAxis.label = yLabel //задаем общую подпись оси OY
        val series = ArrayList<XYChart.Series<String, Number>>() //массив серий данных
        dataSeries.forEach {//цикл по названиям серий данных
            val seriesPart = XYChart.Series<String, Number>().apply { name = it} //создаем серию с нужным названием
            series.add(seriesPart) //добавляем в массив
        }
        // задаем данные
        xValues.forEach {//цикл по значения оси Х - по временам снятия показаний
            val yArray = yValues[it] //берем массив значений данного времени
            if (series.size>1) { //для нескольких серий данных
                var i = 0
                yArray?.forEach { it2 -> //цикл по массиву значений
                    series[i++].data.add(XYChart.Data(it, it2)) //добавляем в соответствующую серию нужные данные
                }
            } else { //для одной серии данных
                try {
//                    if (yArray!![title.toInt() - 1000] != null) {
                        print("$it = ")
                        println("${yArray!![title.toInt() - 1000]}")
                        series[0].data.add(XYChart.Data(it, yArray[title.toInt() - 1000]))
//                    }
                } catch (e:NullPointerException) { //если данных меньше чем значений в xValues
                    return@forEach //выходим из forEach
                }
            }
        }
        bc.data.addAll(series) //добавляем созданные наборы в диаграмму
        bc.minWidth = 0.0
        bc.prefWidth = 2000.0 //чтобы диаграмма менялась с изменением окна
//        bc.maxHeightProperty().bind(paneForDiagram.maxHeightProperty())
        return bc
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
