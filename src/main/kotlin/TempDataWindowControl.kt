import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.layout.BorderPane
import javafx.stage.FileChooser
import java.io.File
import java.nio.file.Paths

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
    }
}