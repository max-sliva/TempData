import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.layout.HBox
import javafx.stage.Stage
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList
import javafx.event.ActionEvent
import javafx.scene.control.Button
import javafx.scene.control.RadioButton

class FreezingAnalysisWindow: Initializable {
    @FXML
    lateinit var diagramBtn: Button

    lateinit var hourlyRadioBtn: RadioButton
    lateinit var daylyRadioBtn: RadioButton
    lateinit var monthlyRadioBtn: RadioButton
    lateinit var allTableColumns: List<TableColumn<FreezingData, *>>
    lateinit var topHBox: HBox
    var freezingArray = ArrayList<FreezingData>()
    lateinit var freezingTable: TableView<FreezingData>
//    val colsArray = arrayOf("Date","Time","Depth","Temp")
    val colMap = mapOf(Pair("Date", "date"), Pair("Time","time"), Pair("Depth", "curMinusDepth"), Pair("Temp", "temperature")) //мап для названий колонок - полей класса FreezingData
    lateinit var data: ObservableList<FreezingData>
    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    fun setTitle(title: String) {
        val window = topHBox.scene.window as Stage
//        println("topHBox scene = ${topHBox.scene}")
//        println("freezingTable window = $window")
        window.title = title
    }

    fun setData(freezingDataArray: ArrayList<FreezingData>) {
        freezingTable.columns.clear()
        colMap.forEach{
            val col = TableColumn<FreezingData, String>(it.key)
            col.minWidth = 80.0
            col.cellValueFactory = PropertyValueFactory(it.value)
            freezingTable.columns.add(col)
        }
        allTableColumns = freezingTable.columns.toList()
        data = FXCollections.observableArrayList(freezingDataArray)
        freezingArray = freezingDataArray
        freezingArray.forEach{
            println("<0: $it ")
        }
        freezingTable.items = data
        //todo сделать анализ массива data на предмет наличия нескольких месяцев и дат
        //чтобы деактивировать ненужные радиокнопки
        diagramBtn.isDisable = true
    }
    //todo сделать переключение показа данных в таблице по часам, дням, месяцам
    fun hourlySelected(actionEvent: ActionEvent) {
        println("hourlySelected")
        data = FXCollections.observableArrayList(freezingArray)
        freezingTable.columns.clear()
        freezingTable.columns.addAll(allTableColumns)
        freezingTable.items = data
        diagramBtn.isDisable = true
    }

    fun daylySelected(actionEvent: ActionEvent) {
        println("daylySelected")
        var daylyData = ArrayList<FreezingData>()
//        var days = setOf<String>()
        var dayData = FreezingData()
        data.forEach{
            if (dayData.date==it.date){
                if (it.curMinusDepth>dayData.curMinusDepth) dayData = it
            } else {
                if (dayData.date!="") daylyData.add(dayData)
                dayData = it
            }
//            days = days.plus(it.date)
        }
        daylyData.add(dayData) //для последнего эл-та
        println("daylyData = $daylyData")
//        freezingTable.items.clear()
        freezingTable.items = FXCollections.observableArrayList(daylyData)
        val timeCol = freezingTable.columns.findLast { it.text=="Time" }
        freezingTable.columns.remove(timeCol)
//        freezingTable.columns.filter{
//            it.text=="Time"
//        }
        diagramBtn.isDisable = false
    }

    fun monthlySelected(actionEvent: ActionEvent) {
        println("monthlySelected")
        freezingTable.items.clear()
        var monthlyData = ArrayList<FreezingData>()
        var dayData =data.first()
        data.forEach{
            if (dayData.date.split(".")[1]==it.date.split(".")[1]){
                if (it.curMinusDepth>dayData.curMinusDepth) dayData = it
            } else {
                if (dayData.date!="") monthlyData.add(dayData)
                dayData = it
            }
            if (it.curMinusDepth>=dayData.curMinusDepth && it==data.last() && dayData.date.split(".")[1]==it.date.split(".")[1]) monthlyData.add(it)
            println(it)
        }
        freezingTable.items = FXCollections.observableArrayList(monthlyData)
        val timeCol = freezingTable.columns.findLast { it.text=="Time" }
        freezingTable.columns.remove(timeCol)
        diagramBtn.isDisable = false
    }

    fun showFreezingDiagram(actionEvent: ActionEvent) {
//todo сделать построение диаграммы с параметрами замерзания

    }
}
