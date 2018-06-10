import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Menu
import javafx.scene.control.MenuBar
import javafx.scene.control.MenuItem
import javafx.scene.control.SeparatorMenuItem
import javafx.scene.layout.BorderPane
import javafx.stage.Stage

class FromTheGroundUpRayTracer : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val message = "From the ground up raytracer"

        val root = BorderPane()
        val scene = Scene(root, 1280.0, 720.0) // TODO size from props?

        createMenus(primaryStage, root)

        primaryStage.title = message
        primaryStage.scene = scene
        primaryStage.show()
    }

    private fun createMenus(primaryStage: Stage, root: BorderPane) {
        val menuBar = MenuBar()
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty())
        root.setTop(menuBar)

        val fileMenu = Menu("File")
        val openMenuItem = MenuItem("Open")
        val exitMenuItem = MenuItem("Exit")
        exitMenuItem.setOnAction({ actionEvent -> Platform.exit() })
        fileMenu.getItems().addAll(openMenuItem, SeparatorMenuItem(), exitMenuItem)

        val helpMenu = Menu("Help")
        val aboutMenuItem = MenuItem("About")
        helpMenu.getItems().addAll(aboutMenuItem)

        menuBar.getMenus().addAll(fileMenu, helpMenu);
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Application.launch(FromTheGroundUpRayTracer::class.java, *args)
        }
    }
}