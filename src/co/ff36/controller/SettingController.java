package co.ff36.controller;

import co.ff36.util.SettingUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.*;

/**
 * Created by tarka on 15/05/2016.
 */
public class SettingController implements Initializable {

    @FXML
    private TextField awsBucket;
    @FXML
    private TextField awsPublicKey;
    @FXML
    private PasswordField awsPrivateKey;
    @FXML
    private TextField downloadFile;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        SettingUtil settingUtil = new SettingUtil();
        Map<String, String> settings = settingUtil.load();

        awsBucket.setText(settings.get(SettingUtil.AWS_BUCKET_KEY));
        awsPublicKey.setText(settings.get(SettingUtil.AWS_PUBLIC_KEY));
        awsPrivateKey.setText(settings.get(SettingUtil.AWS_PRIVATE_KEY));
        downloadFile.setText(settings.get(SettingUtil.DOWNLOAD_FILE_KEY));
    }

    public void saveSetting(ActionEvent actionEvent) {

        SettingUtil settingUtil = new SettingUtil();
        Map<String, String> settings = new HashMap<>();

        settings.put(SettingUtil.AWS_BUCKET_KEY, awsBucket.getText());
        settings.put(SettingUtil.AWS_PUBLIC_KEY, awsPublicKey.getText());
        settings.put(SettingUtil.AWS_PRIVATE_KEY, awsPrivateKey.getText());
        settings.put(SettingUtil.DOWNLOAD_FILE_KEY,downloadFile.getText() );

        settingUtil.save(settings);

        ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
    }
}
