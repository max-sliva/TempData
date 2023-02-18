import javafx.application.Platform
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import org.controlsfx.control.CheckComboBox
import java.io.*
import java.net.URL
import java.nio.file.Paths
import java.util.*


class TempDataWindowControl : Initializable {

    @FXML
    lateinit var topPane: HBox
    lateinit var serialCol: TableColumn<Map<String, StringProperty>, String>
    lateinit var serialsList: ComboBox<Any>
    lateinit var serialsList2: CheckComboBox<String>
    private var dataIsReady: Boolean = false
    lateinit var showBtn: Button
    lateinit var daysList: ComboBox<Any>
    lateinit var daysList2: CheckComboBox<String>
    lateinit var monthsList: ComboBox<Any>
    lateinit var monthsList2: CheckComboBox<String>
    lateinit var timeCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var timeCol: TableColumn<List<StringProperty>, String>

    lateinit var dateCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var dateCol: TableColumn<List<StringProperty>, String>

    @FXML
    lateinit var table: TableView<Map<String, StringProperty>>

    @FXML
    lateinit var yearsList: ComboBox<Any>
    lateinit var yearsList2: CheckComboBox<String>

    //    lateinit var table: TableView<List<StringProperty>>
    lateinit var mainPane: BorderPane
    lateinit var headers: Array<String>
    lateinit var db: DBwork
    var serialNumber = "0"
    var oldSerialNumber = "0"
    var year = "0"
    var oldYear = "0"
    var month = "0"
    var oldMonth = "0"
    var day = "0"
    var oldDay = "0"
    var fileChooser = FileChooser()

    fun fileWork(file: File) {
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
            initData2(dbObject)
        }
        println("FileWork finished")
    }

//    fun createTask(function: () -> (Unit)): Task<Void> { //для запуска потока с функцией
//        val dialog = Stage(StageStyle.UNDECORATED)
//        dialog.title = "Work in progress"
//        dialog.initModality(Modality.APPLICATION_MODAL)
//        val bar = ProgressIndicator()
//        val pane = BorderPane()
//        pane.center = bar
//        dialog.scene = Scene(pane, 350.0, 150.0)
//        val task: Task<Void> = object : Task<Void>() {
//            @Throws(Exception::class)
//            override fun call(): Void? {
////                val file = fileChooser.showOpenDialog(mainPane.scene.window)
//                println("progress = ${bar.progress}")
//                function()
//                println("progress = All")
//                return null
//            }
//        }
//        // bar.progressProperty().bind(task.progressProperty())
//        task.onSucceeded = EventHandler {
//            println("Done!")
//            pane.bottom = Label("Done opening file an preparing data. Please, close the window")
//            dialog.close()
//        }
//        dialog.show().apply { }
//        return task
//    }

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
                serialNumber = line.split(';')[1].trim('\\')
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

