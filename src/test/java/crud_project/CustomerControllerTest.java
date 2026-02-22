package crud_project.ui.controller;

import crud_project.AppCRUD;
import crud_project.logic.AccountRESTClient;
import crud_project.model.Account;
import crud_project.model.Customer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;
import static org.testfx.api.FxAssert.verifyThat;

import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;

import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static org.testfx.matcher.base.NodeMatchers.*;
import static org.testfx.matcher.control.ButtonMatchers.isDefaultButton;

/**
 *
 * @author juan
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CustomerControllerTest extends ApplicationTest {
    private TableView<Customer> table;
    private Button btnDelete;
    private Button btnAdd;
    private Button btnExit;


    @Override
    public void start(Stage stage) throws Exception {
        //Method to start the application
        //new AppCRUD().start(stage);
        new AppCRUD().start(stage);

    }

    @Before
    public void test_init_window() {
        verifyThat("#txtEmail", isVisible());
        clickOn("#txtEmail");
        write("admin");
        clickOn("#txtPassword");
        write("admin");
        clickOn("#btnSignIn");
        verifyThat("#mainPane", isVisible());

        //var needed to test
        table = lookup("#fxTableView").queryTableView();
        btnDelete = lookup("#fxBtnDelete").queryButton();
        btnAdd = lookup("#fxBtnNewCustomer").queryButton();
        btnExit = lookup("#fxBtnExit").queryButton();


    }

    @After
    public void close_window() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }

    @Test
    public void test_D_delete_customer_success() {
        //Instaciar el restClient para verificar las cuentas
        AccountRESTClient accClient = new AccountRESTClient();

        // Buscar al usuario que no tiene cuentas
        int rowIndex = -1;
        String userToDeleteName = "";
        List<Customer> customers = table.getItems();

        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            // Ignorar admin
            if (!c.getFirstName().equalsIgnoreCase("admin")) {
                try {
                    Set<Account> accounts = accClient.findAccountsByCustomerId_XML(
                            new GenericType<Set<Account>>() {
                            },
                            c.getId().toString()
                    );

                    // Si NO tiene cuentas, es el cliente para borrar
                    if (accounts == null || accounts.isEmpty()) {
                        rowIndex = i;
                        userToDeleteName = c.getFirstName();
                        break; // Solo hacemos break si entramos en este IF
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }

        // Verificar que encontramos a Paco
        assertNotEquals("No se encontró el usuario creado para borrar", -1, rowIndex);

        verifyThat(btnDelete, isDisabled());
        int rowsCount = table.getItems().size();

        // Seleccionamos la fila de Paco
        Node row = lookup(".table-row-cell").nth(rowIndex).query();
        clickOn(row);

        verifyThat(btnDelete, isEnabled());
        clickOn(btnDelete);

        // Ahora el alert debería ser el correcto
        verifyThat("Deleting user: " + userToDeleteName, isVisible());

        clickOn("Sí"); // O clickOn("Yes") dependiendo de tu idioma
        assertEquals("The row has not been deleted", rowsCount - 1, table.getItems().size());

    }

    @Test
    public void test_A_add_customer_success() {

        Customer customer = new Customer(
                new Random().nextLong(),
                "Paco",
                "Perez",
                "M",
                "Avenida America",
                "Madrid",
                "Madrid",
                28052,
                615487796L,
                "name@" + System.currentTimeMillis() + ".com",
                "clave$%&"
        );


        String[] datos = {
                customer.getFirstName(),
                customer.getLastName(),
                customer.getMiddleInitial(),
                customer.getEmail(),
                customer.getPassword(),
                customer.getPhone().toString(),
                customer.getStreet(),
                customer.getCity(),
                customer.getState(),
                customer.getZip().toString(),

        };
        int cellIndex = 1;
        int rowsCount = table.getItems().size();
        clickOn(isDefaultButton());
        for (String dato : datos) {
            Node cell = lookup(".table-cell").nth(cellIndex).query();
            assertNotNull(cell);
            clickOn(cell);
            //Escribe el dato en el orden de la lista y borra lo que haya dentro del campo antes de escribir
            write(dato).push(KeyCode.ENTER);
            cellIndex++;
        }
        assertEquals("The row has not been added!!!", rowsCount + 1, table.getItems().size());


    }

    @Test
    public void test_B_update_customer_success() {

        List<String> datos = new ArrayList<>(9);
        datos.add("NameTest");
        datos.add("LastNameTest");
        datos.add("T");
        datos.add("email@Test.com");
        datos.add("claveTest");
        datos.add("615487796");
        datos.add("StreetTest");
        datos.add("CityTest");
        datos.add("StateTest");
        datos.add("12345");

        verifyThat("#fxBtnDelete", isDisabled());
        for (int i = 0; i < datos.size(); i++) {

            Node cell = lookup(".table-cell").nth(i + 1).query();

            doubleClickOn(cell);
            //Pulsar el Ctrl+A para seleccionar toda la celda
            push(KeyCode.SHORTCUT, KeyCode.A);
            //Pulsa el espacio para borrarlo
            push(KeyCode.SPACE);
            //Se escribe el dato
            write(datos.get(i)).push(KeyCode.ENTER);
        }
        Customer c = table.getItems().get(0);

        // Verificaciones
        assertEquals(datos.get(0), c.getFirstName());
        assertEquals(datos.get(1), c.getLastName());
        assertEquals(datos.get(2), c.getMiddleInitial());
        assertEquals(datos.get(3), c.getEmail());
        assertEquals(datos.get(4), c.getPassword());
        assertEquals(Long.valueOf(datos.get(5)), c.getPhone());
        assertEquals(datos.get(6), c.getStreet());
        assertEquals(datos.get(7), c.getCity());
        assertEquals(datos.get(8), c.getState());
        assertEquals(Integer.valueOf(datos.get(9)), c.getZip());

    }

    @Test
    public void test_C_delete_customer_fail() {

        //Instaciar el restClient para verificar las cuentas
        AccountRESTClient accClient = new AccountRESTClient();

        // Buscar al usuario que tiene cuentas
        int rowIndex = -1;
        String userToDeleteName = "";
        List<Customer> customers = table.getItems();

        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);

            if (!c.getFirstName().equalsIgnoreCase("admin")) {
                try {
                    Set<Account> accounts = accClient.findAccountsByCustomerId_XML(
                            new GenericType<Set<Account>>() {
                            },
                            c.getId().toString()
                    );

                    //Busca que el usuario tenga cuentas
                    if (accounts != null && !accounts.isEmpty()) {
                        rowIndex = i;
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error consultando cuentas: " + e.getMessage());
                }
            }
        }
        verifyThat(btnDelete, isDisabled());
        int rowsCount = table.getItems().size();
        assertNotEquals("Table has no data: Cannot test", 0, rowsCount);
        Node row = lookup(".table-row-cell").nth(rowIndex).query();

        assertNotNull("Row is null: table has not that row. ", row);
        clickOn(row);


        verifyThat(btnDelete, isEnabled());
        clickOn(btnDelete);

        //Verificar que aparece el alert
        Node dialogPane = lookup(".dialog-pane").query();
        verifyThat(dialogPane, isVisible());

        //Verificar que es alert de tipo error a traves de la etiqueta css
        assertTrue("The alert is not an error", dialogPane.getStyleClass().contains("error"));

        //Verificar el texto de error es que queremos
        verifyThat("The user cannot be deleted because they have associated accounts or data.", isVisible());

        //Pulsar el boton de aceptar
        Node button = from(dialogPane).lookup(".button").query();
        clickOn(button);

        //Presiona la tecla Ctrl
        press(KeyCode.CONTROL);
        //Presiona nuevamente en la fila con el Ctrl pulsado
        clickOn(row);
        //Libera la tecla Ctrl
        release(KeyCode.CONTROL);
        //Verificar que se ha deshabilitado el boton de eliminar
        verifyThat("#fxBtnDelete", isDisabled());

    }
}