package co.ff36.controller;

import co.ff36.pojo.Archive;
import co.ff36.pojo.Traffic;
import co.ff36.pojo.TrafficTasks;
import co.ff36.util.S3Util;
import co.ff36.util.ZipUtil;
import com.amazonaws.services.s3.transfer.Download;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by tarka on 11/05/2016.
 */
public class RemoteController implements Initializable {

    @FXML
    private TextField search;
    @FXML
    private TableView<Archive> table;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        load();
    }

    @FXML
    protected void remoteFileSelected(ActionEvent actionEvent) throws IOException {
        Archive selectedItem = table.getSelectionModel().getSelectedItem();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {

                try {
                    Download download = new S3Util().download(selectedItem.getKey());
                    Traffic traffic = new Traffic(Traffic.Type.Download, download);
                    TrafficTasks tasks = TrafficTasks.getInstance();
                    tasks.addTraffic(traffic);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    @SuppressWarnings("unchecked")
    public void load() {

        ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());

        // Create a table view for the search
        TableColumn companyCol = new TableColumn(bundle.getString("search_column_title_company"));
        companyCol.setMinWidth(200);
        companyCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("companyName"));

        TableColumn accountCol = new TableColumn(bundle.getString("search_column_title_account"));
        accountCol.setMinWidth(200);
        accountCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("accountName"));

        TableColumn orderCol = new TableColumn(bundle.getString("search_column_title_order"));
        orderCol.setMinWidth(110);
        orderCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("orderNumber"));

        TableColumn extraCol = new TableColumn(bundle.getString("search_column_title_extra"));
        extraCol.setMinWidth(300);
        extraCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("extra"));

        TableColumn dateCol = new TableColumn(bundle.getString("search_column_title_date"));
        dateCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("archivedDate"));

        TableColumn sizeCol = new TableColumn(bundle.getString("search_column_title_size"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("size"));

        // Wrap the filtered list into a sorted list and add it to the table
        GridPane searchGrid = new GridPane();
        table.getItems().clear();
        table.setItems(createTable(searchGrid));
        if (table.getColumns().isEmpty()) {
            table.getColumns().addAll(companyCol, accountCol, orderCol, extraCol, dateCol, sizeCol);
        }
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private FilteredList<Archive> createTable(GridPane searchGrid) {
        // Wrap the ObservableList in a FilteredList (initially display all data).
        try {
            FilteredList<Archive> filteredData = new FilteredList<>(new S3Util().list(), p -> true);

            // Set the filter Predicate whenever the filter changes.
            search.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(archive -> {
                    // If filter text is empty, display all persons.
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    // Compare first name and last name of every person with filter text.
                    String lowerCaseFilter = newValue.toLowerCase();

                    if (archive.getAccountName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches first name.
                    } else if (archive.getCompanyName().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches last name.
                    } else if (archive.getOrderNumber().toLowerCase().contains(lowerCaseFilter)) {
                        return true; // Filter matches last name.
                    }
                    return false; // Does not match.
                });
            });

            return filteredData;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
