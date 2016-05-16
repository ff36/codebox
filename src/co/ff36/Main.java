package co.ff36;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());
        Parent root = FXMLLoader.load(getClass().getResource("fxml/main.fxml"), bundle);

        primaryStage.setTitle(bundle.getString("app_title"));
        Scene scene = new Scene(root, 1800, 1000);
        scene.getStylesheets().add(getClass().getResource("css/style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
