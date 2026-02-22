package crud_project.ui.controller;

// Imports.
import crud_project.logic.AccountRESTClient;
import crud_project.model.Account;
import crud_project.model.AccountType;
import crud_project.model.Customer;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.converter.DoubleStringConverter;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericType;

/**
 * Controlador para la gestión de cuentas bancarias. Maneja la visualización,
 * creación, edición y borrado de cuentas en una TableView.
 *
 * * @author Aitor Jury Rodríguez. 1º DAM.
 * @todo (SOLUCIONADO ) @fixme Hacer que la siguiente clase implemente las
 * interfaces Initializable y MenuActionsHandler para que al pulsar en las
 * acciones CRUD del menú Actions se ejecuten los métodos manejadores
 * correspondientes a la vista que incluye el menú. El métod o initialize debe
 * llamar a setMenuActionsHandler() para establecer que este controlador es el
 * manejador de acciones del menú.
 */
public class AccountsController implements Initializable, MenuActionsHandler {

    // Logger para el seguimiento de eventos y errores en consola.
    private static final Logger LOGGER = Logger.getLogger("crud_project.ui");

    // Componentes de la interfaz definidos en el FXML.
    @FXML
    private TableView<Account> tableAccounts;
    @FXML
    private TableColumn<Account, Long> colId;
    @FXML
    private TableColumn<Account, String> colDescription;
    @FXML
    private TableColumn<Account, AccountType> colType;
    @FXML
    private TableColumn<Account, Double> colBalance, colCreditLine, colBeginBalance;
    @FXML
    private TableColumn<Account, Date> colTimestamp;
    @FXML
    private Button btnRefresh, btnLogOut, btnViewMovements, btnDeleteAccount, btnCancelAccount;
    @FXML
    private ToggleButton btnAddAccount;
    @FXML
    private Label lblMessage;
    /**
     * Controlador del menú superior JavaFX asigna automáticamente el campo
     * topMenuController cuando usas fx:id="menuBar".
     */
    @FXML
    private MenuBarController menuBarController;

    // Lista observable que sincroniza los datos con la tabla.
    private final ObservableList<Account> accountsData = FXCollections.observableArrayList();
    private Stage stage;
    private Customer loggedCustomer;
    private final AccountRESTClient restClient = new AccountRESTClient();
    private Account creatingAccount;

    // Tipos de botones para diálogos de confirmación.
    private final ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private final ButtonType no = new ButtonType("No", ButtonBar.ButtonData.NO);

    /**
     * Inicializa la ventana y configura los componentes.
     *
     * @param root Nodo raíz de la vista.
     */
    public void init(Parent root) {
        try {
            this.stage = new Stage();
            this.stage.setScene(new Scene(root));
            this.stage.setTitle("My Accounts");
            this.stage.setResizable(false);
            // Gestión del cierre de ventana.
            this.stage.setOnCloseRequest(this::handleWindowClose);

            if (menuBarController != null) {
                menuBarController.init(this.stage);

                menuBarController.fxMenuSignOut.setOnAction(this::handleLogOut);

                menuBarController.fxMenuContent.setOnAction(e -> {
                    showCustomHelp("/crud_project/ui/res/helpAccount.html");
                });
            }

            // Configuración inicial de la tabla y carga de datos.
            setupTable();
            tableAccounts.setItems(accountsData);

            if (loggedCustomer != null) {
                loadAccountsData();
            }

            // Asignación de acciones a botones.
            btnAddAccount.setOnAction(this::handleAddAccount);
            btnCancelAccount.setOnAction(this::handleCancelAccount);
            btnRefresh.setOnAction(e -> {
                loadAccountsData();
                showSuccess("Data refreshed from server.");
            });
            btnLogOut.setOnAction(this::handleLogOut);
            btnViewMovements.setOnAction(this::handleViewMovements);
            btnDeleteAccount.setOnAction(this::handleDeleteAccount);

            this.stage.show();
        } catch (WebApplicationException e) {
            showError("Server error during init: " + e.getMessage());
        } catch (Exception e) {
            showError("Initialization Error: " + e.getMessage());
        }
    }

