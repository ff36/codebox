package co.ff36;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The Application main entry class
 */
public class Main extends Application {

    /**
     * JavaFx main entry point
     * @param primaryStage Auto set
     * @throws Exception Standard
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());
        Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"), bundle);

        primaryStage.setTitle(bundle.getString("app_title"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main entry method
     * @param args Standard JVM app args
     */
    public static void main(String[] args) {
        launch(args);
    }

}
