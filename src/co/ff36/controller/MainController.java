package co.ff36.controller;

import co.ff36.Main;
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
import java.util.logging.*;

/**
 * Class dedicated to handling all functions relating to the main container pane of the application.
 *
 * Created by tarka on 11/05/2016.
 */
@SuppressWarnings("unused")
public class MainController implements Initializable {

    private static Logger LOG =  Logger.getLogger("codebox_logger");

    @FXML
    private MenuItem quit;
    @FXML
    private MenuItem about;
    @FXML
    private MenuItem setting;

    /**
     * Automatically invoked whenever the main container pane is initialized. This happens after the constructor.
     * @param location The URL of the invoking FXML file
     * @param resources The resource bundle being used.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        try {
            String log = System.getProperty("user.home") + "/.codebox/logging.log";
            Handler handler = new FileHandler(log);
            LOG.addHandler(handler);
            SimpleFormatter formatter = new SimpleFormatter();
            handler.setFormatter(formatter);
        } catch (IOException e) {
            e.printStackTrace();
        }

        quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCombination.SHORTCUT_DOWN));
        about.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHORTCUT_DOWN));
        setting.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));
    }

    /**
     * Main menu function to handle displaying the About window.
     * @param actionEvent The event used to set the new modal windows owner.
     */
    @FXML
    protected void displayAboutWindow(ActionEvent actionEvent)  {
        try {
            Stage stage = new Stage();
            ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());
            URL resource = Main.class.getResource("fxml/about.fxml");
            FXMLLoader loader = new FXMLLoader(resource, bundle);
            Parent root = loader.load();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(bundle.getString("about_modal_title"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());
            stage.show();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error opening about window", e);
        }
    }

    /**
     * Main menu function to handle displaying the Setting window.
     * @param actionEvent The event used to set the new modal windows owner.
     */
    @FXML
    protected void displaySettingWindow(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());
            URL resource = Main.class.getResource("fxml/setting.fxml");
            FXMLLoader loader = new FXMLLoader(resource, bundle);
            Parent root = loader.load();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(bundle.getString("setting_modal_title"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());
            stage.show();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Error opening setting window", e);
        }
    }

    /**
     * Main menu function to handle quiting the application.
     * @param actionEvent The event used to set the new modal windows owner.
     * @throws IOException
     */
    @FXML
    protected void quitApp(ActionEvent actionEvent) throws IOException {
        Platform.exit();
    }

}