    /**
     * Configura las columnas de la tabla, sus factorías de celdas y eventos de
     * edición.
     */
    private void setupTable() {
        try {
            tableAccounts.setEditable(true);

            // Vinculación de columnas con atributos del modelo Account.
            colId.setCellValueFactory(new PropertyValueFactory<>("id"));

            // Configuración de columna Descripción.
            colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
            colDescription.setCellFactory(TextFieldTableCell.forTableColumn());
            colDescription.setEditable(true);
            colDescription.setOnEditStart(event -> {
                try {
                    // Bloqueo si se intenta editar otra fila durante una creación.
                    if (btnAddAccount.isSelected() && event.getRowValue() != creatingAccount) {
                        cancelEdit("Finish creating the new account first.");
                    }
                } catch (Exception e) {
                    showWarning("Edit start error.");
                }
            });
            colDescription.setOnEditCommit(this::handleDescriptionEdit);

            // Configuración de columna Tipo (Solo editable en creación).
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colType.setCellFactory(ChoiceBoxTableCell.forTableColumn(AccountType.values()));
            colType.setEditable(true);
            colType.setOnEditStart(event -> {
                try {
                    if (!btnAddAccount.isSelected() || event.getRowValue() != creatingAccount) {
                        cancelEdit("Account type cannot be modified for existing accounts.");
                    }
                } catch (Exception e) {
                    showWarning("Type edit error.");
                }
            });
            colType.setOnEditCommit(this::handleTypeEdit);

            // Configuración de columna Balance (No editable).
            colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
            colBalance.setEditable(false);

            // Configuración de columna Credit Line (Editable si es cuenta CREDIT).
            colCreditLine.setCellValueFactory(new PropertyValueFactory<>("creditLine"));
            colCreditLine.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            colCreditLine.setEditable(true);
            colCreditLine.setOnEditStart(event -> {
                try {
                    Account a = event.getRowValue();
                    // Validar si estamos en creación o si la cuenta existente es de crédito.
                    if (btnAddAccount.isSelected() && a != creatingAccount) {
                        cancelEdit("Finish creating the new account first.");
                    } else if (a.getType() != AccountType.CREDIT) {
                        cancelEdit("Credit line only applicable to CREDIT accounts.");
                    }
                } catch (Exception e) {
                    showWarning("Credit line edit error.");
                }
            });
            colCreditLine.setOnEditCommit(this::handleCreditLineEdit);

            // Configuración de columna Begin Balance (Solo editable en creación).
            colBeginBalance.setCellValueFactory(new PropertyValueFactory<>("beginBalance"));
            colBeginBalance.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
            colBeginBalance.setEditable(true);
            colBeginBalance.setOnEditStart(event -> {
                try {
                    if (!btnAddAccount.isSelected() || event.getRowValue() != creatingAccount) {
                        cancelEdit("Initial balance cannot be modified.");
                    }
                } catch (Exception e) {
                    showWarning("Initial balance edit error.");
                }
            });
            colBeginBalance.setOnEditCommit(this::handleBeginBalanceEdit);

            colTimestamp.setCellValueFactory(new PropertyValueFactory<>("beginBalanceTimestamp"));

            setupContextMenu();
        } catch (Exception e) {
            showError("Table setup error: " + e.getMessage());
        }
    }

    /**
     * Procesa la edición de la descripción y guarda cambios si procede.
     */
    private void handleDescriptionEdit(TableColumn.CellEditEvent<Account, String> event) {
        try {
            Account a = event.getRowValue();
            String val = event.getNewValue();
            if (btnAddAccount.isSelected() && a != creatingAccount) {
                tableAccounts.refresh();
                return;
            }
            if (val == null || val.trim().isEmpty()) {
                showWarning("Description is obligatory.");
                tableAccounts.refresh();
            } else {
                a.setDescription(val);
                showSuccess("Description updated.");
                if (!btnAddAccount.isSelected()) {
                    saveOrUpdate(a);
                }
            }
        } catch (NoSuchElementException e) {
            showWarning("Item not found.");
        } catch (Exception e) {
            showWarning("Update error: " + e.getMessage());
        } finally {
            tableAccounts.refresh();
        }
    }

