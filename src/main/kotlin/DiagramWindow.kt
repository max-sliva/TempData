import javafx.application.Platform
import javafx.beans.binding.ObjectExpression
import javafx.collections.FXCollections
import javafx.embed.swing.SwingFXUtils
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.Initializable
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.SnapshotParameters
import javafx.scene.chart.*
import javafx.scene.control.*
import javafx.scene.image.WritableImage
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.text.Text
import javafx.stage.Stage
import java.awt.Desktop
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO
import kotlin.math.roundToInt


data class ChartParams(val title: String,
                       val xLabel: String,
                       val xValues: Array<String?>,
                       val yLabel: String,
                       val ySuffix: String,
                       val yValues: Map<String?, Array<Double>>,
                       val dataSeries: Array<String>)
data class SeriesXY(val x: String, val y: Double) //класс для значений серий данных для изменения диаграммы
class DiagramWindow : Initializable {

    @FXML
    lateinit var zoomSlider: Slider
    lateinit var checkBoxesForSeriesPane: HBox
    lateinit var chartsComboBox: ComboBox<Any>
    lateinit var colorPicker: ColorPicker
    lateinit var opacitySlider: Slider
    lateinit var saveToImageButton: Button
    lateinit var tabPane: TabPane
    lateinit var tabForDiagram: AnchorPane
    lateinit var paneForDiagram: HBox
    lateinit var db: DBwork
    lateinit var seriesNames: Array<String>
    lateinit var chartParams: ChartParams
    var divValue = 10 //значение, на которое делим значение глубины, чтобы получить нормальное в метрах
    val initColor = "008080" //начальный цвет для диаграмм в табах
    var mapOfDeletedSeries = mapOf<String, Array<SeriesXY>>()
//    var mapOfDeletedSeriesX = mapOf<String, Array<String>>()
//    lateinit var bc: XYChart<String, Number>
    override fun initialize(location: URL?, resources: ResourceBundle?) {
        println("Diagram window")
        db = DBwork()
        opacitySlider.valueProperty().addListener { _, oldVal, newVal -> //прозрачность окна
            (paneForDiagram.scene.window as Stage).opacity = 1 - newVal.toInt() / 100.0
        }
        zoomSlider.valueProperty().addListener { _, oldVal, newVal ->  //увеличение диаграммы
            if (paneForDiagram.isVisible){  //todo подобрать параметры зума
                if (oldVal.toDouble() < newVal.toDouble()) {
//                    paneForDiagram.scaleX = paneForDiagram.scaleX + newVal.toDouble()/10
                    paneForDiagram.scaleX+=0.1
//                    paneForDiagram.scaleY = paneForDiagram.scaleY + newVal.toDouble()/10
                    paneForDiagram.scaleY+=0.1
                } else{
//                    paneForDiagram.scaleX = paneForDiagram.scaleX - newVal.toDouble()/10
//                    paneForDiagram.scaleY = paneForDiagram.scaleY - newVal.toDouble()/10
                    paneForDiagram.scaleX-=0.1
                    paneForDiagram.scaleY-=0.1

                }

            }

        }
        chartsComboBox.items.addAll("BarChart", "LineChart")
        chartsComboBox.selectionModel.select(1)
        val curColor = Color.valueOf("0x${initColor}")
        println("color = $curColor")
        colorPicker.value = curColor
    }

