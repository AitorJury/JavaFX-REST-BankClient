package crud_project.ui.controller;

// Imports.
import crud_project.model.Customer;
import crud_project.logic.CustomerRESTClient;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;

/**
 * Controlador de la ventana Sign Up.
 *
 * @author Aitor Jury Rodríguez.
 */
public class SignUpController {

    // TextFields.
    @FXML
    private TextField txtFirstName;
    @FXML
    private TextField txtLastName;
    @FXML
    private TextField txtMiddleInitial;
    @FXML
    private TextField txtEmail;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private PasswordField txtRepeatPassword;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtCity;
    @FXML
    private TextField txtState;
    @FXML
    private TextField txtStreet;
    @FXML
    private TextField txtZip;

    // Labels.
    @FXML
    private Label errFirstName;
    @FXML
    private Label errLastName;
    @FXML
    private Label errMiddleInitial;
    @FXML
    private Label errEmail;
    @FXML
    private Label errPassword;
    @FXML
    private Label errRepeatPassword;
    @FXML
    private Label errPhone;
    @FXML
    private Label errCity;
    @FXML
    private Label errState;
    @FXML
    private Label errStreet;
    @FXML
    private Label errZip;

    // Botones.
    @FXML
    private Button btnCreateAccount;
    @FXML
    private Button btnExit;

    private Stage stage;
    private ButtonType ok = new ButtonType("OK");
    private ButtonType yes = new ButtonType("Yes");
    private ButtonType no = new ButtonType("No");

    // Logger para consola.
    private static final Logger LOGGER
            = Logger.getLogger("crudbankclientsideapplication.ui");

    // Lista de todos los TextFields y PasswordFields.
    private List<javafx.scene.control.TextInputControl> txtFields;
    // Lista de todos los Labels.
    private List<Label> errLabels;

    // Agrupa campos y labels para manejo centralizado.
    private void initialize() {
        txtFields = Arrays.asList(
                txtFirstName, txtLastName, txtMiddleInitial, txtEmail,
                txtPassword, txtRepeatPassword, txtPhone, txtCity,
                txtState, txtStreet, txtZip);

        errLabels = Arrays.asList(
                errFirstName, errLastName, errMiddleInitial, errEmail,
                errPassword, errRepeatPassword, errPhone,
                errCity, errState, errStreet, errZip);
    }

