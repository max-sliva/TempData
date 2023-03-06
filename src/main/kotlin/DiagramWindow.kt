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
        createBarChart("Year",arrayOf("2007", "2008", "2009"),"Price")
        println("Show diagram")
        var bc : BarChart<String, Number>? = null //объект-диаграмма
        var series1 : XYChart.Series<String, Number>? = null //серия данных
        //т.е. некоторый набор однотипных данных
        var data1 : XYChart.Data<String, Number>? = null //данные для столбцов
        val years = arrayOf("2007", "2008", "2009") //массив строк с подписями оси ОХ
        val xAxis = CategoryAxis() //создаем ось ОХ
        val yAxis = NumberAxis() //создаем ось OY
//задаем формат подписей делений оси OY – со знаком $
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, "$", null)
        bc = BarChart(xAxis, yAxis) //создаем столбчатую диаграмму с осями xAxis и yAxis
        bc.title = "Advanced Bar Chart" // задаем название диаграммы
        xAxis.label = "Year" //задаем общую подпись оси ОХ
//задаем подписи категорий оси ОХ
        xAxis.categories = FXCollections.observableArrayList(listOf(*years))
        yAxis.label = "Price" //задаем общую подпись оси OY
        series1 = XYChart.Series() //создаем набор однотипных данных
        series1.name = "Data Series 1" //даем ему название и так еще 2 раза
        val series2 = XYChart.Series<String, Number>()
        series2.name = "Data Series 2"
        val series3 = XYChart.Series<String, Number>()
        series3.name = "Data Series 3"
// задаем данные
        data1 = XYChart.Data(years[0], 567) //для 2007 года данные со значением 567
        series1!!.data.add(data1) //добавляем данные в серию
        series1!!.data.add(XYChart.Data(years[1], 1292)) //остальные данные создаем и сразу добавляем
        series1!!.data.add(XYChart.Data(years[2], 2180))
        series2.data.add(XYChart.Data(years[0], 956))
        series2.data.add(XYChart.Data(years[1], 1665))
        series2.data.add(XYChart.Data(years[2], 2450))
        series3.data.add(XYChart.Data(years[0], 800))
        series3.data.add(XYChart.Data(years[1], 1000))
        series3.data.add(XYChart.Data(years[2], 2800))
        bc!!.data.add(series1) //добавляем созданные наборы в диаграмму
        bc!!.data.add(series2)
        bc!!.data.add(series3)
        if (paneForDiagram.isVisible) paneForDiagram.children.add(bc)

    }

    private fun createBarChart(xLabel: String, times: Array<String>, yLabel: String) {
        var bc : BarChart<String, Number>? = null //объект-диаграмма
//todo дописать метод для создания диаграммы по параметрам
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
