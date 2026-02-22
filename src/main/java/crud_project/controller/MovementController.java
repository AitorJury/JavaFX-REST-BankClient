/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project.ui.controller;

import crud_project.logic.AccountRESTClient;
import crud_project.logic.MovementRESTClient;
import crud_project.model.Account;
import crud_project.model.AccountType;
import crud_project.model.Customer;
import crud_project.model.Movement;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javax.ws.rs.core.GenericType;

/**
 *
 * @author cynthia
 * @todo @fixme Hacer que la siguiente clase implemente las interfaces
 * Initializable y MenuActionsHandler para que al pulsar en las acciones CRUD
 * del menú Actions se ejecuten los métodos manejadores correspondientes a la
 * vista que incluye el menú. El método initialize debe llamar a
 * setMenuActionsHandler() para establecer que este controlador es el manejador
 * de acciones del menú. HECHO
 */
public class MovementController implements MenuActionsHandler {

    private static final Logger LOGGER = Logger.getLogger("crudbankclientside.ui");

    private Stage stage;
    private Scene scene;
    private Movement movement;
    private Customer customer;
    private Account account;
    private AccountType accountType;
    private Double finalNewBalance;
    private Double finalNewCredit;

    //Agregamos los id del fxml al controlador 
    @FXML
    private Label lblCreditLine;
    @FXML
    private Label lblNumAccount;
    @FXML
    private TableView<Movement> tbMovement;
    @FXML
    private TableColumn<Movement, Date> clDate;
    @FXML
    private TableColumn<Movement, Double> clAmount;
    @FXML
    private TableColumn<Movement, String> clDescription;
    @FXML
    private TableColumn<Movement, Double> clBalance;
    @FXML
    private Button btnDelete;
    @FXML
    private Button btnBack;
    @FXML
    private Label lblError;
    @FXML
    private ComboBox comboAccount;
    @FXML
    private TextField txtAmount;
    @FXML
    private ComboBox comboType;
    @FXML
    private Button createMovement;
    @FXML
    private Label lblName;
    @FXML
    private Label lblBalance;
    @FXML
    private Label lblNmCredit;
    /**
     * Controlador del menú superior JavaFX asigna automáticamente el campo
     * topMenuController cuando usas fx:id="hBoxMenu".
     */
    @FXML
    private MenuBarController hBoxMenuController;

    //Se crea los botones para el alert
    private final ButtonType ok = new ButtonType("OK");
    private final ButtonType yes = new ButtonType("Yes");
    private final ButtonType no = new ButtonType("No");

    private int contador = 0;
    AccountRESTClient accountClient = new AccountRESTClient();
    MovementRESTClient movementClient = new MovementRESTClient();

