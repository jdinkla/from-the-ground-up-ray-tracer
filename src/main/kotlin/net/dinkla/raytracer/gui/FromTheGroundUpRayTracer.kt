package net.dinkla.raytracer.gui

import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.Alert.AlertType.CONFIRMATION
import javafx.scene.control.Alert.AlertType.INFORMATION
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.scene.transform.Rotate
import javafx.stage.Stage
import net.dinkla.raytracer.examples.World5
import net.dinkla.raytracer.examples.World6
import net.dinkla.raytracer.films.BufferedImageFilm
import net.dinkla.raytracer.films.JavaFxFilm
import net.dinkla.raytracer.utilities.AppProperties
import net.dinkla.raytracer.world.WorldDef
import org.slf4j.LoggerFactory
import java.io.File


class FromTheGroundUpRayTracer : Application() {

    private var fileChosen: File? = null

    private val aboutDialog: Alert by lazy {
        Alert(INFORMATION).apply {
            title = informationTitle
            headerText = informationHeader
            contentText = informationContext
        }
    }

    private val exitDialog: Alert by lazy {
        Alert(CONFIRMATION).apply {
            title = confirmationTitle
            headerText = confirmationHeader
            contentText = confirmationContext
        }
    }

    private val menuBar: MenuBar by lazy {
        val menuBar = MenuBar()

        val fileMenu = Menu("File")
        val openMenuItem = MenuItem("Open")
        val exitMenuItem = MenuItem("Exit")
        exitMenuItem.setOnAction({
            val result = exitDialog.showAndWait()
            if (result.get() == ButtonType.OK) {
                Platform.exit()
            }
        })
        fileMenu.getItems().addAll(openMenuItem, SeparatorMenuItem(), exitMenuItem)

        val helpMenu = Menu("Help")
        val aboutMenuItem = MenuItem("About")
        aboutMenuItem.setOnAction { aboutDialog.show() }
        helpMenu.getItems().addAll(aboutMenuItem)

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        menuBar
    }

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val root = BorderPane()
        val scene = Scene(root, width, height)
        val textArea = TextArea("display the source code here")
        textArea.font = Font.font("Courier New", FontWeight.NORMAL, 11.0)

        menuBar.prefWidthProperty().bind(primaryStage.widthProperty())
        root.setTop(menuBar)

        val left = leftSide(textArea)
        val right = rightSide(primaryStage, textArea)

        val splitView = SplitPane()
        splitView.items.add(left)
        splitView.items.add(right)

        root.setCenter(splitView);

        primaryStage.title = appTitle
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun rightSide(primaryStage: Stage, textArea: TextArea): BorderPane {
        val defaultPadding = Insets(15.0, 12.0, 15.0, 12.0)
        val buttons = HBox().apply {
            padding = defaultPadding
            spacing = 10.0
        }

        val buttonPreview = Button("Preview").apply {
            setPrefSize(100.0, 20.0);
            setOnAction { _ -> preview() }
        }

        val buttonPNG = Button("PNG").apply {
            setPrefSize(100.0, 20.0);
            setOnAction { _ -> png(primaryStage) }
        }

        buttons.getChildren().addAll(buttonPreview, buttonPNG);

        val rightSide = BorderPane().apply {
            padding = defaultPadding
            top = buttons
            center = textArea;
        }
        return rightSide
    }

    private fun preview() {
        LOGGER.info("preview " + fileChosen?.name)

        val wdef : WorldDef? = when (fileChosen?.name) {
            "World5.kt" -> World5
            "World6.kt" -> World6
            else -> null
        }
        if (wdef == null) return
        val w = wdef.world()
        w.initialize()

        val film = JavaFxFilm(w.viewPlane.resolution)
        openWindow(film.img, 180.0)
        w.render(film)
    }

    private fun png(primaryStage: Stage) {
        LOGGER.info("png " + fileChosen?.name)

        val fileName = GuiUtilities.getOutputPngFileName(this.fileChosen?.name ?: "")

        val w = World5.world()
        w.initialize()

        val film = BufferedImageFilm(w.viewPlane.resolution)
        w.render(film)
        film.saveAsPng(fileName)

        openWindow(Image("file:$fileName"))
    }

    private fun openWindow(img: Image, rotateDegree: Double = 0.0) {
        val view = ImageView().apply {
            image = img
            fitWidth = width
            isPreserveRatio = true
            isSmooth = false
            isCache = true
            rotationAxis = Rotate.X_AXIS
            setRotate(rotateDegree)
        }

        val layout = StackPane()
        layout.children.add(view)

        Stage().apply {
            title = fileChosen?.name
            scene = Scene(layout, Companion.width, Companion.height)
            show()
        }
    }

    private fun leftSide(textArea: TextArea): TreeView<File> {
        val fileView = TreeView<File>(SceneFileTreeItem(File(examplesDirectory)))
        fileView.setCellFactory { _ -> treeCell() }
        fileView.setOnMouseClicked { event -> selectFileInTree(fileView, textArea, event) }
        fileView.getTreeItem(0).setExpanded(true)
        return fileView
    }

    private fun treeCell(): TreeCell<File> {
        return object : TreeCell<File>() {
            override fun updateItem(item: File?, empty: Boolean) {
                super.updateItem(item, empty)
                text = if (empty || item == null) "" else item.name
            }
        }
    }

    private fun selectFileInTree(fileView: TreeView<File>, textArea: TextArea, event: MouseEvent) {
        val node = event.pickResult.intersectedNode
        if (node is Text || node is TreeCell<*> && node.text != null) {
            val file = (fileView.getSelectionModel().getSelectedItem() as TreeItem<File>).value
            if (file.isFile) {
                this.fileChosen = file
                textArea.text = file.readText()
            }
        }
    }

    companion object {

        internal val LOGGER = LoggerFactory.getLogger(this::class.java)

        private val examplesDirectory = AppProperties["examples.directory"] as String

        val width = AppProperties.getAsDouble("display.width")
        val height = AppProperties.getAsDouble("display.height")

        val appTitle = AppProperties["app.title"] as String

        val informationTitle = AppProperties["information.title"] as String
        val informationHeader = AppProperties["information.headerText"] as String
        val informationContext = AppProperties["information.contentText"] as String

        val confirmationTitle = AppProperties["confirmation.title"] as String
        val confirmationHeader = AppProperties["confirmation.headerText"] as String
        val confirmationContext = AppProperties["confirmation.contentText"] as String

        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(FromTheGroundUpRayTracer::class.java, *args)
        }
    }
}