package crud_project;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author cynthia
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import org.junit.FixMethodOrder;
import crud_project.AppCRUD;
import crud_project.model.Account;
import crud_project.model.Movement;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.runners.MethodSorters;
import static org.testfx.api.FxAssert.verifyThat;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import static org.testfx.matcher.base.NodeMatchers.isDisabled;
import static org.testfx.matcher.base.NodeMatchers.isEnabled;
import static org.testfx.matcher.base.NodeMatchers.isVisible;
import static org.testfx.matcher.control.ButtonMatchers.isDefaultButton;
import static org.testfx.matcher.control.LabeledMatchers.hasText;
import static org.testfx.matcher.control.ListViewMatchers.isEmpty;

/**
 *
 * @author cynthia
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MovementControllerTest extends ApplicationTest {

    private TableView table;
    private ComboBox comboType;

    @Override
    public void start(Stage stage) throws Exception {
        new AppCRUD().start(stage);
    }

    @Before
    public void testStart() {
        clickOn("#txtEmail");
        write("awallace@gmail.com");
        clickOn("#txtPassword");
        write("qwerty*9876");
        clickOn("#btnSignIn");
        //cambiar el id para el test 0
        //clickOn("6599097192");
        clickOn("#tableAccounts");
        Node row = lookup(".table-row-cell").nth(0).query();
        clickOn(row);
        clickOn("#btnViewMovements");
       

    }

    @After
    public void close_window() throws Exception {
        FxToolkit.hideStage();
        FxToolkit.cleanupStages();
    }
    /*
    @Test
    public void test0_ButtonCreatePaymentWithCredit() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write("100000");
        clickOn("#createMovement");
        clickOn("Yes");
        int numRowAfter = table.getItems().size();
        assertTrue("The movement cant be created", numRowAfter > numRowBefore);
    }
*/
    @Test
    public void test1_ButtonCreateDeposit() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write("100000");
        clickOn("#createMovement");
        clickOn("Yes");
        int numRowAfter = table.getItems().size();
        assertTrue("The movement cant be created", numRowAfter > numRowBefore);
    }
     @Test
    public void test2_ButtonCreatePayment() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write("100000");
        clickOn("#createMovement");
        clickOn("Yes");
        int numRowAfter = table.getItems().size();
        assertTrue("The movement cant be created", numRowAfter > numRowBefore);
    }

    @Test
    public void test2_DeleteMovement() {
        //Obtiene las filas iniciales
        TableView<Account> table = lookup("#tbMovement").queryTableView();
        int rowCountBefore = table.getItems().size();
        clickOn("#btnDelete");
        clickOn("Yes");
        //Miramos cuantas celdas hay despues
        int rowCountAfter = table.getItems().size();
        assertEquals(rowCountBefore - 1, rowCountAfter);
        verifyThat("#btnDelete", isDisabled());

    }

    @Test
    public void test4_ButtonBackAccount() {
        clickOn("#btnBack");
        clickOn("Yes");
        verifyThat("My Accounts Management", isVisible());
    }

    @Test
    public void test5_ButtonCreateNegativeAmountFailed() {
        TableView<Movement> table = lookup("#tbMovement").queryTableView();
        int numRowBefore = table.getItems().size();
        clickOn("#comboType");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#txtAmount");
        write("-100");
        clickOn("#createMovement");
        verifyThat("Amount cant be negative", isVisible());

    }

}
