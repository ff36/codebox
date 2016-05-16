package co.ff36.controller;

import co.ff36.pojo.Archive;
import co.ff36.pojo.Traffic;
import co.ff36.pojo.TrafficTasks;
import co.ff36.util.S3Util;
import co.ff36.util.ZipUtil;
import com.amazonaws.services.s3.transfer.Download;
import com.amazonaws.services.s3.transfer.Upload;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by tarka on 13/05/2016.
 */
public class ArchiveController implements Initializable {

    @FXML
    private Label toArchive;
    @FXML
    private TextField company;
    @FXML
    private TextField account;
    @FXML
    private TextField order;
    @FXML
    private TextField extra;
    @FXML
    private CheckBox delete;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setToArchive(File file) {
        toArchive.setText(file.getAbsolutePath());
    }

    public void startArchive(ActionEvent actionEvent) {

        // Check the fields are complete
        boolean valid = true;
        if (company.getText().isEmpty()) {
            valid = false;
        }
        if (account.getText().isEmpty()) {
            valid = false;
        }
        if (order.getText().isEmpty()) {
            valid = false;
        }

        if (valid) {

            Task<Void> task = new Task<Void>() {
                @Override
                public Void call() throws InterruptedException {

                    try {

                        String tempDir = System.getProperty("java.io.tmpdir");
                        ZipUtil.zipFolder(toArchive.getText(), tempDir + ".zip");

                        String uploadName = Archive.toKey(
                                company.getText(),
                                account.getText(),
                                order.getText(),
                                extra.getText());

                        Upload upload = new S3Util().Upload(tempDir + ".zip", uploadName);;
                        Traffic traffic = new Traffic(Traffic.Type.Upload, upload);
                        TrafficTasks tasks = TrafficTasks.getInstance();
                        tasks.addTraffic(traffic);

                        if (delete.isSelected()) FileUtils.deleteDirectory(new File(toArchive.getText()));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();

            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        }
    }

}
