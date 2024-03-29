import javafx.application.Platform
import javafx.collections.ListChangeListener
import javafx.event.ActionEvent
import javafx.scene.control.ComboBox
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import org.controlsfx.control.CheckComboBox

class CheckComboBoxes(topPane: Pane, db: DBwork) {
    var serialsList2: CheckComboBox<String>
    var yearsList2: CheckComboBox<String>
    var monthsList2: CheckComboBox<String>
    var daysList2: CheckComboBox<String>
    var db: DBwork
//    var db = DBwork()
//        set(value) {field = value}

    var serialNumber = "0"
    var oldSerialNumber = "0"
    var year = "0"
    var oldYear = "0"
    var month = "0"
    var oldMonth = "0"
    var day = "0"
    var oldDay = "0"

    init {
        this.db = db
        println("in CheckComboBoxes")
        daysList2 = CheckComboBox<String>().apply { title = "Days" }
        daysList2.checkModel.checkedItems.addListener(ListChangeListener<String?> { c -> clearList(c, daysList2) })
        daysList2.addEventHandler(ComboBox.ON_HIDDEN) { event ->
            println("${(event.source as CheckComboBox<String>).title} is now hidden.")
        }
        println("after daysList2")
        monthsList2 = CheckComboBox<String>().apply { title = "Months" }
        monthsList2.checkModel.checkedItems.addListener(ListChangeListener<String?> { c -> clearList(c, monthsList2) })
        monthsList2.addEventHandler(ComboBox.ON_HIDDEN) { event ->
            println("${(event.source as CheckComboBox<String>).title} is now hidden.")
            if (monthsList2.checkModel.checkedItems.size == 1 && oldMonth != monthsList2.checkModel.checkedItems[0]) {
                println("One item")
                val task = createTask { (::monthSelect)(ActionEvent()) }
                Thread(task).start()
                daysList2.isDisable = false
//                serialNumberSelect(ActionEvent())
            } else if (monthsList2.checkModel.checkedItems.size > 1) {
                println("Many items")
                daysList2.isDisable = true
            }
        }
        println("after monthsList2")
        yearsList2 = CheckComboBox<String>().apply { title = "Years" }
        yearsList2.checkModel.checkedItems.addListener(ListChangeListener<String?> { c -> clearList(c, yearsList2) })
        yearsList2.addEventHandler(ComboBox.ON_HIDDEN) { event ->
            println("year item[0] = ${yearsList2.checkModel.checkedItems[0]}")
            println("${(event.source as CheckComboBox<String>).title} is now hidden.")
            if (yearsList2.checkModel.checkedItems.size == 1 && oldYear != yearsList2.checkModel.checkedItems[0]) {
                println("One item")
                val task = createTask { (::yearSelect)(ActionEvent()) }
                Thread(task).start()
                monthsList2.isDisable = false
//                daysList2.isDisable = false
//                serialNumberSelect(ActionEvent())
            } else if (yearsList2.checkModel.checkedItems.size > 1) {
                println("Many items")
                monthsList2.isDisable = true
                daysList2.isDisable = true
            }
            if (yearsList2.checkModel.checkedItems[0] == "0") {
                println("Many with space")
            }
        }
        yearsList2.addEventHandler(ComboBox.ON_SHOWN) { event ->
            println("Year is shown, list size = ${yearsList2.checkModel.checkedItems.size}")
            if (yearsList2.checkModel.checkedItems.size == 1){
                println("years selected = 1")
            }

        }
        println("after yearsList2")
        serialsList2 = CheckComboBox<String>().apply { title = "Serials" }
        serialsList2.checkModel.checkedItems.addListener(ListChangeListener<String?> { c -> clearList(c, serialsList2)})
        serialsList2.addEventHandler(ComboBox.ON_HIDDEN) { event ->
            println("${(event.source as CheckComboBox<String>).title} is now hidden.")
            if (serialsList2.checkModel.checkedItems.size == 1 && oldSerialNumber != serialsList2.checkModel.checkedItems[0]) {
                println("One item")
                //todo разобраться с глюком бесконечного прогресс-бара, исчезающего названия комбобоксов, если такой глюк еще будет
                val task = createTask { (::serialNumberSelect)(ActionEvent()) }
                Thread(task).start()
                yearsList2.isDisable = false  //активируем другие списки
//                monthsList2.isDisable = false
//                daysList2.isDisable = false
//                serialNumberSelect(ActionEvent())
            } //esle if ()

            else if (serialsList2.checkModel.checkedItems.size > 1) {
                println("Many items")
                yearsList2.isDisable = true  //деактивируем другие списки, чтобы в них ничего нельзя было выбрать
                monthsList2.isDisable = true
                daysList2.isDisable = true
            }
        }
        println("after serialsList2")
        topPane.children.addAll(serialsList2, yearsList2, monthsList2, daysList2)
        println("end init in CheckComboBoxes")
    }

