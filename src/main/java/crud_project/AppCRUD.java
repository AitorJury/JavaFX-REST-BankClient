/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crud_project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import crud_project.ui.controller.SignInController;

/**
 *
 * @author juan
 */
public class AppCRUD extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader =new FXMLLoader(getClass().getResource("ui/view/SignIn.fxml"));
        Parent root = loader.load();

        SignInController controller = loader.getController();

        controller.initStage(stage,root);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
