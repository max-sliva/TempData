import javafx.concurrent.Task
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.ProgressIndicator
import javafx.scene.layout.BorderPane
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

fun createTask(function: () -> (Unit)): Task<Void> { //для запуска потока с функцией
    val dialog = Stage(StageStyle.UNDECORATED)
    dialog.title = "Work in progress"
    dialog.initModality(Modality.APPLICATION_MODAL)
    val bar = ProgressIndicator()
    val pane = BorderPane()
    pane.center = bar
    dialog.scene = Scene(pane, 350.0, 150.0)
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
    dialog.show().apply { }
    return task
}