import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import java.io.*
import java.net.URL
import java.nio.file.Paths
import java.util.*


class TempDataWindowControl : Initializable{

    @FXML lateinit var showBtn: Button
    lateinit var daysList: ComboBox<Any>
    lateinit var monthsList: ComboBox<Any>
    lateinit var timeCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var timeCol: TableColumn<List<StringProperty>, String>

    lateinit var dateCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var dateCol: TableColumn<List<StringProperty>, String>

    @FXML lateinit var table: TableView<Map<String, StringProperty>>
    @FXML lateinit var yearsList: ComboBox<Any>
//    lateinit var table: TableView<List<StringProperty>>
    lateinit var mainPane: BorderPane
    lateinit var headers: Array<String>
    lateinit var db: DBwork
    var year = "0"
    var month = "0"
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
//        headers = headers.filterNot {
//            it.startsWith('5')
//        }.toTypedArray()
        headers.forEach {
            print("$it ")
        }
//        headers.forEach {
//            if (it.startsWith('5')) headers.filterNot { it1->
//                it.startsWith('5')
//            }
//        }
        println()
//        records.forEach {
//            it.forEach { it1 ->
//                print("$it1 ")
//            }
//            println()
//        }
        val dbObject = DBwork()
        dbObject.writeToDB(headers, records)
        println("size = ${records.size}")
        if (records.size > 0) {
            showBtn.isDisable = false
            initData(dbObject)
        }
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
                //todo добавить получение значения поля Серийный номер
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
    fun showData(actionEvent: ActionEvent) {
        table.items.clear()
        table.columns.clear()
        dateCol.setCellValueFactory { data -> data.value["Date"] }
        timeCol.setCellValueFactory { data -> data.value["Time"] }
//        val data = db.getRecordsForYear(year)
        //todo добавить фильтр по дню месяца
        var data = if (monthsList.value!=null && month!="0") db.getRecordsForMonthAndYear(year, monthsList.value.toString())
                    else db.getRecordsForYear(year)
//        println("keys = ${data[0].keys}")
        var keys = data[0].keys.sorted()
        keys = keys.minusElement("Date")
        keys = keys.minusElement("Time")
        keys = keys.filter {
            it.startsWith('1')
        }
        println("keys = $keys")
        table.columns.addAll(dateCol, timeCol)
        keys.forEach {
            val col= TableColumn<Map<String, StringProperty>, String>(it)
            col.minWidth = 80.0
            table.columns.add(col)
            col.setCellValueFactory { data -> data.value[it] }
        }
        table.items.addAll(data)
        println("data size = ${data.size}")
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

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("Start!!")
        db = DBwork()
        println("db size = ${db.dbSize()}")
        initData(db)
    }

    private fun initData(db: DBwork) {
        val years = db.getYears()
        if (db.dbSize() == 0) showBtn.isDisable = true
        yearsList.items.clear()
        yearsList.items.add(" ")
        yearsList.items.addAll(years)
        monthsList.items.clear()
        monthsList.items.add(" ")
        daysList.items.clear()
    }


    fun yearSelect(actionEvent: ActionEvent) {
        year = yearsList?.selectionModel?.selectedItem.toString()
        println("year = $year")
        if (year == " ") year = "0".also {
            monthsList.selectionModel.clearSelection()
            monthsList.items.clear()
            monthsList.items.add(" ")
            monthsList.selectionModel.select(0)
        } else {
            val months = db.getMonthsForYear(year).sorted()
            println("months = $months")
            monthsList.items.clear()
            monthsList.items.add(" ")
            monthsList.items.addAll(months)
        }
//        db.showRecordsForYear(year, table)
    }

    fun monthSelect(actionEvent: ActionEvent) {
        month = monthsList?.value.toString()
        if (month == " ") month = "0"
        println("month = $month")
        val days = db.getDaysForMonth(month, year).sorted()
        println("days for month = $days")
        daysList.items.clear()
        daysList.items.add(" ")
        daysList.items.addAll(days)
    }

}