    /**
     * Вызывает createBarChartForDay для создания диаграмм по нужным параметрам
     * @param title Заголовок диаграммы
     * @param xLabel Подпись для оси ОХ
     * @param xValues Значения шкалы по оси ОХ
     * @param yLabel Подпись для оси ОУ
     * @param yValues Значения шкалы по оси ОУ
     * @param arraySeriesNames Названия серий данных
     * @param chartType Тип диаграммы
     */
    fun showDiagram(
        title: String = "17.10.2019",
        xLabel: String = "Times",
        xValues: Array<String?> = arrayOf(
            "-0:01:00",
            "-3:01:00",
            "-6:01:00",
            "-9:01:00",
            "-12:01:00",
            "-15:01:00",
            "-18:01:00",
            "-21:01:00"
        ),
        yLabel: String = "Temperature",
        ySuffix: String = "°C",
        yValues: Map<String?, Array<Double>> = mapOf(
            Pair("0:01:00", arrayOf(-4.19, 5.107, 5.68, 6.0)),
            Pair("3:01:00", arrayOf(3.18, -4.478, 5.428, 4.0)),
            Pair("6:01:00", arrayOf(2.485, 3.911, -5.05, 3.0))
        ),
        arraySeriesNames: Array<String> = arrayOf("1000", "1001", "1002", "1004"),
        chartType: String = "LineChart"
    ) {
//        println("data received: xValues = ${xValues.toList()}")
        val window = tabPane.scene.window as Stage
        window.title = title
        if (arraySeriesNames[1]=="1010") divValue = 100 //если второе значение глубины 1010, то делить будем на 100
        seriesNames = arraySeriesNames
        var bc = createBarChartForDay(title, xLabel, xValues, yLabel, ySuffix, yValues, seriesNames, chartType)
        chartParams = ChartParams(title, xLabel, xValues, yLabel, ySuffix, yValues, seriesNames)
        paneForDiagram.children.add(bc)
        seriesNames.forEach {
            val tab = Tab(it)
            tabPane.tabs.add(tab)
            val bc = createBarChartForDay(it, xLabel, xValues, yLabel, ySuffix, yValues, arrayOf(it), chartType)
            if (chartsComboBox.value=="BarChart")
                for (n in bc.lookupAll(".default-color0.chart-bar")) {
                    n.style = "-fx-bar-fill: #008080;"
                }
            else
                for (n in bc.lookupAll(".default-color0.chart-series-line")) {
                    n.style = "-fx-stroke: #008080};"
                }

            tab.contentProperty().set(bc)
        }
        checkBoxesForSeriesPane.children.clear()
        val arrayOfChecksForSeries = arrayOf<CheckBox>() //массив чек-боксов для серий
        arraySeriesNames.forEach {name-> //цикл по сериям данных
            arrayOfChecksForSeries.plus(CheckBox(name).apply {  //добавляем новый чек-бокс с текстом серии данных
                isSelected = true       //ставим галочку
                checkBoxesForSeriesPane.children.add(this)  //добавляем на панель
                this.selectedProperty().addListener { _, _, newValue ->  //слушатель нажатия на чек-бокс
                    println("$name checked is $newValue")
                    if (newValue==false) { //если сняли флаг
                        val seriesPart = bc.data.filter {
                            it.name==name  //фильтруем серии данных
                        }.first()
                        println("found in series = ${seriesPart.name}")
                        println("series value = ${seriesPart.data.toList()}")
                        var seriesValuesArray = arrayOf<SeriesXY>() //врменный массив для записи данных убираемой серии данных
                        seriesPart.data.forEach {//цикл по данным убираемой серии
                            seriesValuesArray = seriesValuesArray.plus(SeriesXY(it.xValue as String, it.yValue as Double)) //добавляем данные во временный массив
                        }
                        mapOfDeletedSeries = mapOfDeletedSeries.plus(Pair(name, seriesValuesArray)) //помещаем убираевые данные в мап с именем серии
//                        println("data for $name from map = ${mapOfDeletedSeries[name]?.toList()}")
                        println("data for $name from map = ${mapOfDeletedSeries[name]?.toList()}")
                        Platform.runLater {
                            bc.data.remove(seriesPart) //убираем эту серию
                        }
                        println("data for $name from map2 = ${mapOfDeletedSeries[name]?.toList()}")
                    }
                    if (newValue==true) { //если поставили флаг
                        println("data for $name from map = ${mapOfDeletedSeries[name]?.toList()}")
                        val seriesCopy = XYChart.Series<String, Number>().apply {//создаем новую серию данных
                            this.name = name //задаем имя
                            mapOfDeletedSeries[name]?.forEach { //и данные из мапа
                                data.add(XYChart.Data(it.x, it.y))
                            }
                        }
                        println("series data from map = ${seriesCopy.data.toList()}")
                        val i = arraySeriesNames.indexOf(name) //для добавления в нужную позицию на легенде
                        if (i>=bc.data.size)
                            bc.data.add(seriesCopy)
                        else bc.data.add(i, seriesCopy)
//                        var bcData = bc.data.sorted()
////                        bc.data.clear()
//                        bc.data.removeAll(bcData)
//                        bc.data.addAll(bcData)
                    }
//                    println("bc.data = ${bc.data}")
                }
            })
        }
        println("Show diagram")
    }

