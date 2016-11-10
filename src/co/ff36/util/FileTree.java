package co.ff36.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.io.File;

/**
 * Display a file tree of the system. The file tree system will only be inspected once. If the tree changes during
 * runtime the whole tree will nee to be rebuilt.
 * Created by tarka on 13/05/2016.
 */
public class FileTree extends TreeItem<File> {

    private boolean isFirstTimeChildren = true;
    private boolean isFirstTimeLeaf = true;
    private boolean isLeaf;

    /**
     * Calling the constructor of super class in oder to create a new
     * TreeItem<File>.
     *
     * @param file
     *            an object of type File from which a tree should be build or
     *            which children should be gotten.
     */
    public FileTree(File file) {
        super(file);
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.control.TreeItem#getChildren()
     */
    @Override
    public ObservableList<TreeItem<File>> getChildren() {
        if (isFirstTimeChildren) {
            isFirstTimeChildren = false;

			/*
			 * First getChildren() call, so we actually go off and determine the
			 * children of the File contained in this TreeItem.
			 */
            super.getChildren().setAll(buildChildren(this));
        }
        return super.getChildren();
    }

    /*
     * (non-Javadoc)
     *
     * @see javafx.scene.control.TreeItem#isLeaf()
     */
    @Override
    public boolean isLeaf() {
        if (isFirstTimeLeaf) {
            isFirstTimeLeaf = false;
            File f = getValue();
            isLeaf = f.isFile();
        }

        return isLeaf;
    }

    /**
     * Returning a collection of type ObservableList containing TreeItems, which
     * represent all children available in handed TreeItem.
     *
     * @param TreeItem
     *            the root node from which children a collection of TreeItem
     *            should be created.
     * @return an ObservableList<TreeItem<File>> containing TreeItems, which
     *         represent all children available in handed TreeItem. If the
     *         handed TreeItem is a leaf, an empty list is returned.
     */
    private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
        File f = TreeItem.getValue();
        if (f != null && f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();

                for (File childFile : files) {
                    if (!childFile.getName().startsWith(".")
                            && !childFile.getName().startsWith("%")) children.add(new FileTree(childFile));
                }

                return children;
            }
        }

        return FXCollections.emptyObservableList();
    }
}
