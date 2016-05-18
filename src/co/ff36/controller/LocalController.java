package co.ff36.controller;

import co.ff36.util.FileTree;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Class dedicated to handling all functions relating to the local table pane of the application.
 *
 * Created by tarka on 11/05/2016.
 */
public class LocalController implements Initializable {

    @FXML
    private TreeView<File> tree;

    /**
     * Automatically invoked whenever the local pane is initialized. This happens after the constructor.
     * @param location The URL of the invoking FXML file
     * @param resources The resource bundle being used.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        load();
    }

    /**
     * This is fundamentally the initialize method used to prepare the local tree. It has been moved to a separate
     * method so that it can be called as part of a reload function.
     */
    public void load() {

        TreeView<File> view = new TreeView<>(new FileTree(new File(System.getProperty("user.home"))));
        TreeItem<File> root = view.getRoot();
        root.setExpanded(true);
        tree.setRoot(root);

        tree.setCellFactory(treeView -> new TreeCell<File>() {
            @Override
            public void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(file.getName());
                }
            }
        });
    }

    /**
     * A local directory has been selected to be archived. This method invokes the archive modal and passes the respective
     * information to it.
     * @param actionEvent The event used to set the new modal windows owner.
     * @throws IOException
     */
    @FXML
    protected void localDirectorySelected(ActionEvent actionEvent) throws IOException {
        TreeItem<File> selectedItem = tree.getSelectionModel().getSelectedItem();
        if (selectedItem.getValue().isDirectory()) {
            Stage stage = new Stage();
            ResourceBundle bundle = ResourceBundle.getBundle("cb-local", Locale.getDefault());
            URL resource = getClass().getResource("../fxml/archive.fxml");
            FXMLLoader loader = new FXMLLoader(resource, bundle);
            Parent root = loader.load();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle(bundle.getString("archive_modal_title"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((MenuItem) actionEvent.getSource()).getParentPopup().getOwnerWindow());

            ArchiveController archiveController = loader.<ArchiveController>getController();
            archiveController.setToArchive(selectedItem.getValue());

            stage.show();
        }
    }
}
