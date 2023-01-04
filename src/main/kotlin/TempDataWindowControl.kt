//import com.opencsv.CSVReader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import java.io.*
import java.nio.file.Paths
import java.util.*

class TempDataWindowControl {
    @FXML
    lateinit var mainPane: BorderPane

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
        records.forEach {
            it.forEach { it1 ->
                print("$it1 ")
            }
            println()
        }
        println("size = ${records.size}")
    }

    fun readCSVfromFile2(file: File): ArrayList<List<String>> {
        val records: ArrayList<List<String>> = ArrayList()
        var i = 0
        var flag = false
        val br = BufferedReader(InputStreamReader(FileInputStream(file.name), "Cp1251"))
        br.readLines().forEach {line ->
//            println(line)
            if (i >= 2) {
                val values: Array<String> = line.split(';').dropLastWhile { it.isEmpty() }.toTypedArray()
                records.add(values.asList())
            }
            if (flag) i++
            if (line.contains("Доп. информация")) {
//                print("!!!!")
                flag = true
                i++
            }
        }
        return records
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
//                    //todo делать запись в массив данных через 1 строку после "Доп. информация"
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