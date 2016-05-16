package co.ff36.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    private MenuItem quit;
    @FXML
    private MenuItem about;
    @FXML
    private MenuItem setting;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        about.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        setting.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
    }

    @FXML
    protected void displayAboutWindow(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());
        URL resource = getClass().getResource("../fxml/about.fxml");
        FXMLLoader loader = new FXMLLoader(resource, bundle);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle(bundle.getString("about_modal_title"));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());
        stage.show();
    }

    @FXML
    protected void displaySettingWindow(ActionEvent actionEvent) throws IOException {
        Stage stage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());
        URL resource = getClass().getResource("../fxml/setting.fxml");
        FXMLLoader loader = new FXMLLoader(resource, bundle);
        Parent root = loader.load();
        stage.setScene(new Scene(root, 800, 600));
        stage.setTitle(bundle.getString("setting_modal_title"));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());
        stage.show();
    }

    @FXML
    protected void quitApp(ActionEvent actionEvent) throws IOException {
        Platform.exit();
    }

}
