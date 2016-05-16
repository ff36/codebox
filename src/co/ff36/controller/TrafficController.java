package co.ff36.controller;

import co.ff36.pojo.Traffic;
import co.ff36.pojo.TrafficTasks;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by tarka on 13/05/2016.
 */
public class TrafficController implements Initializable {

    @FXML
    private TableView<Traffic> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        load();
    }

    @SuppressWarnings("unchecked")
    public void load() {

        // Create a table view for the search
        TableColumn<Traffic, Traffic.Type> typeCol = new TableColumn();
        typeCol.setPrefWidth(25.0);
        typeCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getType()));
        typeCol.setCellFactory(new Callback<TableColumn<Traffic, Traffic.Type>,TableCell<Traffic, Traffic.Type>>(){
            @Override
            public TableCell<Traffic, Traffic.Type> call(TableColumn<Traffic, Traffic.Type> param) {
                return new TableCell<Traffic, Traffic.Type>(){
                    @Override
                    public void updateItem(Traffic.Type item, boolean empty) {
                        if(item != null){
                            HBox hbox = new HBox();
                            VBox vbox = new VBox();

                            Image img = null;
                            switch(item) {
                                case Upload: img = new Image(getClass().getResource("../img/upload.png").toExternalForm());
                                    break;
                                case Download: img = new Image(getClass().getResource("../img/download.png").toExternalForm());
                                    break;
                            }

                            ImageView imageview = new ImageView();
                            imageview.setImage(img);

                            hbox.getChildren().addAll(imageview, vbox);
                            setGraphic(hbox);
                        }
                    }
                };
            }

        });

        TableColumn<Traffic, String> dateCol = new TableColumn();
        dateCol.setPrefWidth(150.0);
        dateCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        TableColumn<Traffic, String> fileCol = new TableColumn();
        fileCol.setPrefWidth(900.0);
        fileCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getFile()));

        TableColumn<Traffic, Double> progressCol = new TableColumn();
        progressCol.setPrefWidth(300.0);
        progressCol.setCellValueFactory(new PropertyValueFactory<>("progress"));
        progressCol.setCellFactory(ProgressBarTableCell.<Traffic> forTableColumn());

        TableColumn<Traffic, String> statusCol = new TableColumn();
        statusCol.setPrefWidth(25.0);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("message"));
        statusCol.setCellFactory(new Callback<TableColumn<Traffic, String>,TableCell<Traffic, String>>(){
            @Override
            public TableCell<Traffic, String> call(TableColumn<Traffic, String> param) {
                return new TableCell<Traffic, String>(){

                    @Override
                    public void updateItem(String item, boolean empty) {
                        if(item != null){
                            HBox hbox = new HBox();
                            VBox vbox = new VBox();

                            Image img = null;
                            switch(item) {
                                case "Current": img = new Image(getClass().getResource("../img/progress.png").toExternalForm());
                                    break;
                                case "Success": img = new Image(getClass().getResource("../img/success.png").toExternalForm());
                                    break;
                                case "Fail": img = new Image(getClass().getResource("../img/cancel.png").toExternalForm());
                                    break;
                            }

                            ImageView imageview = new ImageView();
                            imageview.setImage(img);

                            hbox.getChildren().addAll(imageview, vbox);
                            setGraphic(hbox);
                        }
                    }
                };
            }

        });

        // Wrap the filtered list into a sorted list and add it to the table
        table.getItems().clear();
        table.setItems(TrafficTasks.getInstance().getTraffic());
        if (table.getColumns().isEmpty()) {
            table.getColumns().addAll(typeCol, dateCol, fileCol, progressCol, statusCol);
        }

        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    @FXML
    protected void stop() throws IOException {
        Traffic selectedItem = table.getSelectionModel().getSelectedItem();
        switch (selectedItem.getType()) {
            case Upload: selectedItem.getUpload().abort();
                selectedItem.abort();
                break;
            case Download: selectedItem.getDownload().abort();
                selectedItem.abort();
                break;
        }
    }

    @FXML
    protected void clear() {
        Traffic selectedItem = table.getSelectionModel().getSelectedItem();
        TrafficTasks.getInstance().deleteTraffic(selectedItem);
        load();
    }

    @FXML
    protected void clearDone() {
        TrafficTasks.getInstance().deleteDoneTraffic();
        load();
    }

}