    /**
     * Inicializa la ventana Sign Up y configura los controles.
     *
     * @param stage La ventana principal (Stage) donde se muestra la escena.
     * @param root La raíz (Parent) que contiene los elementos del FXML.
     */
    public void init( Parent root) {
        try {
            initialize();
            Scene scene = new Scene(root);
            stage = new Stage();
            stage.setScene(scene);
            LOGGER.info("Initializing window.");
            stage.setTitle("Sign Up");
            stage.setResizable(false);
            stage.show();

            // Todos los labels de error se vacían.
            for (Label err : errLabels) {
                err.setText("");
            }

            /*
        Los campos de texto y password estarán habilitados para la entrada
        de datos (por defecto).
        La ventana es no modal (por defecto).
             */
            // El botón btnCreateAccount estará deshabilitado hasta que se
            // introduzcan todos los campos de texto.
            btnCreateAccount.setDisable(true);
            btnCreateAccount.setDefaultButton(true);
            // El botón btnExit estará habilitado.
            btnExit.setDisable(false);
            btnExit.setCancelButton(true);
            // El foco se coloca en txtFirstName.
            txtFirstName.requestFocus();

            // Asociar eventos de campos de texto a manejadores.
            txtFirstName.focusedProperty().addListener(this::handleTxtFirstNameFocusChange);
            txtLastName.focusedProperty().addListener(this::handleTxtLastNameFocusChange);
            txtMiddleInitial.focusedProperty().addListener(this::handleTxtMiddleInitialFocusChange);
            txtPhone.focusedProperty().addListener(this::handleTxtPhoneFocusChange);
            txtCity.focusedProperty().addListener(this::handleTxtCityFocusChange);
            txtState.focusedProperty().addListener(this::handleTxtStateFocusChange);
            txtStreet.focusedProperty().addListener(this::handleTxtStreetFocusChange);
            txtZip.focusedProperty().addListener(this::handleTxtZipFocusChange);
            txtEmail.focusedProperty().addListener(this::handleTxtEmailFocusChange);
            txtPassword.textProperty().addListener(this::handleTxtPasswordTextChange);
            txtRepeatPassword.textProperty().addListener(this::handleTxtRepeatPasswordTextChange);
            // Asociar eventos de botones y links a manejadores.
            btnCreateAccount.setOnAction(this::handleBtnCreateAccountOnAction);
            btnExit.setOnAction(this::handleBtnExitOnAction);
            stage.setOnCloseRequest(this::handleBtnExitOnAction);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo FirstName. Valida el
     * campo al perder el foco. Máximo 20 caracteres.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtFirstNameFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtFirstName.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("FirstName must not be empty");
                }
                // Si tiene algo distinto a letras, lanzar excepción.
                if (!text.matches("[a-zA-Z]+")) {
                    throw new Exception("FirstName must contain only letters");
                }
                // Si tiene más de 20 caracteres, recortar y lanzar excepción.
                if (text.length() > 20) {
                    txtFirstName.setText(text.substring(0, 20));
                    throw new Exception("FirstName cannot exceed length of 20");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct FirstName");
                txtFirstName.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtFirstName);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in FirstName");
                txtFirstName.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtFirstName);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo LastName. Valida el
     * campo al perder el foco. Máximo 20 caracteres
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtLastNameFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtLastName.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("LastName must not be empty");
                }
                // Si tiene algo distinto a letras, lanzar excepción.
                if (!text.matches("[a-zA-Z]+")) {
                    throw new Exception("LastName must contain only letters");
                }
                // Si tiene más de 20 caracteres, recortar y lanzar excepción.
                if (text.length() > 20) {
                    txtLastName.setText(text.substring(0, 20));
                    throw new Exception("LastName cannot exceed length of 20");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct LastName");
                txtLastName.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtLastName);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in LastName");
                txtLastName.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtLastName);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo MiddleInitial. Valida
     * que sea una sola letra al perder el foco.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtMiddleInitialFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtMiddleInitial.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("MiddleInitial must not be empty");
                }
                // Si tiene algo distinto a letras, lanzar excepción.
                if (!text.matches("[a-zA-Z]+")) {
                    throw new Exception("MiddleInitial must contain only letters");
                }
                // Si tiene más de 1 caracter, recortar y lanzar excepción.
                if (text.length() > 1) {
                    txtMiddleInitial.setText(text.substring(0, 1));
                    throw new Exception("MiddleInitial cannot exceed length of 20");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct MiddleInitial");
                txtMiddleInitial.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtMiddleInitial);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in MiddleInitial");
                txtMiddleInitial.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtMiddleInitial);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo Phone. Valida que no
     * esté vacío, que contenga solo números y tenga entre 7 y 11 dígitos al
     * perder el foco.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtPhoneFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtPhone.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("Phone must not be empty");
                }
                // Si tiene algo distinto a números, lanzar excepción.
                if (!text.matches("\\d+")) {
                    throw new Exception("Phone must contain only numbers");
                }
                // Si no tiene entre 7 y 11 dígitos, recortar y lanzar excepción.
                if (text.length() < 7 || text.length() > 11) {
                    if (text.length() > 11) {
                        txtPhone.setText(text.substring(0, 11));
                    }
                    throw new Exception("Phone length must be 7-11 digits");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct Phone");
                txtPhone.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtPhone);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in Phone");
                txtPhone.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtPhone);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo City. Valida que no
     * esté vacío, que contenga solo letras y espacios, y que no supere 20
     * caracteres al perder el foco.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtCityFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtCity.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("City must not be empty");
                }
                // Si tiene algo distinto a letras y espacios, lanzar excepción.
                if (!text.matches("[a-zA-Z\\s]+")) {
                    throw new Exception("City must contain only letters and spaces");
                }
                // Si tiene más de 20 caracteres, recortar y lanzar excepción.
                if (text.length() > 20) {
                    txtCity.setText(text.substring(0, 20));
                    throw new Exception("City cannot exceed length of 20");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct City");
                txtCity.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtCity);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in City");
                txtCity.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtCity);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo State. Valida que no
     * esté vacío, que contenga solo letras y espacios, y que no supere 20
     * caracteres al perder el foco.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtStateFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtState.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("State must not be empty");
                }
                // Si tiene algo distinto a letras y espacios, lanzar excepción.
                if (!text.matches("[a-zA-Z\\s]+")) {
                    throw new Exception("State must contain only letters and spaces");
                }
                // Si tiene más de 20 caracteres, recortar y lanzar excepción.
                if (text.length() > 20) {
                    txtState.setText(text.substring(0, 20));
                    throw new Exception("State cannot exceed length of 20");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct State");
                txtState.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtState);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in State");
                txtState.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtState);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo Street. Valida que no
     * esté vacío, que tenga letras, números o símbolos válidos, y que no supere
     * 50 caracteres al perder el foco.
     */
    private void handleTxtStreetFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtStreet.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("Street must not be empty");
                }
                // Si tiene símbolos inválidos, lanzar excepción.
                if (!text.matches("[\\p{L}\\p{N}\\s,\\.-/ºª#]+")) {
                    throw new Exception("Street contains invalid characters");
                }
                // Si tiene más de 50 caracteres, recortar y lanzar excepción.
                if (text.length() > 50) {
                    txtStreet.setText(text.substring(0, 50));
                    throw new Exception("Street cannot exceed length of 50");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct Street");
                txtStreet.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtStreet);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in Street");
                txtStreet.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtStreet);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo Zip. Valida que no
     * esté vacío, que contenga solo números y que tenga exactamente 5 dígitos
     * al perder el foco.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtZipFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtZip.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("Zip must not be empty");
                }
                // Si tiene algo distinto a dígitos, lanzar excepción.
                if (!text.matches("\\d+")) {
                    throw new Exception("Zip must contain only numbers");
                }
                // Si no tiene 5 dígitos, recortar y lanzar excepción.
                if (text.length() != 5) {
                    if (text.length() > 5) {
                        text = text.substring(0, 5);
                        txtZip.setText(text);
                    }
                    throw new Exception("Zip must have length of 5");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct Zip");
                txtZip.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtZip);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in Zip");
                txtZip.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtZip);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de foco en el campo Email. Valida formato
     * de correos (-@-.-) y longitud máxima de 50 caracteres al perder el foco.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del foco.
     * @param newValue El nuevo valor del foco.
     */
    private void handleTxtEmailFocusChange(ObservableValue observable,
            Boolean oldValue, Boolean newValue) {
        if (!newValue) {
            try {
                String text = txtEmail.getText().trim();
                // Si está vacío, lanzar excepción.
                if (text.isEmpty()) {
                    throw new Exception("Email must not be empty");
                }
                // Si tiene formato o símbolos inválidos, lanzar excepción.
                if (!text.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
                    throw new Exception("Email format invalid");
                }
                // Si tiene más de 50 caracteres, recortar y lanzar excepción.
                if (text.length() > 50) {
                    txtEmail.setText(text.substring(0, 50));
                    throw new Exception("Email cannot exceed length of 50");
                }

                // Si es válido, rodear el campo en verde y vaciar error.
                LOGGER.info("Correct Email");
                txtEmail.setStyle("-fx-border-color: green;");
                handleErrLabelChange(null, txtEmail);
            } catch (Exception e) {
                // Si el formato no es válido o está vacío al perder el foco; 
                // rodear el campo en rojo, lanzar una excepción y mostrar 
                // mensaje de error.
                LOGGER.warning("Error in Email");
                txtEmail.setStyle("-fx-border-color: red;");
                handleErrLabelChange(e.getMessage(), txtEmail);
            } finally {
                // Comprobar habilitación de btnCreateAccount.
                checkBtnCreateAccount();
            }
        }
    }

    /**
     * Manejador del evento de cambio de texto en el campo Password. Valida que
     * no esté vacío y que tenga al menos 8 caracteres al cambiar el texto.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del texto.
     * @param newValue El nuevo valor del texto.
     */
    private void handleTxtPasswordTextChange(ObservableValue observable,
            String oldValue, String newValue) {
        try {
            // Si está vacío, lanzar excepción.
            if (newValue.isEmpty()) {
                throw new Exception("Password must not be empty");
            }
            // Si tiene símbolos inválidos, lanzar excepción.
            if (!newValue.matches("[a-zA-Z0-9.*!@#$%&\\-_]+")) {
                throw new Exception("Password contains invalid characters");
            }
            // Si tiene menos de 8 caracteres, lanzar excepción.
            if (newValue.length() < 8) {
                throw new Exception("Password must be at least 8 characters");
            }

            // Si es válido, rodear el campo en verde y vaciar error.
            LOGGER.info("Correct Password");
            txtPassword.setStyle("-fx-border-color: green;");
            handleErrLabelChange(null, txtPassword);
        } catch (Exception e) {
            // Si el formato no es válido o está vacío al cambiar el texto; 
            // rodear el campo en rojo, lanzar una excepción y mostrar 
            // mensaje de error.
            LOGGER.warning("Error in Password");
            txtPassword.setStyle("-fx-border-color: red;");
            handleErrLabelChange(e.getMessage(), txtPassword);
        } finally {
            // Comprobar habilitación de btnCreateAccount.
            checkBtnCreateAccount();
        }
    }

    /**
     * Manejador del evento de cambio de texto en el campo RepeatPassword.
     * Valida que no esté vacío y que coincida con Password al cambiar el texto.
     *
     * @param observable El valor observable que cambia.
     * @param oldValue El valor anterior del texto.
     * @param newValue El nuevo valor del texto.
     */
    private void handleTxtRepeatPasswordTextChange(ObservableValue observable,
            String oldValue, String newValue) {
        try {
            // Si está vacío, lanzar excepción.
            if (newValue.isEmpty()) {
                throw new Exception("RepeatPassword must not be empty");
            }
            // Si no es igual que Password, lanzar excepción.
            if (!newValue.equals(txtPassword.getText())) {
                throw new Exception("RepeatPassword must match Password");
            }

            // Si es válido, rodear el campo en verde y vaciar error.
            LOGGER.info("Correct RepeatPassword");
            txtRepeatPassword.setStyle("-fx-border-color: green;");
            handleErrLabelChange(null, txtRepeatPassword);
        } catch (Exception e) {
            // Si el formato no es válido o está vacío al cambiar el texto; 
            // rodear el campo en rojo, lanzar una excepción y mostrar 
            // mensaje de error.
            LOGGER.warning("Error in RepeatPassword");
            txtRepeatPassword.setStyle("-fx-border-color: red;");
            handleErrLabelChange(e.getMessage(), txtRepeatPassword);
        } finally {
            // Comprobar habilitación de btnCreateAccount.
            checkBtnCreateAccount();
        }
    }

    /**
     * Manejador del evento de acción en el botón CreateAccount.
     *
     * @param event El evento de acción generado por el botón.
     */
    private void handleBtnCreateAccountOnAction(ActionEvent event) {
        CustomerRESTClient client = null;
        try {
            LOGGER.info("Clicked create account button");
            // Crear un objeto Customer
            Customer customer = new Customer();
            // Establecer propiedades del objeto a partir de los valores dados.
            customer.setFirstName(txtFirstName.getText());
            customer.setLastName(txtLastName.getText());
            customer.setMiddleInitial(txtMiddleInitial.getText());
            customer.setEmail(txtEmail.getText());
            customer.setPassword(txtPassword.getText());
            customer.setPhone(Long.parseLong(txtPhone.getText()));
            customer.setCity(txtCity.getText());
            customer.setState(txtState.getText());
            customer.setStreet(txtStreet.getText());
            customer.setZip(Integer.parseInt(txtZip.getText()));

            // Establecer un objeto Client para crear el usuario.
            client = new CustomerRESTClient();
            client.create_XML(customer);

            // Si conecta correctamente, el email no existe en la base y no
            // ocurre algún otro error, mostrar un alert modal que indique que
            // se ha generado correctamente el usuario. Debe aceptar el mensaje
            // con un OK.
            LOGGER.info("Correct user creation");
            new Alert(Alert.AlertType.INFORMATION, "Customer created and logged"
                    + " in successfully!", ok)
                    .showAndWait();

            // Conectar con Sign In para iniciar sesión.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/view/SignIn.fxml"));
            Parent root = loader.load();

            SignInController controller = loader.getController();
            controller.initStage(this.stage, root);
            
            LOGGER.info("Correct connection with Sign In");
        } catch (ForbiddenException e) {
            // Excepción generada por el servidor por email ya usado. Mostrar
            // un alert modal que indique el error. Debe aceptar el mensaje
            // con un OK. Se devuelve al Sign Up.
            LOGGER.warning("Error user creation: Email exists");
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "The email exists. Please try another.", ok).showAndWait();
        } catch (InternalServerErrorException e) {
            // Excepción generada por el servidor por falta de conexión con esta. 
            // Mostrar un alert modal que indique el error. Debe aceptar el 
            // mensaje con un OK. Se devuelve al Sign Up.
            LOGGER.warning("Error user creation: Server error");
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Server error. Please try again later.", ok).showAndWait();
        } catch (Exception e) {
            // Excepción generada por cualquier otro error. Mostrar un alert 
            // modal que indique el error. Debe aceptar el mensaje con un OK. Se
            // devuelve al Sign Up.
            LOGGER.warning("Error user creation: Unknown error");
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,
                    "Unexpected error: " + e.getMessage(), ok).showAndWait();
        } finally {
            // Cerrar cliente correctamente.
            client.close();
        }
    }
    
    /**
     * Manejador del evento de acción en el botón Exit.
     *
     * @param event El evento de acción generado por el botón.
     */
    private void handleBtnExitOnAction(Event event) {
        try {
            /*
            LOGGER.info("Clicked exit button");
            // Mostrar alert modal de confirmación para salir de la ventana y pase a Sign In.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you really want to exit Sign Up?", yes, no);
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, reenviar a la ventana Sign In.
                // Si cancela, mantener la ventana abierta.
                if (resp == yes) {
                    try {
                    // Cargar FXML de Sign In y obtener la ventana.
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(
                        "/crudbankclientsideapplication/ui/SignIn.fxml"));
                    Parent root = loader.load();
                    SignInController controller = loader.getController();
                    controller.initStage(this.stage, root);
                    LOGGER.info("Correct connection with Sign In");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });
            */
            event.consume();
            stage.close();
            
        } catch (Exception e) {
            // Si no logra cambiar u otro error, mostrar un alert modal que 
            // indique que no se puede salir. Debe aceptar el mensaje con un OK.
            LOGGER.warning("Error leaving Sign Up");
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not exit Sign Up window.", ok)
                    .showAndWait();
        }
    }

    /**
     * Gestiona los labels de error de la ventana Sign Up. Si el parámetro
     * errText es null, se limpian todos los labels de error. Si errText
     * contiene texto, se usará para mostrar un mensaje de error.
     *
     * @param message Mensaje de error a mostrar (null para limpiar).
     * @param textField Campo de texto asociado.
     */
    private void handleErrLabelChange(String message, javafx.scene.control
            .TextInputControl textField) {
        // Si los errores o los campos están vacíos, salir del método.
        if (errLabels == null || txtFields == null) {
            return;
        }

        // Busco el error correspondiente con la posición del campo.
        int i = txtFields.indexOf(textField);
        if (i != -1 && i < errLabels.size()) {
            Label errLabel = errLabels.get(i);
            // Si el mensaje está vacío, borrar el error.
            // Si el mensaje tiene algo de texto, mostrarlo en el error
            // correspondiente.
            if (message == null || message.isEmpty()) {
                LOGGER.info("Emptying message");
                errLabel.setText("");
            } else {
                LOGGER.info("Writing message");
                errLabel.setText(message);
            }
        }
    }

    /**
     * Método que habilita btnCreateAccount si todos los campos son válidos.
     */
    private void checkBtnCreateAccount() {
        LOGGER.info("Confirming validations");
        boolean allValid = true;

        if (!txtFirstName.getText().matches("[a-zA-Z]{1,20}")) {
            allValid = false;
        }
        if (!txtLastName.getText().matches("[a-zA-Z]{1,20}")) {
            allValid = false;
        }
        if (!txtMiddleInitial.getText().matches("[a-zA-Z]{1}")) {
            allValid = false;
        }
        if (!txtEmail.getText().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            allValid = false;
        }
        if (!txtPassword.getText().matches("[a-zA-Z0-9.*!@#$%&\\-_]{8,}")) {
            allValid = false;
        }
        if (!txtRepeatPassword.getText().equals(txtPassword.getText())) {
            allValid = false;
        }
        if (!txtPhone.getText().matches("\\d{7,11}")) {
            allValid = false;
        }
        if (!txtCity.getText().matches("[a-zA-Z\\s]{1,20}")) {
            allValid = false;
        }
        if (!txtState.getText().matches("[a-zA-Z\\s]{1,20}")) {
            allValid = false;
        }
        if (!txtStreet.getText().matches("[\\p{L}\\p{N}\\s,\\.-/ºª#]{1,50}")) {
            allValid = false;
        }
        if (!txtZip.getText().matches("\\d{5}")) {
            allValid = false;
        }

        btnCreateAccount.setDisable(!allValid);
    }
    public Stage getStage(){
        return this.stage;
    }
}