    public void init(Parent root) {
        lblNumAccount.setText(account.getId().toString());
        //Se muestra la escenar
        Scene scene = new Scene(root);
        this.stage.setScene(scene);
        stage.setTitle("Movements");
        LOGGER.info("Initializing Movement Window");
        // Establecer el título de la ventana.
        this.stage.setTitle("Movement page");
        this.stage.setResizable(false);
        this.stage.setOnCloseRequest(this::handleWindowClose);

        if (hBoxMenuController != null) {
            //HECHO
            hBoxMenuController.setMenuActionsHandler(this);
            hBoxMenuController.init(this.stage);
            hBoxMenuController.fxMenuContent.setOnAction(e -> {
                showCustomerHelp("/crud_project/ui/res/help_movement.html");
            });
        }

        //Da valor a la factoría de celda 
        clAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        clDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        clBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        //Carga los movimientos de la tabla
        loadMovements();
        //Para que cargue los label
        lblBalance.setText(account.getBalance().toString());
        if (lblCreditLine.isVisible()) {
            lblCreditLine.setText(account.getCreditLine().toString());
        }
        buttonEnable();

        //Creamos variable que necesitaremos para el delete
        Double creditNow = 0.0;
        btnBack.setCancelButton(true);
        //Se pone los valores en la combo de la description (type)
        ObservableList<String> items = FXCollections.observableArrayList("Deposit", "Payment");
        comboType.setItems(items);

        //Esto es para que dependiendo el tipo de cuenta si no es credito que no se muestren los lbl
        if (account.getType() != AccountType.CREDIT) {
            lblCreditLine.setVisible(false);
            lblNmCredit.setVisible(false);
        } else {
            lblCreditLine.setVisible(true);
            lblNmCredit.setVisible(true);
        }

        //Deshabilita el botón de crear movimiento
        createMovement.setDisable(true);
        //Ponemos eventos a manejadores
        createMovement.setOnAction(this::handleBtnCreate);
        btnDelete.setOnAction(this::handleBtnDelete);
        btnBack.setOnAction(this::handleBtnBack);
        //Se pone un listener en el TextField y en la ComboBox para ver cuando los valores cambian lleven al método
        txtAmount.textProperty().addListener((observable, oldValue, newValue) -> buttonEnable());
        comboType.valueProperty().addListener((observable, oldValue, newValue) -> buttonEnable());

        this.stage.show();

        btnDelete.setOnAction(this::handleBtnDelete);
        btnBack.setOnAction(this::handleBtnBack);
        this.stage.show();
        //
        /* MEJORAS FORMATEAR FECHA*/
        clDate.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        clDate.setCellFactory(column -> new TableCell<Movement, Date>() {
            private final SimpleDateFormat format
                    = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : format.format(item));
            }
        });
        /*MEJORA PARA QUE APAREZCA EL EURO*/
        clBalance.setCellFactory(column -> new TableCell<Movement, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f €", item));
            }
        });
        clAmount.setCellFactory(column -> new TableCell<Movement, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : String.format("%.2f €", item));
            }
        });
        /* MEJORA PARA QUE EL VALOR DEL DINERO ESTA EN LA DERECHA  */
        clAmount.setStyle("-fx-alignment: CENTER-RIGHT;");
        clBalance.setStyle("-fx-alignment: CENTER-RIGHT;");
        clDescription.setStyle("-fx-alignment: CENTER;");

    }

    public void loadMovements() {

        try {
            //this.account = accountClient.find_XML(Account.class, account.getId().toString());
            //Id de prueba idAccount
            //Se crea una lista de movimientos
            GenericType<List<Movement>> movementListType = new GenericType<List<Movement>>() {
            };

            List<Movement> movements = movementClient.findMovementByAccount_XML(movementListType, account.getId().toString());
            //Si la tabla esta vacia lanzamos excepcion de que no hay datos que cargar
            if (movements == null || movements.isEmpty()) {
                LOGGER.info("No movements found ");
                throw new Exception("No movements found for this account");
            }
            ObservableList<Movement> dataMovement = FXCollections.observableArrayList(movements);
            //Se muestra la lista en la tabla
            LOGGER.info("Showing table of movements");
            FXCollections.sort(dataMovement, (m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()));
            tbMovement.setItems(dataMovement);
            //Ponemos en el label del balance el dinero que tiene en la cuenta
            lblBalance.setText(account.getBalance().toString());
            //Vemos si es visible credit line y si lo es ponemos el dinero
            if (lblCreditLine.isVisible()) {
                lblCreditLine.setText(account.getCreditLine().toString());
            }
        } catch (Exception e) {
            handlelblError(e.getMessage());
            LOGGER.info("No load movements");
        }
    }

    public void handleBtnBack(ActionEvent event) {
        try {
            // Mostrar alert modal de confirmación para salir de la aplicación.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you want to go to manage your accounts?", yes, no);
            alert.setTitle("Exit to Accounts");
            alert.setHeaderText("Departure confirmation");
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, cerrar la aplicación.
                if (resp == yes) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/view/Accounts.fxml"));
                        Parent root = loader.load();
                        //Cargamos controlador
                        AccountsController controller = loader.getController();
                        controller.setCustomer(customer);

                        //Iniciamos la pagina y cerramos la mia
                        LOGGER.info("Showing accounts page");
                        controller.init(root);
                        this.stage.close();

                    } catch (IOException ex) {
                        Logger.getLogger(MovementController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                // Si no confirma, la ventana permanecerá abierta.
            });
            //Se carga el controlador y la vista de la ventana de Accounts
        } catch (Exception e) {
            LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }

    }

    //Contador a 1 cuando este a 0 se deshabilite el boton 
    public void handleBtnDelete(ActionEvent event) {
        try {
            //Cargamos los movimientos para ordenarlos
            loadMovements();
            if (tbMovement.getItems().isEmpty()) {
                throw new Exception("There are no movements on the account.");
            }
            //Coge el ultimo movimiento de la fecha más alta
            Movement lastDate = tbMovement.getItems().stream()
                    .max((m1, m2) -> m1.getTimestamp().compareTo(m2.getTimestamp()))
                    .get();
            //Buscamos el id del utlimo movimiento por fecha mas alta
            String lastDateToId = String.valueOf(lastDate.getId());

            // Mostrar alert modal de confirmación para borrar el ultimo movimiento.
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Do you want remove the last movement?", yes, no);
            alert.setTitle("Alert to delete movement");
            alert.setHeaderText("Departure confirmation");
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, cerrar la aplicación.
                if (resp == yes) {
                    contador++;
                    //Si la respuesta es que si borra el ultimo movimiento
                    //Asignamos el balance para devolver el dinero
                    Double actualBalance = this.account.getBalance();
                    //Buscamos en el ultimo movimiento cuanta cantidad de dinero habia
                    Double importDelete = lastDate.getAmount();
                    //Le devuelves el dinero del ultimo movimiento
                    this.account.setBalance(actualBalance - importDelete);
                    //this.account.setCreditLine(actualCredit - importCredit);
                    //Borramos el movimiento por la ultima fecha
                    movementClient.remove(lastDateToId);
                    //Cargamos la cuenta del cliente para que se guarden los cambios
                    accountClient.updateAccount_XML(this.account);
                    //Cargamos los movimientos
                    loadMovements();
                    LOGGER.info("Movement deleted");
                } else {
                    alert.close();
                }
                //Esto es para que se deshabilite el boton de delete
                if (contador > 0) {
                    btnDelete.setDisable(true);
                }
            });
        } catch (Exception e) {
            handlelblError("Could not delete movement");
            e.printStackTrace();
        }
    }

    public void buttonEnable() {
        //Comprueba que la comboBox esté seleccionada y que el TextField no esté vacio
        boolean buttonTy = true;
        boolean buttonTx = true;

        String type = (String) comboType.getValue();
        String txt = txtAmount.getText();

        if (!txt.trim().isEmpty()) {
            lblError.setText("");
        }
        if (type == null) {
            buttonTy = false;
        }

        if (txt.trim().isEmpty()) {
            buttonTx = false;
        }
        createMovement.setDisable(!(buttonTy && buttonTx));
    }

    public void handleBtnCreate(ActionEvent event) {

        try {
            //Se crea el movimiento
            Movement newMovement = new Movement();
            //En el string type se parsea a un String con el valor seleccionado en la combo
            String type = (String) comboType.getValue();
            //Se recoge en amount el valor del text y se parsea a double
            Double amount = Double.valueOf(txtAmount.getText());
            //Si la cantidad es menor o igual que 0 salta el label de error
            if (amount <= 0) {
                throw new Exception("Amount cant be negative");
            }
            AccountType accountType = account.getType();
            //Creamos el nuevo balance
            Double newBalance = 0.0;
            //Asignamos a la variable el balance que hay
            Double accountBalance = this.account.getBalance();
            //Asignamos a la variable la linea de credito que tiene
            Double accountCredit = this.account.getCreditLine();
            finalNewCredit = accountCredit;

            if (type.equals("Payment")) {
                // Validamos el credito y el balance que tiene
                if (amount > (accountBalance + accountCredit)) {
                    throw new Exception("The balance and the credit are insuficient");
                }
                if (accountBalance >= amount) {
                    //Validamos si el balance llega sin necesitar la linea de credito
                    newBalance = accountBalance - amount;
                } else {
                    //Calculamos cuanto falta para cubrir con el credito
                    Double restateAmount = amount - accountBalance; // Lo que falta por cubrir
                    newBalance = 0.0;
                    // Restamos lo que falta de la línea de crédito
                    Double newCredit = accountCredit - restateAmount;
                    this.account.setCreditLine(newCredit);
                }
                newMovement.setAmount(-amount);
                newMovement.setDescription(type);
            } else if (type.equals("Deposit")) {
                newMovement.setAmount(amount);
                newMovement.setDescription(type);
                newBalance = accountBalance + amount;
            }
            this.finalNewBalance = newBalance;
            newMovement.setBalance(newBalance);
            //Metemos el newBalance en la variable final para poder mostrarla
            finalNewBalance = newBalance;
            //Se pone la fecha y hora actual 
            Date date = new Date();
            newMovement.setTimestamp(date);
            //Salta un alert para confirmar la creacción del movimiento
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                    "Movement Type:" + type + ". Amount: " + amount, yes, no);
            alert.setTitle("Alert to create movement");
            alert.setHeaderText("Create Movement");
            alert.showAndWait().ifPresent(resp -> {
                // Si confirma, cerrar la aplicación.
                if (resp == yes) {
                    try {
                        //Se crea el movimiento
                        movementClient.create_XML(newMovement, account.getId().toString());
                        //Asignamos el balance
                        this.account.setBalance(finalNewBalance);
                        //Actualizo la cuenta para que se guarde
                        accountClient.updateAccount_XML(this.account);
                        //Ponemos en el lbl de nuevo el nuevo balance
                        lblBalance.setText(this.account.getBalance().toString());
                        lblCreditLine.setText(this.account.getCreditLine().toString());
                        txtAmount.setText("");
                        //Cargamos los movimientos
                        loadMovements();
                        LOGGER.info("Movement created");
                        buttonEnable();
                        btnDelete.setDisable(false);
                    } catch (Exception e) {
                        handlelblError("The movement cant be created");
                    }
                } else {
                    alert.close();
                }
            });
        } catch (NumberFormatException e) {
            handlelblError("Invalid amount format");
        } catch (Exception e) {
            handlelblError(e.getMessage());
        }
    }

    public void handlelblError(String message) {
        lblError.setText(message);
    }

    public void handleAlert(String message) {

    }

    public Stage getStage() {
        return this.stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Account getAccount() {
        return this.account;
    }

    private void handleWindowClose(WindowEvent event) {
        try {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Exit application?", yes, no);
            if (a.showAndWait().get() == yes) {
                System.exit(0);
            } else {
                event.consume();
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }

    private void showCustomerHelp(String source) {
        try {
            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            webView.getEngine().load(getClass().getResource(source).toExternalForm());

            Stage helpStage = new Stage();
            helpStage.setTitle("Help movement");
            helpStage.setScene(new Scene(new javafx.scene.layout.StackPane(webView), 800, 600));
            helpStage.show();
        } catch (Exception e) {
            handlelblError("The help file could not be loaded");
        }
    }
    //Implementados los Override

    @Override
    public void onCreate() {
        handleBtnCreate(null);
        LOGGER.info("Movimiento creado");

    }

    @Override
    public void onRefresh() {
        loadMovements();
        handlelblError("Tabla refrescada");
        LOGGER.info("Tabla refrescada");
    }

    @Override
    public void onUpdate() {
        handlelblError("No se puede hacer update con los movimientos");
    }

    @Override
    public void onDelete() {
        handleBtnDelete(null);
        LOGGER.info("Movimiento borrado");

    }
}
