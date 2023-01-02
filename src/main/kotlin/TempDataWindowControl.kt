import com.opencsv.CSVReader
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import java.io.File
import java.io.FileInputStream
import java.io.FileReader
import java.io.InputStreamReader
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

class TempDataWindowControl {
    @FXML
    lateinit var mainPane: BorderPane

    fun openCSVfile(actionEvent: ActionEvent) {
        println("open file")
        val fileChooser = FileChooser().apply{
            title = "Open Excel File"
            val currentPath: String = Paths.get(".").toAbsolutePath().normalize().toString()
            initialDirectory = File(currentPath)
            extensionFilters.addAll(
                FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                FileChooser.ExtensionFilter("All Files", "*.*")
            )
        }
        val file = fileChooser.showOpenDialog(mainPane.scene.window)
        val records = readCSVfromFile(file)

    }

    fun readCSVfromFile(file: File): ArrayList<List<String?>> {
        val records: ArrayList<List<String?>> = ArrayList()
        CSVReader(InputStreamReader( FileInputStream(file.name), "CP1251")).use { csvReader ->
            var values: Array<String?>? = null
            while (csvReader.readNext().also { values = it } != null) {
                records.add(values!!.asList())
//                println(values)
                values!!.forEach {it1->
                    print(it1)
                    //todo делать запись в массив данных через 1 строку после "Доп. информация"
                    if (it1!!.contains("Доп. информация")) print("!!!!")
                }
                println()
            }
        }
        return records
    }
}