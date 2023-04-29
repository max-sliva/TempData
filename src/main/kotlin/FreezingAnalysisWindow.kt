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
import javafx.scene.control.RadioButton

class FreezingAnalysisWindow: Initializable {
    @FXML
    lateinit var hourlyRadioBtn: RadioButton
    lateinit var daylyRadioBtn: RadioButton
    lateinit var monthlyRadioBtn: RadioButton

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
        data = FXCollections.observableArrayList(freezingDataArray)
        freezingArray = freezingDataArray
        freezingArray.forEach{
            println("<0: $it ")
        }
        freezingTable.items = data
        //todo сделать анализ массива data на предмет наличия нескольких месяцев и дат
        //чтобы деактивировать ненужные радиокнопки

    }
    //todo сделать переключение показа данных в таблице по часам, дням, месяцам
    fun hourlySelected(actionEvent: ActionEvent) {

    }

    fun daylySelected(actionEvent: ActionEvent) {

    }

    fun monthlySelected(actionEvent: ActionEvent) {

    }
}
