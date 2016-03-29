package co.ff36;

import co.ff36.archive.Archive;
import co.ff36.util.S3Util;
import co.ff36.util.ZipUtil;
import javafx.application.Application;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        // Setup the main canvas
        primaryStage.setTitle("Archiver");
        Group root = new Group();
        Scene scene = new Scene(root, 1000, 800);

        // Create a tab pane for both tabs to be added
        TabPane tabPane = new TabPane();
        BorderPane borderPane = new BorderPane();

        // Create a grid to hold the archive form
        GridPane formGrid = new GridPane();
        formGrid.setAlignment(Pos.CENTER);
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(0, 0, 0, 0));

        // Create the archive form
        Text sceneTitle = new Text("   CREATE NEW ARCHIVE");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        formGrid.add(sceneTitle, 0, 0, 2, 1);

        TextField companyTextField = new TextField();
        companyTextField.setPromptText("Company Name (required)");
        companyTextField.setMinWidth(250);
        formGrid.add(companyTextField, 0, 1);

        TextField accountTextField = new TextField();
        accountTextField.setPromptText("Account Name (required)");
        formGrid.add(accountTextField, 0, 2);

        TextField orderTextField = new TextField();
        orderTextField.setPromptText("Order Number (required)");
        formGrid.add(orderTextField, 0, 3);

        final Text actionTarget = new Text();
        formGrid.add(actionTarget, 0, 6);

        // Add the form to the tab
        Tab archiveTab = new Tab();
        archiveTab.setText("Archive");
        archiveTab.setClosable(false);
        HBox archiveBox = new HBox();
        archiveBox.getChildren().add(formGrid);
        archiveBox.setAlignment(Pos.CENTER);
        archiveTab.setContent(archiveBox);
        tabPane.getTabs().add(archiveTab);

        // Create a table view for the search
        TableView<Archive> table = new TableView<>();
        TableColumn companyCol = new TableColumn("Company Name");
        companyCol.setMinWidth(300);
        companyCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("companyName"));
        TableColumn accountCol = new TableColumn("Account Name");
        accountCol.setMinWidth(300);
        accountCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("accountName"));
        TableColumn orderCol = new TableColumn("Order Number");
        orderCol.setMinWidth(100);
        orderCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("orderNumber"));
        TableColumn dateCol = new TableColumn("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("archivedDate"));
        TableColumn sizeCol = new TableColumn("Size");
        sizeCol.setCellValueFactory(new PropertyValueFactory<Archive, String>("size"));

        // Double click event listener for table download
        table.setRowFactory( tv -> {
            TableRow<Archive> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Archive rowData = row.getItem();
                    try {
                        new S3Util().download(rowData.getKey());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            return row ;
        });

        // Wrap the filtered list into a sorted list and add it to the table
        GridPane searchGrid = new GridPane();
        table.setItems(createTable(searchGrid));
        table.getColumns().addAll(companyCol, accountCol, orderCol, dateCol, sizeCol);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setPrefHeight(740);

        // Register the search tab and add the table and search field
        Tab searchTab = new Tab();
        searchTab.setText("Search");
        searchTab.setClosable(false);
        VBox searchBox = new VBox();
        searchBox.getChildren().addAll(searchGrid, table);
        searchBox.setAlignment(Pos.TOP_LEFT);
        searchTab.setContent(searchBox);
        tabPane.getTabs().add(searchTab);

        // Add a drag and drop event listener for the archive folder drop
        archiveBox.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.MOVE);
                } else {
                    event.consume();
                }
            }
        });

        // Add a drag and drop event listener for the archive folder drop
        archiveBox.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    for (File file : db.getFiles()) {
                        final String srcPath = file.getAbsolutePath();
                        try {
                            // Check the fileds are complete
                            boolean valid = true;
                            if (companyTextField.getText().isEmpty()) {
                                actionTarget.setFill(Color.FIREBRICK);
                                actionTarget.setText("Company Name is required");
                                valid = false;
                            }
                            if (accountTextField.getText().isEmpty()) {
                                actionTarget.setFill(Color.FIREBRICK);
                                actionTarget.setText("Account Name is required");
                                valid = false;
                            }
                            if (orderTextField.getText().isEmpty()) {
                                actionTarget.setFill(Color.FIREBRICK);
                                actionTarget.setText("Company Name is required");
                                valid = false;
                            }

                            if (valid) {

                                companyTextField.setDisable(true);
                                accountTextField.setDisable(true);
                                orderTextField.setDisable(true);

                                actionTarget.setFill(Color.GREEN);
                                actionTarget.setText("Archiving...");

                                Task<Void> task = new Task<Void>() {
                                    @Override public Void call() throws InterruptedException {

                                        try {
                                            String tempDir = System.getProperty("java.io.tmpdir");
                                            ZipUtil.zipFolder(srcPath, tempDir + ".zip");

                                            String uploadName = Archive.toKey(companyTextField.getText(), accountTextField.getText(), orderTextField.getText());

                                            new S3Util().Upload(tempDir + ".zip",  uploadName );

                                            FileUtils.deleteDirectory(new File(srcPath));

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        return null;
                                    }
                                };

                                // java 8 construct, replace with java 7 code if using java 7.
                                task.setOnSucceeded(e -> {
                                    companyTextField.clear();
                                    accountTextField.clear();
                                    orderTextField.clear();
                                    companyTextField.setDisable(false);
                                    accountTextField.setDisable(false);
                                    orderTextField.setDisable(false);

                                    actionTarget.setText(null);

                                    try {
                                        table.setItems(createTable(searchGrid));
                                    } catch (IOException e1) {
                                        e1.printStackTrace();
                                    }
                                });

                                Thread thread = new Thread(task);
                                thread.setDaemon(true);
                                thread.start();

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });

        borderPane.prefHeightProperty().bind(scene.heightProperty());
        borderPane.prefWidthProperty().bind(scene.widthProperty());
        borderPane.setCenter(tabPane);
        root.getChildren().add(borderPane);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private FilteredList<Archive> createTable(GridPane searchGrid) throws IOException {
        // Wrap the ObservableList in a FilteredList (initially display all data).
        FilteredList<Archive> filteredData = new FilteredList<>(new S3Util().list(), p -> true);

        TextField filterField = new TextField();
        filterField.setMinWidth(1000);
        filterField.setPromptText("Search");
        searchGrid.add(filterField, 0, 1);

        // Set the filter Predicate whenever the filter changes.
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
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
    }

}
