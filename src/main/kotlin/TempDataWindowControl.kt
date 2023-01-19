import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.concurrent.Task
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import javafx.stage.Modality
import javafx.stage.Stage
import java.io.*
import java.net.URL
import java.nio.file.Paths
import java.util.*


class TempDataWindowControl : Initializable{

    private var dataIsReady: Boolean = false
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
    var day = "0"
    lateinit var serialNumber: String
    var fileChooser = FileChooser()

    fun fileWork(file: File){
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
        println()
        val dbObject = DBwork()
        dbObject.writeToDB(serialNumber, headers, records)
        println("size = ${records.size}")
        if (records.size > 0) {
            showBtn.isDisable = false
            initData(dbObject)
        }
    }

    fun createTask(function: () -> (Unit)): Task<Void> { //для запуска потока с функцией
        val dialog = Stage()
        dialog.title = "Work in progress"
        dialog.initModality(Modality.APPLICATION_MODAL)
        val bar = ProgressIndicator()
        val pane = BorderPane()
        pane.center = bar
        dialog.scene = Scene(pane, 250.0, 150.0)
        val task: Task<Void> = object : Task<Void>() {
            @Throws(Exception::class)
            override fun call(): Void? {
//                val file = fileChooser.showOpenDialog(mainPane.scene.window)
                println("progress = ${bar.progress}")
                function()
                println("progress = All")
                return null
            }
        }
        // bar.progressProperty().bind(task.progressProperty())
        task.onSucceeded = EventHandler {
            println("Done!")
            pane.bottom = Label("Done opening file an preparing data. Please, close the window")
            dialog.close()
        }
        dialog.show()
        return task
    }

    fun openCSVfile(actionEvent: ActionEvent) {
        dataIsReady = false
        println("open file")
        fileChooser = FileChooser().apply {
            title = "Open Excel File"
            val currentPath: String = Paths.get(".").toAbsolutePath().normalize().toString()
            initialDirectory = File(currentPath)
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                FileChooser.ExtensionFilter("All Files", "*.*")
            )
        }
        val file = fileChooser.showOpenDialog(mainPane.scene.window)
        val task = createTask { (::fileWork)(file) }
        Thread(task).start()
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
            if (line.contains("Серийный номер")) {
                serialNumber = line.split(';')[1]
                println("serialNumber = $serialNumber")
            }

            if (line.contains("Доп. информация")) {
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
        val task = createTask(::dataWork)
        Thread(task).start()
    }

    fun dataWork(){ //для показа данных из БД
        Platform.runLater {
            println("Started dataWork")
            table.items.clear()
            table.columns.clear()
            dateCol.setCellValueFactory { data -> data.value["Date"] }
            timeCol.setCellValueFactory { data -> data.value["Time"] }
//        val data = db.getRecordsForYear(year)
            var data: ObservableList<Map<String, StringProperty>>
            if (daysList.value != null && day != "0") data =
                db.getRecordsForMonthYearAndDay(year, monthsList.value.toString(), daysList.value.toString())
            else data = if (monthsList.value != null && month != "0") db.getRecordsForMonthAndYear(
                year,
                monthsList.value.toString()
            )
            else db.getRecordsForYear(year)
//        println("keys = ${data[0].keys}")
            var keys = data[0].keys.sorted()
            keys = keys.minusElement("Date")
            keys = keys.minusElement("Time")
            keys = keys.filter {//для показа только данных температуры (они начинаются с 1)
                it.startsWith('1')
            }
            println("keys = $keys")
            table.columns.addAll(dateCol, timeCol)
            keys.forEach {
                val col = TableColumn<Map<String, StringProperty>, String>(it)
                col.minWidth = 80.0
                table.columns.add(col)
                col.setCellValueFactory { data -> data.value[it] }
            }
            table.items.addAll(data)
            println("data size = ${data.size}")
        }
    }

    fun fillData(data: ObservableList<Map<String, StringProperty>>){
        data.addAll(
            mutableMapOf(
                Pair("date", SimpleStringProperty("10/1/2002")),
                Pair("time", SimpleStringProperty("11:11")),
                Pair("temp 1", SimpleStringProperty("3,11"))
            ),
            mutableMapOf(
                Pair("date", SimpleStringProperty("11/11/2021")),
                Pair("time", SimpleStringProperty("12:10")),
                Pair("temp 1", SimpleStringProperty("4,3"))
            ),
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
        //todo сделать множественный выбор в комбобоксах
    }

    private fun initData(db: DBwork) {
        val years = db.getYears()
        if (db.dbSize() == 0L) showBtn.isDisable = true
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
        println("month = $month")
        if (month == " ") month = "0".also {
            daysList.selectionModel.clearSelection()
            daysList.items.clear()
            daysList.items.add(" ")
            daysList.selectionModel.select(0)
        }
        else {
            val days = db.getDaysForMonth(month, year).sorted()
            println("days for month = $days")
            daysList.items.clear()
            daysList.items.add(" ")
            daysList.items.addAll(days)
        }
    }

    fun daySelect(actionEvent: ActionEvent) {
        day = daysList?.value.toString()
        if (day == " ") day = "0"
        println("day = $day")
    }

}