    /**
     * Создает диаграмму по нужным параметрам
     * @param title Заголовок диаграммы
     * @param xLabel Подпись для оси ОХ
     * @param xValues Значения шкалы по оси ОХ
     * @param yLabel Подпись для оси ОУ
     * @param yValues Значения шкалы по оси ОУ
     * @param dataSeries Названия серий данных
     * @param chartType Тип диаграммы
     */
    fun createBarChartForDay(
        title: String,
        xLabel: String,
        xValues: Array<String?>,
        yLabel: String,
        ySuffix: String,
        yValues: Map<String?, Array<Double>>,
        dataSeries: Array<String>,
        chartType: String
//    ): BarChart<String, Number> {
    ): XYChart<String, Number> {
//        var bc : BarChart<String, Number>? = null //объект-диаграмма
        val xAxis = CategoryAxis() //создаем ось ОХ
        val yAxis = NumberAxis() //создаем ось OY
//задаем формат подписей делений оси OY – со знаком °C
        yAxis.tickLabelFormatter = NumberAxis.DefaultFormatter(yAxis, null, ySuffix)

        var bc = if (chartType=="BarChart") BarChart<String, Number>(xAxis, yAxis) //создаем столбчатую диаграмму с осями xAxis и yAxis
                 else LineChart<String, Number>(xAxis, yAxis) //создаем линейную диаграмму с осями xAxis и yAxis
        bc.title =
            if (!title.contains(".") && title.startsWith('1')) ((title.toInt() - 1000.0) / divValue).toString() + " м" else title// задаем название диаграммы
        xAxis.label = xLabel //задаем общую подпись оси ОХ
        xAxis.categories = FXCollections.observableArrayList(listOf(*xValues))//задаем подписи категорий оси ОХ
        xAxis.tickLabelRotation = -45.0
        yAxis.label = yLabel //задаем общую подпись оси OY
        val series = ArrayList<XYChart.Series<String, Number>>() //массив серий данных
        dataSeries.forEach {//цикл по названиям серий данных
            val seriesPart = XYChart.Series<String, Number>().apply { name = it } //создаем серию с нужным названием
            series.add(seriesPart) //добавляем в массив
        }
        // задаем данные
        xValues.forEach {//цикл по значения оси Х - по временам снятия показаний
            val yArray = yValues[it] //берем массив значений данного времени
            if (series.size > 1) { //для нескольких серий данных
                var i = 0
                yArray?.forEach { it2 -> //цикл по массиву значений
                    series[i++].data.add(XYChart.Data(it, it2)) //добавляем в соответствующую серию нужные данные
                }
            } else { //для одной серии данных
                try {
//                    if (yArray!![title.toInt() - 1000] != null) {
//                        print("$it = ")
//                        println("${yArray!![title.toInt() - 1000]}")
                    val k = seriesNames.indexOf(title) //находим в массиве глубин номер текущей
//                        println("$k")
//                        series[0].data.add(XYChart.Data(it, yArray[title.toInt() - 1000]))
                    series[0].data.add(
                        XYChart.Data(
                            it,
                            yArray!![k]
                        )
                    ) //и берем для нее значение в массиве значений температур
                    if (bc is LineChart){
                        series[0].data.forEach { dataPart ->
                            dataPart.node = createDataNode(dataPart.YValueProperty())
                        }
                    } else {
//                        series[0].data.forEach { dataPart ->
////                            dataPart.node = createDataNode(dataPart.YValueProperty())
//                            displayLabelForData(dataPart)
////                            val i = series[0].data.indexOf(dataPart)
////                            dataPart.node = HoveredThresholdNode(
////                                if (i == 0) 0 else series[0].data[i-1].YValueProperty() as Int,
////                                dataPart.YValueProperty() as Int
////                            )
//                        }
                    }
//                    }
                } catch (e: NullPointerException) { //если данных меньше чем значений в xValues
                    return@forEach //выходим из forEach
                }
            }
        }
        if (dataSeries.size == 1) {
            bc.isLegendVisible = false
        }
        bc.data.addAll(series) //добавляем созданные наборы в диаграмму
        series.forEach {   //для добавления всплывающего сообщения при наведении на точку графика или линию барчарта
            for (entry in it.data) { //проходим по данным
//                println("Entered!")
                val s = entry.yValue.toString() //берем значение
                val t = Tooltip(if (s.length>=6) s.substring(0..5) else s) //и сокращаем его, если оно длинне 6 символов
                Tooltip.install(entry.node, t) //добавляем тултип к объекту
//                val node = entry.node
                entry.node.onMouseEntered = EventHandler<MouseEvent?>() {event->  //для сокращения задержки вывода сообщения
                 // +15 moves the tooltip 15 pixels below the mouse cursor;
                 // if you don't change the y coordinate of the tooltip, you will see constant screen flicker
                    t.show(entry.node, event.screenX, event.screenY + 15)
                }
                entry.node.onMouseExited = EventHandler { t.hide() }
            }
            if (series.size==1){
                if (bc is BarChart){
                    series[0].data.forEach { dataPart ->
//                            dataPart.node = createDataNode(dataPart.YValueProperty())
//                        node.parentProperty().addListener { observable, oldValue, parent ->
//                            val parentGroup: Group = parent as Group
//                            parentGroup.children.add(dataText)
//                        }
                        dataPart.node.setOnMousePressed { event: MouseEvent? -> //todo добавить лейбл с текстом значения бара, выводить по координатам щелчка мыши
                            println("value = ${dataPart.yValue}")
                        }
//                        setNodeStyle(dataPart)
//                        displayLabelForData(dataPart)
//                        dataPart.nodeProperty().addListener { ov, oldNode, node ->
//                            println("node in dataPart.nodeProperty() = $node")
//                            if (node != null) {
//                              println("in dataPart.nodeProperty()")
//                                setNodeStyle(dataPart)
//                                displayLabelForData(dataPart)
//                            }
//                        }
                    }
                }
            }
        }

        bc.minWidth = 0.0
        bc.prefWidth = 2000.0 //чтобы диаграмма менялась с изменением окна
        val node = bc.lookup(".data0.chart-bar")
//        println("data0.chart-bar style = ${node.style}")
//        bc.maxHeightProperty().bind(paneForDiagram.maxHeightProperty())
        return bc
    }

