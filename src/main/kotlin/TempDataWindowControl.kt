import javafx.application.Platform
import javafx.beans.property.StringProperty
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.stage.FileChooser
import javafx.stage.Stage
import org.controlsfx.control.CheckComboBox
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*


class TempDataWindowControl : Initializable {

    lateinit var diagramBtn: Button
    lateinit var diagramWindowShow: MenuItem
    lateinit var textLabel: Label

    @FXML
    lateinit var topPane: HBox
    lateinit var serialCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var serialsList: ComboBox<Any>
//    lateinit var yearsList: ComboBox<Any>
//    lateinit var monthsList: ComboBox<Any>
    private var dataIsReady: Boolean = false
    lateinit var showBtn: Button
    lateinit var daysList: ComboBox<Any>
    var serialsList2 = CheckComboBox<String>()
    var yearsList2 = CheckComboBox<String>()
    var daysList2 = CheckComboBox<String>()
    var monthsList2 = CheckComboBox<String>()
    lateinit var timeCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var timeCol: TableColumn<List<StringProperty>, String>

    lateinit var dateCol: TableColumn<Map<String, StringProperty>, String>
//    lateinit var dateCol: TableColumn<List<StringProperty>, String>

    @FXML
    lateinit var table: TableView<Map<String, StringProperty>>