//    fun showTable(actionEvent: ActionEvent) {}

    fun showData(actionEvent: ActionEvent) {
        val task = createTask(::dataWork)
        Thread(task).start()
    }

    fun dataWork() { //для показа данных из БД
        Platform.runLater {
            println("Started dataWork")
            table.items.clear()
            table.columns.clear()
            dateCol.setCellValueFactory { data -> data.value["Date"] }
            timeCol.setCellValueFactory { data -> data.value["Time"] }
            serialCol.setCellValueFactory { data -> data.value["SerialNumber"] }
//        val data = db.getRecordsForYear(year)
            var data: ObservableList<Map<String, StringProperty>>?
            //todo сделать выбор из БД записей с несколькими значениями по дням, месяцам, годам и номерам датчиков
            data = getDataFromDB()

////        println("keys = ${data[0].keys}")
//            println("serialNumber = $serialNumber")
//            var keys = data[0].keys.sorted()
//            keys = keys.minusElement("Date")
//            keys = keys.minusElement("Time")
//            keys = keys.filter {//для показа только данных температуры (они начинаются с 1)
//                it.startsWith('1')
//            }
//            println("keys = $keys")
//            table.columns.addAll(dateCol, timeCol, serialCol)
//            keys.forEach {
//                val col = TableColumn<Map<String, StringProperty>, String>(it)
//                col.minWidth = 80.0
//                table.columns.add(col)
//                col.setCellValueFactory { data -> data.value[it] }
//            }
//            table.items.addAll(data)
//            println("data size = ${data.size}")
        }
    }

    private fun getDataFromDB(): ObservableList<Map<String, StringProperty>>? {
        var data: ObservableList<Map<String, StringProperty>>? = null
//            if (daysList2.checkModel.checkedItems.size!=0) data = db.getRecordsForMonthYearAndDay(
//                    serialNumber,
//                    year,
//                    monthsList.value.toString(),
//                    daysList.value.toString()
//                )
        if (serialsList2.checkModel.checkedItems.size == 0) {
            println("Showing all data from db")
            data = db.getRecordsForSerialNumber(serialNumber)
        } else {
            if (serialsList2.checkModel.checkedItems.size >1){
                println("Showing for several serials")
                val checkedSerials = serialsList2.checkModel.checkedItems
                data = db.getRecordsForSerialNumbers(checkedSerials)
            } else {
                println("One serial")
            }
        }
//            if (daysList.value != null && day != "0") data =
//                db.getRecordsForMonthYearAndDay(
//                    serialNumber,
//                    year,
//                    monthsList.value.toString(),
//                    daysList.value.toString()
//                )
//            else if (monthsList.value != null && month != "0") data = db.getRecordsForMonthAndYear(
//                serialNumber,
//                year,
//                monthsList.value.toString()
//            )
//            else {
//                data =
//                    if (yearsList.value != null && year != "0") db.getRecordsForYearAndSerialNumber(year, serialNumber)
//                    else db.getRecordsForSerialNumber(serialNumber)
//            }
        return data
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        topPane.children.remove(serialsList)
        topPane.children.remove(yearsList)
        topPane.children.remove(monthsList)
        topPane.children.remove(daysList)
        topPane.children.remove(showBtn)
        println("Start!!")
        db = DBwork()
        println("db size = ${db.dbSize()}")
        var checkComboBoxes = CheckComboBoxes(topPane, db)
        topPane.children.add(showBtn)
        serialsList2 = checkComboBoxes.getSerials()
        yearsList2 = checkComboBoxes.getYears()
        monthsList2 = checkComboBoxes.getMonths()
        daysList2 = checkComboBoxes.getDays()

        initData(db)
        initData2(db)

    }

    private fun initData2(db: DBwork) {
//        val years = db.getYearsForSerialNumber(serialNumber)
        if (db.dbSize() == 0L) showBtn.isDisable = true
        val serialNumbers = db.getSerialNumbers()
        serialsList2.items.clear()
        serialsList2.items.add("0")
        serialsList2.items.addAll(serialNumbers)

        yearsList2.items.clear()
        yearsList2.items.add("0")
//        yearsList2.items.addAll(years)
        monthsList2.items.clear()
        monthsList2.items.add("0")
        daysList2.items.clear()

//        val strings = FXCollections.observableArrayList<String>()
//        for (i in 0..100) {
//            strings.add("Item $i")
//        }
//        val checkComboBox = CheckComboBox(strings)
//        topPane.children.addAll(serialsList2, yearsList2, monthsList2, daysList2)
    }

    private fun initData(db: DBwork) {
        val years = db.getYearsForSerialNumber(serialNumber)
        if (db.dbSize() == 0L) showBtn.isDisable = true
        val serialNumbers = db.getSerialNumbers()
        serialsList.items.clear()
        serialsList.items.add("0")
        serialsList.items.addAll(serialNumbers)

        yearsList.items.clear()
        yearsList.items.add("0")
        yearsList.items.addAll(years)
        monthsList.items.clear()
        monthsList.items.add("0")
        daysList.items.clear()
    }

    //ф-ии внизу можно будет потом уброть вместе с обычными комбобоксами
    fun yearSelect(actionEvent: ActionEvent) {
        year = yearsList2.checkModel.checkedItems[0]
        oldYear = year
//        year = yearsList?.selectionModel?.selectedItem.toString()
        println("year = $year")
        val months = db.getMonthsForYear(year, serialNumber).sorted()
        println("months = $months")
        monthsList2.items.clear()
        monthsList2.items.add("0")
        monthsList2.items.addAll(months)

        if (year == "0") year = "0".also {
            monthsList.selectionModel.clearSelection()
            monthsList.items.clear()
            monthsList.items.add("0")
            monthsList.selectionModel.select(0)
        } else {
            val months = db.getMonthsForYear(year, serialNumber).sorted()
            println("months = $months")
            monthsList.items.clear()
            monthsList.items.add("0")
            monthsList.items.addAll(months)
        }
//        db.showRecordsForYear(year, table)
    }

    fun monthSelect(actionEvent: ActionEvent) {
        month = monthsList2.checkModel.checkedItems[0]
        oldMonth = month
//        month = monthsList?.value.toString()
        val days = db.getDaysForMonth(month, year, serialNumber).sorted()
        println("days for month = $days")
        daysList2.items.clear()
        daysList2.items.add(" ")
        daysList2.items.addAll(days)

        println("month = $month")
        if (month == "0") month = "0".also {
            daysList.selectionModel.clearSelection()
            daysList.items.clear()
            daysList.items.add(" ")
            daysList.selectionModel.select(0)
        }
        else {
            val days = db.getDaysForMonth(month, year, serialNumber).sorted()
            println("days for month = $days")
            daysList.items.clear()
            daysList.items.add(" ")
            daysList.items.addAll(days)
        }
    }

    fun daySelect(actionEvent: ActionEvent) {
        day = daysList?.value.toString()
        if (day == "0") day = "0"
        println("day = $day")
    }

    fun serialNumberSelect(actionEvent: ActionEvent) {
        println("in serialNumberSelect")
        serialNumber = serialsList2.checkModel.checkedItems[0]
        oldSerialNumber = serialNumber
        println("serialNumber2 = $serialNumber")
        val years = db.getYearsForSerialNumber(serialNumber).sorted()
        println("years = $years")
        yearsList2.items.clear()
        yearsList2.items.add("0")
        yearsList2.items.addAll(years)
//        serialNumber = serialsList?.selectionModel?.selectedItem.toString()
        println("serialNumber = $serialNumber")
        if (serialNumber == "0") serialNumber = "0".also {
            yearsList.selectionModel.clearSelection()
            yearsList.items.clear()
            yearsList.items.add(" ")
            yearsList.selectionModel.select(0)
        } else {
            val years = db.getYearsForSerialNumber(serialNumber).sorted()
            println("years = $years")
            yearsList.items.clear()
            yearsList.items.add("0")
            yearsList.items.addAll(years)
        }
    }

//    fun fillData(data: ObservableList<Map<String, StringProperty>>) {
//        data.addAll(
//            mutableMapOf(
//                Pair("date", SimpleStringProperty("10/1/2002")),
//                Pair("time", SimpleStringProperty("11:11")),
//                Pair("temp 1", SimpleStringProperty("3,11"))
//            ),
//            mutableMapOf(
//                Pair("date", SimpleStringProperty("11/11/2021")),
//                Pair("time", SimpleStringProperty("12:10")),
//                Pair("temp 1", SimpleStringProperty("4,3"))
//            ),
//        )
//    }
//
//    fun fillData1(data: ObservableList<List<StringProperty>>) {
//        val firstRow = ArrayList<StringProperty>(3)
//        firstRow.add(0, SimpleStringProperty("31/12/2002"))
//        firstRow.add(1, SimpleStringProperty("10:12"))
//        firstRow.add(2, SimpleStringProperty("100"))
//
//        val secondRow = ArrayList<StringProperty>(3)
//        secondRow.add(0, SimpleStringProperty("30/1/2012"))
//        secondRow.add(1, SimpleStringProperty("11:12"))
//        secondRow.add(2, SimpleStringProperty("102"))
//        data.addAll(firstRow, secondRow)
//    }

}