    private fun clearList(c: ListChangeListener.Change<out String>?, list: CheckComboBox<String>) {
        if (c!!.list.contains("0")) {
            println("Clear, item[0] = ${list.checkModel.checkedItems[0]}")
//            list.checkModel.checkedItems.clear()
            Platform.runLater {
                list.checkModel.clearCheck(0)
                list.checkModel.clearChecks()
                if (list == serialsList2) {
                    oldSerialNumber = "0"
                    yearsList2.items.clear()
                    yearsList2.items.add("0")
                    monthsList2.items.clear()
                    monthsList2.items.add("0")
                    daysList2.items.clear()
                    daysList2.items.add("0")
                }
                if (list == yearsList2) {
                    monthsList2.items.clear()
                    monthsList2.items.add("0")
                    daysList2.items.clear()
                    daysList2.items.add("0")
                }
                if (list == monthsList2) {
                    daysList2.items.clear()
                    daysList2.items.add("0")
                }
            }
        }
    }


    fun serialNumberSelect(actionEvent: ActionEvent) {
        println("in serialNumberSelect")
        serialNumber = serialsList2.checkModel.checkedItems[0]
        oldSerialNumber = serialNumber
        println("serialNumber2 = $serialNumber")
        val years = db.getYearsForSerialNumber(serialNumber).sorted()
        println("years = $years")
        Platform.runLater {
            yearsList2.items.clear()
            yearsList2.items.add("0")
            yearsList2.items.addAll(years)
        }
//        serialNumber = serialsList?.selectionModel?.selectedItem.toString()
        println("serialNumber = $serialNumber")
//        if (serialNumber == "0") serialNumber = "0".also {
//            yearsList.selectionModel.clearSelection()
//            yearsList.items.clear()
//            yearsList.items.add(" ")
//            yearsList.selectionModel.select(0)
//        } else {
//            val years = db.getYearsForSerialNumber(serialNumber).sorted()
//            println("years = $years")
//            yearsList.items.clear()
//            yearsList.items.add("0")
//            yearsList.items.addAll(years)
//        }
    }

    fun yearSelect(actionEvent: ActionEvent) {
        year = yearsList2.checkModel.checkedItems[0]
        oldYear = year
//        year = yearsList?.selectionModel?.selectedItem.toString()
        println("year = $year")
        val months = db.getMonthsForYear(year, serialNumber).sorted()
        println("months = $months")
        Platform.runLater {
            monthsList2.items.clear()
            monthsList2.items.add("0")
            monthsList2.items.addAll(months)
        }

//        if (year == "0") year = "0".also {
//            monthsList.selectionModel.clearSelection()
//            monthsList.items.clear()
//            monthsList.items.add("0")
//            monthsList.selectionModel.select(0)
//        } else {
//            val months = db.getMonthsForYear(year, serialNumber).sorted()
//            println("months = $months")
//            monthsList.items.clear()
//            monthsList.items.add("0")
//            monthsList.items.addAll(months)
//        }
//        db.showRecordsForYear(year, table)
    }

    fun monthSelect(actionEvent: ActionEvent) {
        month = monthsList2.checkModel.checkedItems[0]
        oldMonth = month
//        month = monthsList?.value.toString()
        val days = db.getDaysForMonth(month, year, serialNumber).sorted()
        println("days for month = $days")
        Platform.runLater {
            daysList2.items.clear()
            daysList2.items.add("0")
            daysList2.items.addAll(days)
        }

//        println("month = $month")
//        if (month == "0") month = "0".also {
//            daysList.selectionModel.clearSelection()
//            daysList.items.clear()
//            daysList.items.add(" ")
//            daysList.selectionModel.select(0)
//        }
//        else {
//            val days = db.getDaysForMonth(month, year, serialNumber).sorted()
//            println("days for month = $days")
//            daysList.items.clear()
//            daysList.items.add(" ")
//            daysList.items.addAll(days)
//        }
    }

    fun getSerials() =  serialsList2
    fun getYears() = yearsList2
    fun getMonths() = monthsList2
    fun getDays() = daysList2

//    fun daySelect(actionEvent: ActionEvent) {
//        day = daysList?.value.toString()
//        if (day == "0") day = "0"
//        println("day = $day")
//    }
}