    /**
     * Maneja el cambio de tipo de cuenta durante la creación.
     */
    private void handleTypeEdit(TableColumn.CellEditEvent<Account, AccountType> event) {
        try {
            Account a = event.getRowValue();
            if (!btnAddAccount.isSelected()) {
                cancelEdit("Account type is immutable.");
                return;
            }
            a.setType(event.getNewValue());
            // Si cambia a STANDARD, el límite de crédito debe ser 0.
            if (a.getType() == AccountType.STANDARD) {
                a.setCreditLine(0.0);
            }
            updateBalance(a);
            tableAccounts.refresh();
        } catch (Exception e) {
            showWarning("Type change failed.");
            tableAccounts.refresh();
        }
    }

    /**
     * Procesa la edición de Credit Line tanto en creación como en cuentas
     * existentes CREDIT.
     */
    private void handleCreditLineEdit(TableColumn.CellEditEvent<Account, Double> event) {
        try {
            Account a = event.getRowValue();
            Double val = event.getNewValue();
            if (btnAddAccount.isSelected() && a != creatingAccount) {
                tableAccounts.refresh();
                return;
            }
            if (val == null || val < 0) {
                showWarning("Credit Line must be 0 or positive.");
                tableAccounts.refresh();
            } else {
                a.setCreditLine(val);
                updateBalance(a);
                showSuccess("Credit line updated.");
                if (!btnAddAccount.isSelected()) {
                    saveOrUpdate(a);
                }
            }
        } catch (NumberFormatException e) {
            showWarning("Invalid number format.");
        } catch (Exception e) {
            showWarning("Credit line update error.");
        } finally {
            tableAccounts.refresh();
        }
    }

    /**
     * Maneja la edición del balance inicial en la nueva cuenta.
     */
    private void handleBeginBalanceEdit(TableColumn.CellEditEvent<Account, Double> event) {
        try {
            Account a = event.getRowValue();
            Double val = event.getNewValue();
            if (val == null || val < 0) {
                showWarning("Balance cannot be negative.");
                tableAccounts.refresh();
            } else {
                a.setBeginBalance(val);
                updateBalance(a);
                showSuccess("Begin balance updated.");
            }
        } catch (Exception e) {
            showWarning("Balance update error.");
            tableAccounts.refresh();
        }
    }

    /**
     * Inicia el proceso de creación de una cuenta o confirma la persistencia de
     * la misma.
     */
    private void handleAddAccount(ActionEvent event) {
        try {
            if (btnAddAccount.isSelected()) {
                // Modo creación activado.
                btnAddAccount.setText("Confirm");
                btnCancelAccount.setDisable(false);
                setButtonsCreating(true);

                creatingAccount = new Account();
                creatingAccount.setId(generateUniqueId());
                Set<Customer> customer = new HashSet<>();
                customer.add(loggedCustomer);
                creatingAccount.setCustomers(customer);
                creatingAccount.setBeginBalanceTimestamp(new Date());
                creatingAccount.setDescription("");
                creatingAccount.setType(AccountType.STANDARD);
                creatingAccount.setBalance(0.0);
                creatingAccount.setCreditLine(0.0);
                creatingAccount.setBeginBalance(0.0);

                accountsData.add(creatingAccount);
                tableAccounts.refresh();

                // Cálculo de posición y enfoque automático en la nueva fila.
                int i = accountsData.indexOf(creatingAccount);
                tableAccounts.getSelectionModel().select(i);
                tableAccounts.scrollTo(i);
                tableAccounts.requestFocus();
                tableAccounts.edit(i, colDescription);
            } else {
                // Confirmación de creación.
                if (creatingAccount.getDescription().trim().isEmpty()) {
                    showWarning("Description is obligatory.");
                    btnAddAccount.setSelected(true);
                } else {
                    restClient.createAccount_XML(creatingAccount);
                    creatingAccount = null;
                    finishCreation("Account created.");
                    event.consume();
                }
            }
        } catch (WebApplicationException e) {
            showError("Server error: " + e.getResponse().getStatus());
            btnAddAccount.setSelected(true);
        } catch (ProcessingException e) {
            showError("Network connection error.");
            btnAddAccount.setSelected(true);
        } catch (Exception e) {
            showError("Creation failed: " + e.getMessage());
            btnAddAccount.setSelected(true);
        }
    }

