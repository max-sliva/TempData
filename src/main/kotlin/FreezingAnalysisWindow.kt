import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import javafx.stage.Stage
import java.net.URL
import java.util.*
import kotlin.collections.ArrayList

class FreezingAnalysisWindow: Initializable {
    @FXML
    lateinit var topHBox: HBox
    var freezingArray = ArrayList<FreezingData>()
    lateinit var freezingTable: TableView<Any>

    override fun initialize(location: URL?, resources: ResourceBundle?) {

    }

    fun setTitle(title: String) {
        val window = topHBox.scene.window as Stage
//        println("topHBox scene = ${topHBox.scene}")
//        println("freezingTable window = $window")
        window.title = title
    }

    fun setData(freezingDataArray: ArrayList<FreezingData>) {
        freezingArray = freezingDataArray
        freezingArray.forEach{
            println("<0: $it ")
        }
    }
}
