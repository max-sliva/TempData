import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import java.io.*
import java.nio.file.Paths
import java.util.*


class TempDataWindowControl {

    lateinit var timeCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var timeCol: TableColumn<List<StringProperty>, String>

    lateinit var dateCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var dateCol: TableColumn<List<StringProperty>, String>

    @FXML lateinit var table: TableView<Map<String, StringProperty>>
    @FXML
//    lateinit var table: TableView<List<StringProperty>>
    lateinit var mainPane: BorderPane
    lateinit var headers: Array<String>
    fun openCSVfile(actionEvent: ActionEvent) {
        println("open file")
        val fileChooser = FileChooser().apply {
            title = "Open Excel File"
            val currentPath: String = Paths.get(".").toAbsolutePath().normalize().toString()
            initialDirectory = File(currentPath)
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                FileChooser.ExtensionFilter("All Files", "*.*")
            )
        }
        val file = fileChooser.showOpenDialog(mainPane.scene.window)
        val records = readCSVfromFile2(file)
        headers[0] = "Date"
        headers = headers.plus(headers[headers.size - 1])
        for (i in headers.size - 1 downTo 2) {
            headers[i] = headers[i - 1]
        }
        headers[1] = "Time"
        headers.forEach {
            print("$it ")
        }
        println()
//        records.forEach {
//            it.forEach { it1 ->
//                print("$it1 ")
//            }
//            println()
//        }
        val dbObject = DBwork()
        dbObject.writeToDB(headers, records)
        //todo сделать в интерфейсе выпадающие списки с выбором года, месяца, числа
        //и потом показом нужных записей из БД
        println("size = ${records.size}")
    }

    fun readCSVfromFile2(file: File): ArrayList<List<String>> {
        val records: ArrayList<List<String>> = ArrayList()
        var i = 0
        var flag = false
        val br = BufferedReader(InputStreamReader(FileInputStream(file.name), "Cp1251"))
        br.readLines().forEach { line ->
//            println(line)
            if (i >= 2) {
                val values: Array<String> = line.split(';', ' ').dropLastWhile { it.isEmpty() }.toTypedArray()
                records.add(values.asList())
            }
            if (flag) i++
            if (line.contains("Доп. информация")) {
//                print("!!!!")
//                println(line)
                headers = line.split(';').dropLastWhile { it.isEmpty() }.toTypedArray()
                flag = true
                i++
            }
        }
        return records
    }

    fun showTable(actionEvent: ActionEvent) {

    }

    //    private fun generateTableViewRows(): ObservableList<ArrayList<String>> {
//        val max = 6
//        val tableViewRows: ObservableList<ArrayList<String>> = FXCollections.observableArrayList()
//        for (i in 1 until max) {
//            val dataRow = ArrayList<String>()
//            val value1 = "GREEN"
//            val value2 = "A$i"
//            val value3 = "B$i"
//            dataRow.add(value1)
//            dataRow.add(value2)
//            dataRow.add(value3)
//            tableViewRows.add(dataRow)
//        }
//        return tableViewRows
//    }
//    private fun buildCallbackColor(index: Int): Callback<TableColumn.CellDataFeatures<ArrayList<*>, Any?>, ObservableValue<Any?>?> {
//        return Callback<TableColumn.CellDataFeatures<ArrayList<*>, Any?>, ObservableValue<Any?>?> { param ->
//            SimpleObjectProperty(
//                param.value[index]
//            )
//        }
//    }
    fun addColumn(actionEvent: ActionEvent) {
        val col= TableColumn<Map<String, StringProperty>, String>("temp 1")
//        val col = TableColumn<List<StringProperty>, String>("temp 1")
        col.minWidth = 80.0

        table.columns.add(col)
//        dateCol.setCellValueFactory { data -> data.value[0] }
//        timeCol.setCellValueFactory { data -> data.value[1] }
//        col.setCellValueFactory { data -> data.value[2] }
        dateCol.setCellValueFactory { data ->
            data.value["date"]
        }
        timeCol.setCellValueFactory { data -> data.value["time"] }
        col.setCellValueFactory { data -> data.value["temp 1"] }

//        val data: ObservableList<List<StringProperty>> = FXCollections.observableArrayList()
        val data: ObservableList<Map<String, StringProperty>> = FXCollections.observableArrayList()
        fillData(data)
//        fillData1(data)
//        val firstRow = ArrayList<StringProperty>(3)
//        firstRow.add(0, SimpleStringProperty("31/12/2002"))
//        firstRow.add(1,  SimpleStringProperty("10:12"))
//        firstRow.add(2, SimpleStringProperty("100"))
//
//        val secondRow = ArrayList<StringProperty>(3)
//        secondRow.add(0, SimpleStringProperty("30/1/2012"))
//        secondRow.add(1, SimpleStringProperty("11:12"))
//        secondRow.add(2, SimpleStringProperty("101"))
//        data.addAll(firstRow, secondRow)
        table.items.addAll(data)
    }

    fun fillData(data: ObservableList<Map<String, StringProperty>>){
        data.addAll( mutableMapOf(Pair("date", SimpleStringProperty("10/1/2002")),
                               Pair("time", SimpleStringProperty("11:11")),
                               Pair("temp 1", SimpleStringProperty("3,11"))),
                     mutableMapOf(Pair("date", SimpleStringProperty("11/11/2021")),
                                Pair("time", SimpleStringProperty("12:10")),
                                Pair("temp 1", SimpleStringProperty("4,3"))),
                )
    }
    fun fillData1(data: ObservableList<List<StringProperty>>){
        val firstRow = ArrayList<StringProperty>(3)
        firstRow.add(0, SimpleStringProperty("31/12/2002"))
        firstRow.add(1,  SimpleStringProperty("10:12"))
        firstRow.add(2, SimpleStringProperty("100"))

        val secondRow = ArrayList<StringProperty>(3)
        secondRow.add(0, SimpleStringProperty("30/1/2012"))
        secondRow.add(1, SimpleStringProperty("11:12"))
        secondRow.add(2, SimpleStringProperty("102"))
        data.addAll(firstRow, secondRow)
    }

//    fun readCSVfromFile(file: File): ArrayList<List<String?>> {
//        var i = 0
//        var flag = false
//        val records: ArrayList<List<String?>> = ArrayList()
//        CSVReader(InputStreamReader(FileInputStream(file.name), "CP1251")).use { csvReader ->
//            var values: Array<String?>? = null
//            while (csvReader.readNext().also { values = it } != null) {
//                if (i >= 2) records.add(values!!.asList())
//                if (flag) i++
////                println(values)
//                values!!.forEach { it1 ->
////                    print(it1)
//                    if (it1!!.contains("Доп. информация")) {
//                        print("!!!!")
//                        flag = true
//                        i++
//                    }
//                }
////                println()
//            }
//        }
//        return records
//    }
}