    private fun createDataNode(value: ObjectExpression<Number>): Node? {
        val label = Label()
        label.textProperty().bind(value.asString("%,.2f"))
        val pane = Pane(label)
        pane.shape = Circle(6.0)
        pane.isScaleShape = false
        label.translateYProperty().bind(label.heightProperty().divide(-1.5))
        return pane
    }

    fun allInOneClick(actionEvent: ActionEvent) {
        paneForDiagram.isVisible = true
        tabPane.isVisible = false
        colorPicker.isDisable = true
        saveToImageButton.text = "Save to image"
        checkBoxesForSeriesPane.isVisible = true
//        showDiagram()
    }

    fun inTabsClick(actionEvent: ActionEvent) {
        paneForDiagram.isVisible = false
        tabPane.isVisible = true
        colorPicker.isDisable = false
        saveToImageButton.text = "Save to images"
        checkBoxesForSeriesPane.isVisible = false
//        showDiagram()
    }

    fun saveToImage(actionEvent: ActionEvent) {
        val rightNow = Calendar.getInstance()
        val date = rightNow.time as Date
        val sdf = SimpleDateFormat("dd_MM_yy HH_mm_ss")
        val dirPath = sdf.format(date)
//        val currentPath: String = Paths.get(".").toAbsolutePath().normalize().toString()
        println("date = ${sdf.format(date)}")
        Files.createDirectories(Paths.get(dirPath))
        if (paneForDiagram.isVisible) saveDiagramToImage(
            paneForDiagram,
            dirPath,
            (paneForDiagram.scene.window as Stage).title
        )
        if (tabPane.isVisible) {
            tabPane.tabs.forEach {
                tabPane.selectionModel.select(it)
                saveDiagramToImage(it.content, dirPath, it.text)
//                println("text = ${it.text}")
            }
        }
        Desktop.getDesktop().open(File(dirPath))
    }