    /**
     * Cancela la creación de la cuenta actual y limpia la interfaz.
     */
    private void handleCancelAccount(ActionEvent event) {
        try {
            if (creatingAccount != null) {
                accountsData.remove(creatingAccount);
            }
            creatingAccount = null;
            finishCreation("Action Cancelled");
        } catch (Exception e) {
            showError("Cancel error: " + e.getMessage());
        }
    }

    /**
     * Cancela una edición activa en la tabla y muestra un aviso.
     */
    private void cancelEdit(String message) {
        try {
            tableAccounts.edit(-1, null);
            showWarning(message);
            tableAccounts.refresh();
        } catch (Exception e) {
            LOGGER.severe("Error cancelling edit: " + e.getMessage());
        }
    }

    /**
     * Restablece el estado de los botones y recarga datos tras una creación o
     * cancelación.
     */
    private void finishCreation(String message) {
        try {
            btnAddAccount.setText("Create Account");
            btnAddAccount.setSelected(false);
            btnCancelAccount.setDisable(true);
            setButtonsCreating(false);
            loadAccountsData();
            showSuccess(message);
        } catch (Exception e) {
            showError("Finalization error: " + e.getMessage());
        }
    }

    /**
     * Recupera las cuentas del cliente logueado desde el servicio REST.
     */
    private void loadAccountsData() {
        try {
            List<Account> accounts = restClient.findAccountsByCustomerId_XML(
                    new GenericType<List<Account>>() {
            },
                    loggedCustomer.getId().toString()
            );
            accountsData.setAll(accounts);
            tableAccounts.refresh();
        } catch (ProcessingException e) {
            showWarning("Network error: Unable to load data.");
        } catch (WebApplicationException e) {
            showWarning("Server error: " + e.getResponse().getStatus());
        } catch (Exception e) {
            showWarning("Data sync failed.");
        }
    }

    /**
     * Actualiza una cuenta existente en el servidor.
     */
    private void saveOrUpdate(Account a) {
        try {
            restClient.updateAccount_XML(a);
            showSuccess("Changes saved.");
        } catch (WebApplicationException e) {
            showError("Server update failed: " + e.getMessage());
            loadAccountsData();
        } catch (Exception e) {
            showError("Save error: " + e.getMessage());
            loadAccountsData();
        }
    }

    /**
     * Recalcula el balance de la cuenta en base al saldo inicial y línea de
     * crédito.
     */
    private void updateBalance(Account a) {
        try {
            if (btnAddAccount.isSelected() && a == creatingAccount) {
                a.setBalance(a.getBeginBalance());
            } else {
                loadAccountsData();
            }
        } catch (Exception e) {
            LOGGER.warning("Balance calculation error" + e.getMessage());
        }
    }

    /**
     * Habilita o deshabilita botones de navegación durante el proceso de
     * creación.
     */
    private void setButtonsCreating(boolean creating) {
        try {
            btnRefresh.setDisable(creating);
            btnLogOut.setDisable(creating);
            btnViewMovements.setDisable(creating);
            btnDeleteAccount.setDisable(creating);
        } catch (Exception e) {
            LOGGER.warning("Button state error");
        }
    }

    /**
     * Maneja la confirmación de salida de la aplicación.
     */
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

    /**
     * Cierra la sesión y redirige a la ventana de Sign In.
     */
    private void handleLogOut(ActionEvent event) {
        try {
            Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Log out?", yes, no);
            if (a.showAndWait().get() == yes) {
                restClient.close();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/view/SignIn.fxml"));
                Parent root = loader.load();
                SignInController sic = loader.getController();
                Stage signInStage = new Stage();
                sic.initStage(signInStage, root);
                this.stage.close();
            }
        } catch (Exception e) {
            showError("Logout failed: " + e.getMessage());
        }
    }