    @FXML

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
    lateinit var keys: List<String>
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
//            initData(dbObject)
//            initData2(dbObject)
            dbObject.clearHashMap()
            initData2(dbObject)
        }
        println("FileWork finished")
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

    fun showData(actionEvent: ActionEvent) {
        val task = createTask(::dataWork)
        Thread(task).start()
//        diagramWindowShow.isDisable = false
        diagramBtn.isDisable = false
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
            var data = getDataFromDB()

//        println("keys = ${data[0].keys}")
            println("serialNumber = $serialNumber")
            keys = data?.get(0)!!.keys.sorted()
            keys = keys.minusElement("Date")
            keys = keys.minusElement("Time")
            keys = keys.filter {//для показа только данных температуры (они начинаются с 1)
                it.startsWith('1')
            }
            println("keys = $keys")
            table.columns.addAll(dateCol, timeCol, serialCol)
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
//todo добавить различные фильтры по данным таблицы - по времени, темппературе и т.д.
    private fun getDataFromDB(): ObservableList<Map<String, StringProperty>>? {
        var data: ObservableList<Map<String, StringProperty>>? = null
        if (serialsList2.checkModel.checkedItems.size != 0) serialNumber = serialsList2.checkModel.checkedItems[0]
        else serialNumber = serialsList2.checkModel.getItem(1)
        if (yearsList2.checkModel.checkedItems.size != 0) year = yearsList2.checkModel.checkedItems[0]
        if (monthsList2.checkModel.checkedItems.size != 0) month = monthsList2.checkModel.checkedItems[0]
        if (!daysList2.isDisable && daysList2.checkModel.checkedItems.size != 0) {
            println("getData for days, serialNumber = ${serialNumber}, year = ${year}, month = $month")
            val checkedDays = daysList2.checkModel.checkedItems
            checkedDays.forEach {
                if (data == null) data = db.getRecordsForMonthYearAndDay(serialNumber, year, month, it)
                else data?.addAll(db.getRecordsForMonthYearAndDay(serialNumber, year, month, it))
            }
            val days = getStringFromArray(checkedDays)
            textLabel.text = "Serial = $serialNumber, year = ${year}, month = $month, day = $checkedDays "
        } else {
            if (!monthsList2.isDisable && monthsList2.checkModel.checkedItems.size != 0) {
                println("getData for months, serialNumber = ${serialNumber}, year = $year")
                val checkedMonths = monthsList2.checkModel.checkedItems
                checkedMonths.forEach {
                    if (data == null) data = db.getRecordsForMonthAndYear(serialNumber, year, it)
                    else data?.addAll(db.getRecordsForMonthAndYear(serialNumber, year, it))
                }
                val months = getStringFromArray(checkedMonths)
                textLabel.text = "Serial = $serialNumber, year = ${year}, month = $checkedMonths all days"

            } else {
                if (!yearsList2.isDisable && yearsList2.checkModel.checkedItems.size != 0) {
                    println("getData for years, serialNumber = ${serialNumber}")
                    val checkedYears = yearsList2.checkModel.checkedItems
                    checkedYears.forEach {
                        if (data == null) data = db.getRecordsForYearAndSerialNumber(it, serialNumber)
                        else data?.addAll(db.getRecordsForYearAndSerialNumber(it, serialNumber))
                    }
                    val years = getStringFromArray(checkedYears)
                    textLabel.text = "Serial = $serialNumber, year = $checkedYears all months all days"
                } else {
                    if (!serialsList2.isDisable && serialsList2.checkModel.checkedItems.size != 0) {
                        println("getData for serialNumber")
                        val checkedSerials = serialsList2.checkModel.checkedItems
                        checkedSerials.forEach {
                            if (data == null) data = db.getRecordsForSerialNumber(it)
                            else data?.addAll(db.getRecordsForSerialNumber(it))
                        }
//                        val serials = getStringFromArray(checkedSerials)
                        textLabel.text = "Serial = $$checkedSerials all years all months all days"
                    }
                    else {
                        data = db.getRecordsForSerialNumber(serialNumber)
                    }
                }
            }
        }
        return data
    }

    private fun getStringFromArray(checkedItems: ObservableList<String>?): Any {
        var str: String
        if (checkedItems?.size == 1) str = checkedItems[0]
        else str = "${checkedItems?.first()}-${checkedItems?.last()}"
        return str
    }

    override fun initialize(location: URL?, resources: ResourceBundle?) {
        topPane.children.remove(showBtn)
        println("Start!!")
        db = DBwork()

        if (db.dbSize() != 0L)
            initData2(db)
    }

    private fun initData2(db: DBwork) {
//    private fun initData2() {
//        val years = db.getYearsForSerialNumber(serialNumber)
//    db = DBwork()
//    if (db.dbSize() != 0L) {
//        showBtn.isDisable = true
        println("started initData2")
        Platform.runLater {
            serialsList2.isDisable = true
            topPane.children.remove(serialsList2)
            topPane.children.remove(yearsList2)
            topPane.children.remove(monthsList2)
            topPane.children.remove(daysList2)
            topPane.children.remove(showBtn)
            topPane.children.remove(diagramBtn)
            println("db size in initialize = ${db.dbSize()}")
            var checkComboBoxes = CheckComboBoxes(topPane, db)
            topPane.children.add(showBtn)
            topPane.children.add(diagramBtn)
            serialsList2 = checkComboBoxes.getSerials()
            yearsList2 = checkComboBoxes.getYears().apply { isDisable = true }
            monthsList2 = checkComboBoxes.getMonths().apply { isDisable = true }
            daysList2 = checkComboBoxes.getDays().apply { isDisable = true }
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
//            checkComboBoxes.db = db
        }
    }


    fun openDiagramWindow(actionEvent: ActionEvent) {
//        val fxmlPath = "${getCurrentPath()}/DiagramWindow.fxml"
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("ferreFrame.fxml")) //для запуска из IDE
//        val fxmlLoader = getLoader("DiagramWindow.fxml")
//        val stage = Stage() //создаем новое окно
//        stage.scene = Scene(fxmlLoader.load()) //загружаем в него данные
//        val diagramWindowClass = fxmlLoader.getController<DiagramWindow>()
//        stage.initModality(Modality.WINDOW_MODAL) //делаем окно модальным
//        stage.initOwner(mainPane.scene.window) //и его владельцем делаем главное окно
//        stage.show()
    }

    fun createDiagram(actionEvent: ActionEvent) {
        val fxmlLoader = getLoader("DiagramWindow.fxml")
        val stage = Stage() //создаем новое окно
        stage.scene = Scene(fxmlLoader.load()) //загружаем в него данные
        val diagramWindowClass = fxmlLoader.getController<DiagramWindow>()
//        stage.initModality(Modality.WINDOW_MODAL) //делаем окно модальным
        stage.initOwner(mainPane.scene.window) //и его владельцем делаем главное окно
        stage.show()
//        table.columns.forEach {
//            print("${it.text} ")
//        }
        println(keys)
//        var keys = table.columns.filter {
//
////            startsWith('1')
//        }
//        if (!daysList2.isDisable && daysList2.checkModel.checkedItems.size == 1) {
        val data = table.items
        var dates = setOf<String?>()  //множество для дат
        val xValues = arrayOf("0:01:00", "3:01:00", "6:01:00", "9:01:00", "12:01:00", "15:01:00", "18:01:00", "21:01:00")
//        val yValues: Map<String, Array<Double>> = mapOf(Pair("0:01:00", arrayOf(-4.19, 5.107, 5.68, 6.0)), Pair("3:01:00", arrayOf(3.18, -4.478, 5.428, 4.0)), Pair("6:01:00", arrayOf(2.485, 3.911, -5.05, 3.0)))
        var yValues = mutableMapOf<String, Array<Double>>()
        data.forEach {//цикл по данным таблицы
            val dateProp = it["Date"]?.value //дата
            dates=dates.plusElement(dateProp) //добавляем в мн-во
            var seriesArray = arrayOf<Double>()  //массив температур на разных глубинах
            keys.forEach {series-> //меняем запятую на точку
                seriesArray = seriesArray.plus(it[series]?.value!!.replace(',','.').toDouble())
            }
            val time = it["Time"]?.value.toString() //время
            yValues[time] = seriesArray //данные по температурам на разных глубинах для этого времени
        }
//        println("set of dates = $dates")
//        println("map of times = ")
//        yValues.forEach { (time, array) ->
//            println("$time: ${array.asList()}")
//        }
        if (dates.size==1) //если выбрана одна дата
            diagramWindowClass.showDiagram(dates.first().toString(), "Times", xValues, "Temperature", "°C", yValues, keys.toTypedArray())
        else {
            val months = getMonths(dates)
            if (months.size==1) {
                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, months.first().toInt());
                val monthDate = SimpleDateFormat("MMMM")
                val monthName = monthDate.format(cal.time)
                println("month name = $monthName")
                //todo сделать подсчет средних значений за день для каждой глубины и записывать их в массив, а потом в мап по дням
            }
        }
//todo сделать для нескольких дат, для месяца, для нескольких месяцев и т.д.

    }

    private fun getMonths(dates: Set<String?>): Set<String> {
        var months = setOf<String>()
//        print("months = ")
        dates.forEach{
            val month = it?.split(".")!![1]
            months = months.plus(month)
        }
        return months
    }

    /*
    fun fillData(data: ObservableList<Map<String, StringProperty>>) {
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

    fun fillData1(data: ObservableList<List<StringProperty>>) {
    val firstRow = ArrayList<StringProperty>(3)
    firstRow.add(0, SimpleStringProperty("31/12/2002"))
    firstRow.add(1, SimpleStringProperty("10:12"))
    firstRow.add(2, SimpleStringProperty("100"))

    val secondRow = ArrayList<StringProperty>(3)
    secondRow.add(0, SimpleStringProperty("30/1/2012"))
    secondRow.add(1, SimpleStringProperty("11:12"))
    secondRow.add(2, SimpleStringProperty("102"))
    data.addAll(firstRow, secondRow)
    }
    */

}