    private fun saveDiagramToImage(pane: Node, dirPath: String?, title: String?) {
        val snapshot: WritableImage = pane.snapshot(SnapshotParameters(), null)
        val file = File("$dirPath/$title.png")
        ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", file)
    }

    fun onColorPick(actionEvent: ActionEvent) {
        var colorRGB = colorPicker.value.toString()
        colorRGB = colorRGB.subSequence(2, colorRGB.length).toString()
        println("color = $colorRGB")
        val tab = tabPane.selectionModel.selectedItem
        val bc = tab.content
        if (chartsComboBox.value=="BarChart")
            for (n in bc.lookupAll(".default-color0.chart-bar")) {
                n.style = "-fx-bar-fill: #$colorRGB};"
            }
        else {
//            println("in tabs")
            for (n in bc.lookupAll(".default-color0.chart-series-line")) {
                n.style = "-fx-stroke: #$colorRGB};"
            }
        }
//        println("tab content = ${tab.content}")
    }

    fun onChooseChart(actionEvent: ActionEvent) {
        paneForDiagram.children.clear()
        tabPane.tabs.clear()
        showDiagram(chartParams.title, chartParams.xLabel, chartParams.xValues, chartParams.yLabel, chartParams.ySuffix,
                    chartParams.yValues, chartParams.dataSeries, chartsComboBox.value.toString())
        println("chart = ${chartsComboBox.value}")
    }

    private fun displayLabelForData(data: XYChart.Data<String, Number>) {
        val node = data.node
        val dataText = Text(data.yValue.toString() + "!")
        println("!dataText = ${dataText.text}")
        println("node = $node")
        println("node parent = ${node.parent}")
        node.parentProperty().addListener { observable, oldValue, parent ->
            val parentGroup: Group = parent as Group
            parentGroup.children.add(dataText)
        }

        node.boundsInParentProperty().addListener { ov, oldBounds, bounds ->
            dataText.layoutX = (bounds!!.minX + bounds.width / 2 - dataText.prefWidth(-1.0) / 2).roundToInt().toDouble()
            dataText.layoutY = (bounds.minY - dataText.prefHeight(-1.0) * 0.5).roundToInt().toDouble()
        }
//        dataText.toFront()
    }
    private fun setNodeStyle(data: XYChart.Data<String, Number>) {
        val node = data.node
        if (data.yValue.toInt() > 8) {
            node.style = "-fx-bar-fill: -fx-exceeded;"
        } else if (data.yValue.toInt() > 5) {
            node.style = "-fx-bar-fill: -fx-achieved;"
        } else {
            node.style = "-fx-bar-fill: -fx-not-achieved;"
        }
    }
}

