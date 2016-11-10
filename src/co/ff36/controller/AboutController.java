package co.ff36.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Class dedicated to handling all functions relating to the about modal of the application.
 *
 * Created by tarka on 11/05/2016.
 */
public class AboutController implements Initializable {

    @FXML
    private Label author;
    @FXML
    private Label version;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
