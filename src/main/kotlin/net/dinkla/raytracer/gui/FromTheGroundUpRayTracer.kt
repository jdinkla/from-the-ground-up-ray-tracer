import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.JavaFXBuilderFactory
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.*
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
import net.dinkla.raytracer.films.BufferedImageFilm
import net.dinkla.raytracer.films.JavaFxFilm
import net.dinkla.raytracer.gui.GuiUtilities
import net.dinkla.raytracer.gui.SceneFileTreeItem
import net.dinkla.raytracer.worlds.World
import net.dinkla.raytracer.worlds.WorldBuilder
import java.io.File


class FromTheGroundUpRayTracer : Application() {

    internal val LOGGER = org.apache.log4j.Logger.getLogger(FromTheGroundUpRayTracer::class.java)

    private var fileChosen : File? = null

    private val aboutDialog: Alert by lazy {
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.setTitle("About this application")
        alert.setHeaderText("From the ground up raytracer")
        alert.setContentText("(c) 2010 - 2018 Jörn Dinkla - https://www.dinkla.net")
        alert
    }

    private val exitDialog: Alert by lazy {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.setTitle("Exit this application?")
        alert.setHeaderText("Do you really want to exit this application?")
        alert.setContentText("")
        alert
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
        val scene = Scene(root, 1280.0, 720.0) // TODO size from props?
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

        primaryStage.title = "From the ground up raytracer"
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun rightSide(primaryStage: Stage, textArea: TextArea): BorderPane {
        val padding = Insets(15.0, 12.0, 15.0, 12.0)
        val rightSide = BorderPane()
        val buttons = HBox()
        buttons.padding = padding
        buttons.spacing = 10.0

        val buttonPreview = Button("JavaFX")
        buttonPreview.setPrefSize(100.0, 20.0);
        buttonPreview.setOnAction {_ -> preview() }
        //buttonPreview.setDisable(true)

        val buttonRender = Button("PNG")
        buttonRender.setPrefSize(100.0, 20.0);
        buttonRender.setOnAction {_ -> render(primaryStage) }
        //buttonRender.setDisable(true)

        buttons.getChildren().addAll(buttonPreview, buttonRender);

        rightSide.padding = padding
        rightSide.top = buttons
        rightSide.center = textArea;
        return rightSide
    }

    private fun preview() {
        println("preview")
        println("render " + this.fileChosen?.name)

        val width = 1280.0
        val height = 720.0

        val w = World()
        val builder = WorldBuilder(w)
        builder.build(this.fileChosen)
        w.initialize()

        val fileName2 = GuiUtilities.getOutputPngFileName(this.fileChosen?.name ?: "")
        val imf = JavaFxFilm(w.viewPlane.resolution)

        val url = "file:$fileName2"
        LOGGER.info("Showing url '$url'")

        val view = ImageView()
        view.image = imf.img
        view.fitWidth = width
        view.isPreserveRatio = true
        view.isSmooth = false
        view.isCache = true
        view.rotationAxis = Rotate.X_AXIS
        view.setRotate(180.0)

        val layout = StackPane()
        layout.children.add(view)

        val newScene = Scene(layout, width, height)

        val newWindow = Stage()
        newWindow.title = this.fileChosen?.name
        newWindow.scene = newScene
        newWindow.show()

        w.render(imf)
    }

    private fun render(primaryStage: Stage) {
        println("render " + this.fileChosen?.name)

        val width = 1280.0
        val height = 720.0

        val w = World()
        val builder = WorldBuilder(w)
        builder.build(this.fileChosen)
        w.initialize()

        val fileName2 = GuiUtilities.getOutputPngFileName(this.fileChosen?.name ?: "")
        val imf = BufferedImageFilm(w.viewPlane.resolution)
        w.render(imf)
        imf.saveAsPng(fileName2)

        val url = "file:$fileName2"
        LOGGER.info("Showing url '$url'")

        val image = Image(url)
        val view = ImageView()
        view.image = image
        view.fitWidth = width
        view.isPreserveRatio = true
        view.isSmooth = false
        view.isCache = true

        val layout = StackPane()
        layout.children.add(view)

        val newScene = Scene(layout, width, height)

        val newWindow = Stage()
        newWindow.title = this.fileChosen?.name
        newWindow.scene = newScene
        newWindow.show()
    }

    private fun leftSide(textArea: TextArea): TreeView<File> {
        val fileView = TreeView<File>(SceneFileTreeItem(File("examples")))
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
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(FromTheGroundUpRayTracer::class.java, *args)
        }
    }
}