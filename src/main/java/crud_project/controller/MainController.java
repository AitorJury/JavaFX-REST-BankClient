package crud_project.ui.controller;

// Imports.
import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

/**
 *
 * @author Aitor & Cynthia.
 */
public class MainController {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");
    @FXML
    private Button btnLogout;
    @FXML
    private Button btnExit;
    private Stage stage;
    private final ButtonType yes = new ButtonType("Yes");
    private final ButtonType no = new ButtonType("No");

    /**
     * Inicializa la ventana Main y configura los controles.
     *
     * @param stage La ventana principal (Stage) donde se muestra la escena.
     * @param root La raíz (Parent) que contiene los elementos del FXML.
     */
    public void initStage(Stage stage, Parent root) {
        Scene scene = new Scene(root);
        stage.setScene(scene);
        this.stage = stage;
        LOGGER.info("Initializing window");
        stage.setTitle("Main");
        stage.setResizable(false);
        
        // El botón btnLogout está habilitado.
        // Se enfoca por defecto la ventana de forma no modal.
        btnLogout.setOnAction(this::handleBtnLogoutOnAction);
        btnExit.setOnAction(this::handleBtnExitOnAction);
    }

    /**
     * Manejador del evento de acción en el botón Log Out.
     *
     * @param event El evento de acción generado por el botón.
     */
    private void handleBtnLogoutOnAction(ActionEvent event) {
        try {
            LOGGER.info("Clicked log out button");
            //Muestra un alert modal de confirmación para cerrar sesión.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                    "Do you want to log out?", yes, no);
            alert.setTitle("Log Out from the application");
            alert.setHeaderText("Departure confirmation");

            //Si el usuario confirma, la sesión se cierra y se redirige a la ventana de Sign In.
            //Si el usuario cancela, la ventana principal permanece abierta y sin cambios.
            alert.showAndWait().ifPresent(resp -> {
                if (resp == yes) {
                    LOGGER.info("Clicked Yes");
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass()
                                .getResource("SignIn.fxml"));
                        Parent root = loader.load();
                        
                        SignInController controller = loader.getController();
                        controller.initStage(this.stage, root);

                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            });
            LOGGER.info("Clicked No");
        } catch (Exception ex) {
            LOGGER.warning("Error in Log Out Button");
            ex.printStackTrace();
        }
    }
    
    /**
     * Manejador del evento de acción en el botón Exit.
     *
     * @param event El evento de acción generado por el botón.
     */
    private void handleBtnExitOnAction(ActionEvent event) {
        //Muestra un alert modal de confirmación para salir.
        LOGGER.info("Clicked Exit Button");
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you really want to exit?", yes, no);

            //Si el usuario confirma, la ventana se cierra.
            //Si el usuario cancela, la ventana principal permanece abierta y sin cambios.
            alert.showAndWait().ifPresent(resp -> {
                if (resp == yes) {
                    Stage stage = (Stage) btnExit.getScene().getWindow();
                    stage.close();
                    LOGGER.info("Clicked Yes");
                }
            });
            LOGGER.info("Clicked No");
        } catch (Exception ex) {
            LOGGER.warning("Error in Exit Button");
            ex.printStackTrace();
        }
    }
}
