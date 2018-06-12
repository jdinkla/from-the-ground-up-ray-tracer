import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.BorderPane
import javafx.stage.Stage
import javafx.scene.web.HTMLEditor
import javafx.scene.control.SplitPane
import net.dinkla.raytracer.gui.SceneFileTreeItem
import java.io.File
import javafx.scene.control.TreeCell
import javafx.scene.control.TreeView
import javafx.scene.text.Text
import javax.security.auth.callback.Callback


class FromTheGroundUpRayTracer : Application() {

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
        exitMenuItem.setOnAction({ actionEvent ->
            val result = exitDialog.showAndWait()
            if (result.get() == ButtonType.OK) {
                Platform.exit()
            }
        })
        fileMenu.getItems().addAll(openMenuItem, SeparatorMenuItem(), exitMenuItem)

        val helpMenu = Menu("Help")
        val aboutMenuItem = MenuItem("About")
        aboutMenuItem.setOnAction { actionEvent -> aboutDialog.show() }
        helpMenu.getItems().addAll(aboutMenuItem)

        menuBar.getMenus().addAll(fileMenu, helpMenu);
        menuBar
    }

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val root = BorderPane()
        val scene = Scene(root, 1280.0, 720.0) // TODO size from props?

        menuBar.prefWidthProperty().bind(primaryStage.widthProperty())
        root.setTop(menuBar)

        val fileView = TreeView<File>(SceneFileTreeItem(File("examples")))
        fileView.setCellFactory { treeView ->
            object : TreeCell<File>() {
                override fun updateItem(item: File?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (empty || item == null) "" else item.name
                }
            }
        }
        fileView.getTreeItem(0).setExpanded(true)

        val splitView = SplitPane()
        splitView.items.add(fileView)
        splitView.items.add(TextArea("display the source code here"))

        root.setCenter(splitView);

        primaryStage.title = "From the ground up raytracer"
        primaryStage.scene = scene
        primaryStage.show()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(FromTheGroundUpRayTracer::class.java, *args)
        }
    }
}