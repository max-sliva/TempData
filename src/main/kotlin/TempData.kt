import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.event.EventHandler
import javafx.scene.Scene
import javafx.stage.Stage
import java.net.URL
import java.net.URLDecoder
import kotlin.system.exitProcess

class TempData: Application() {
    override fun start(primaryStage: Stage) {
//        val locale =  Locale("ru", "RU")
//        val bundle: ResourceBundle = ResourceBundle.getBundle("strings", locale)
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("mainWindow.fxml"), bundle)

//        fxmlLoader.setResources(ResourceBundle.getBundle("bundles.stringsForUI", locale));
//        val fxmlPath = "${getCurrentPath()}/mainWindow.fxml"
//        println("path = $fxmlPath")
//        val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
        val fxmlLoader = getLoader("tempDataWindow.fxml")
//        val fxmlLoader = FXMLLoader(this.javaClass.getResource("mainWindow.fxml")) //для запуска из IDE
        primaryStage.title = "TempData 0.5!"
        val scene = Scene(fxmlLoader.load(), 1200.0, 900.0)
        primaryStage.scene = scene

//        val scene = Scene(fxmlLoader.load(), 1200.0, 900.0)
//        primaryStage?.initStyle(StageStyle.UNDECORATED)
//        primaryStage?.setMaximized(true)

        primaryStage.show()
        primaryStage.onCloseRequest = EventHandler {
            Platform.exit()
            exitProcess(0)
        }
    }

    companion object { //специальный объект для запуска проекта в рамках фреймворка JavaFX
        @JvmStatic // его всегда оставляем одинаковым для всех проектов
        fun main(args: Array<String>) {
            launch(TempData::class.java) // Main – имя запускного класса
        }
    }
}

fun getCurrentPath(): String {
    val path: String = TempData::class.java.protectionDomain.codeSource.location.path
    val decodedPath = URLDecoder.decode(path, "UTF-8")
    val last = decodedPath.lastIndexOf("/")
    val newPath = decodedPath.subSequence(0, last)

    return newPath.toString()
}

fun getLoader(fxmlName: String): FXMLLoader {
    val fxmlPath = "${getCurrentPath()}/$fxmlName"
//    val fxmlLoader = FXMLLoader(URL("file:$fxmlPath")) //для jar-файла
    val fxmlLoader = FXMLLoader(TempData::class.java.getResource(fxmlName)) //для запуска из IDE
    return fxmlLoader
}