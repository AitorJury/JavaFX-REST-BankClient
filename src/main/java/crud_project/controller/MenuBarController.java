package crud_project.ui.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.util.logging.Logger;

import static crud_project.ui.controller.CustomerController.EXIT_CONFIRMATION_MESSAGE;
import static crud_project.ui.controller.CustomerController.EXIT_CONFIRMATION_TITLE;

/**
 * La clase {@code MenuBarController} gestiona el comportamiento de la barra de
 * menú en una aplicación JavaFX.
 *
 * Se encarga de manejar las acciones del usuario asociadas a los distintos
 * elementos del menú, como cerrar la aplicación, mostrar la ventana "Acerca
 * de", cerrar sesión y mostrar la página de ayuda.
 */
public class MenuBarController {

    /**
     * @todo El siguiente código permite la implementación de las acciones CRUD
     * desde el menú reutilizable de forma diferente(polimorfismo) en cada vista 
     * que lo incluya.
     */
    private MenuActionsHandler handler;
    /**
     * Este método debe ser utilizado para indicar desde cada controlador de vista que
     * incluya el menú que controlador se encargará de manejar cada acción.
     * @param handler La clase que implementa MenuActionsHandler.
     */
    public void setMenuActionsHandler(MenuActionsHandler handler) {
        this.handler = handler;
    }

    @FXML
    private void handleCreate() {
        if (handler != null) {
            handler.onCreate();
        }
    }

    @FXML
    private void handleUpdate() {
        if (handler != null) {
            handler.onUpdate();
        }
    }

    @FXML
    private void handleRefresh() {
        if (handler != null) {
            handler.onRefresh();
        }
    }

    @FXML
    private void handleDelete() {
        if (handler != null) {
            handler.onDelete();
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    private Stage userStage;

    /**
     * Elemento de menú que permite cerrar la aplicación.
     */
    @FXML
    public MenuItem fxMenuClose;

    /**
     * Elemento de menú que muestra la ventana "Acerca de" de la aplicación.
     */
    @FXML
    public MenuItem fxMenuAbout;

    /**
     * Elemento de menú que permite cerrar sesión y volver a la ventana de
     * inicio de sesión.
     */
    @FXML
    public MenuItem fxMenuSignOut;

    /**
     * Elemento de menú que muestra la página de ayuda de la aplicación.
     */
    @FXML
    public MenuItem fxMenuContent;

    /**
     * Inicializa la funcionalidad de la barra de menú y configura los
     * manejadores de eventos para cada elemento del menú.
     *
     * @param stage escenario principal de la aplicación, utilizado para
     * gestionar las ventanas de la aplicación
     */
    public void init(Stage stage) {
        this.userStage = stage;
        fxMenuClose.setOnAction(e -> System.exit(0));
        fxMenuSignOut.setOnAction(this::handleOnExitAction);
        fxMenuAbout.setOnAction(this::handleAboutWindow);
        //fxMenuContent.setOnAction(this::handleWindowShowing);

    }

    /**
     * Maneja la acción de cerrar sesión.
     * <p>
     * Muestra un cuadro de diálogo de confirmación al usuario y, si este
     * confirma, cierra la ventana actual. En caso de error, se muestra un
     * mensaje de alerta.
     *
     * @param event evento asociado a la acción del menú
     */
    public void handleOnExitAction(Event event) {
        try {
            LOGGER.info("Clicked exit button");
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, EXIT_CONFIRMATION_MESSAGE,
                    ButtonType.OK, ButtonType.CANCEL);
            alert.setTitle(EXIT_CONFIRMATION_TITLE);

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    userStage.close();
                }
            });

            event.consume();
        } catch (Exception e) {
            LOGGER.severe(e.getMessage());
            handleAlertError("Fail to Close try again");
        }
    }

    /**
     * Maneja la visualización de la ventana de ayuda.
     * <p>
     * Crea una nueva ventana que muestra un archivo HTML embebido
     * ({@code help.html}) con la documentación de ayuda del sistema. Si el
     * archivo no se encuentra, se muestra una alerta de error.
     *
     * @param event evento que desencadena la apertura de la ventana de ayuda
     */
    private void handleWindowShowing(Event event) {
        WebView webView = new WebView();

        WebEngine webEngine = webView.getEngine();

        try {
            String url = getClass().getResource("/crud_project/ui/res/help.html").toExternalForm();
            webEngine.load(url);

            //Crear ventana para mostrar la help al igual que el about
            StackPane root = new StackPane(webView);
            Scene scene = new Scene(root, 800, 600);
            Stage helpStage = new Stage();

            helpStage.setResizable(false);
            helpStage.setTitle("System help");
            helpStage.setScene(scene);
            helpStage.show();

        } catch (Exception e) {
            LOGGER.severe("File help not found: " + e.getMessage());
            handleAlertError("File not found");
        }

    }

    /**
     * Muestra una ventana "Acerca de" no redimensionable con información y
     * créditos de la aplicación.
     *
     * @param event evento que desencadena la apertura de la ventana
     */
    private void handleAboutWindow(Event event) {

        StackPane root = new StackPane();
        Text contentText = new Text("This is a simple bank application made by group 3\n" +
                "made up for:\n" +
                "Aitor Jury, Cynthia Medina & Juan Ismael Caiza");


        // Centrar el alineamiento de las líneas de texto entre sí
        contentText.setTextAlignment(TextAlignment.CENTER);

        root.getChildren().add(contentText);

        Scene scene = new Scene(root, 400, 300);
        Stage stage = new Stage();
        stage.setTitle("About");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

    }

    /**
     * Muestra un cuadro de diálogo de error con el mensaje indicado.
     * <p>
     * El diálogo contiene un título, el mensaje de error y un botón "OK".
     *
     * @param message mensaje de error que se mostrará al usuario
     */
    private void handleAlertError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "", ButtonType.OK);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();

    }

}