    /**
     * Solicita confirmación y borra la cuenta seleccionada.
     */
    private void handleDeleteAccount(ActionEvent event) {
        try {
            Account a = tableAccounts.getSelectionModel().getSelectedItem();
            if (a == null) {
                showWarning("Please select an account to delete.");
                return;
            }
            if (a.getMovements() != null && !a.getMovements().isEmpty()) {
                showWarning("Cannot delete account with existing movements.");
                return;
            }

            Alert al = new Alert(Alert.AlertType.CONFIRMATION, "Delete account: " + a.getDescription() + "?", yes, no);
            if (al.showAndWait().get() == yes) {
                restClient.removeAccount(a.getId().toString());
                loadAccountsData();
                showSuccess("Account deleted.");
            }
        } catch (WebApplicationException e) {
            showError("Server denied deletion: " + e.getMessage());
        } catch (Exception e) {
            showError("Delete failed: " + e.getMessage());
        }
    }

    /**
     * Navega a la ventana de movimientos de la cuenta seleccionada.
     */
    @FXML
    private void handleViewMovements(ActionEvent event) {
        try {
            Account a = tableAccounts.getSelectionModel().getSelectedItem();
            if (a == null) {
                showWarning("Please select an account to view movements.");
                return;
            }
            // Carga del controlador de movimientos.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/crud_project/ui/view/Movement.fxml"));
            Parent root = loader.load();

            MovementController mc = loader.getController();
            mc.setAccount(a);
            mc.setCustomer(loggedCustomer);
            Stage movementsStage = new Stage();
            mc.setStage(movementsStage);
            mc.init(root);

            this.stage.close();
        } catch (Exception e) {
            showError("Navigation Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Genera un identificador único de 10 dígitos para nuevas cuentas.
     */
    private Long generateUniqueId() {
        try {
            Random random = new Random();
            Long newId;
            boolean exists;
            do {
                newId = 1000000000L + (long) (random.nextDouble() * 8999999999L);
                exists = false;
                for (Account a : accountsData) {
                    if (a.getId() != null && a.getId().equals(newId)) {
                        exists = true;
                        break;
                    }
                }
            } while (exists);
            return newId;
        } catch (Exception e) {
            return System.currentTimeMillis();
        }
    }

    /**
     * Configuración del menú contextual para la tabla.
     */
    private void setupContextMenu() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem itemDelete = new MenuItem("Delete Selected Account");
        MenuItem itemView = new MenuItem("View Account Movements");
        MenuItem itemHelp = new MenuItem("Get Help");

        itemDelete.setOnAction(this::handleDeleteAccount);
        itemView.setOnAction(this::handleViewMovements);
        itemHelp.setOnAction(e -> {
            showCustomHelp("/crud_project/ui/res/helpAccount.html");
        });

        contextMenu.getItems().addAll(itemDelete, itemView, new SeparatorMenuItem(), itemHelp);
        tableAccounts.setContextMenu(contextMenu);
    }

    /**
     * Muestra la ayuda HTML sensible al contexto.
     */
    private void showCustomHelp(String resourcePath) {
        try {
            javafx.scene.web.WebView webView = new javafx.scene.web.WebView();
            webView.getEngine().load(getClass().getResource(resourcePath).toExternalForm());

            Stage helpStage = new Stage();
            helpStage.setTitle("Help: Managing Accounts");
            helpStage.setScene(new Scene(new javafx.scene.layout.StackPane(webView), 800, 600));
            helpStage.show();
        } catch (Exception ex) {
            showError("The help file could not be loaded.");
        }
    }

    // Métodos auxiliares para la gestión de mensajes en la interfaz.
    private void showWarning(String msg) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String msg) {
        lblMessage.setText(msg);
        lblMessage.setStyle("-fx-text-fill: green;");
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait();
    }

    public void setCustomer(Customer c) {
        this.loggedCustomer = c;
    }

    public Stage getStage() {
        return this.stage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menuBarController.setMenuActionsHandler(this);
    }

    @Override
    public void onCreate() {
        btnAddAccount.fire();
    }

    @Override
    public void onRefresh() {
        loadAccountsData();
        showSuccess("Data refreshed from server.");
    }

    @Override
    public void onUpdate() {
        showError("Editable table, click on an editable cell");
    }

    @Override
    public void onDelete() {
        handleDeleteAccount(null);
    }
}
