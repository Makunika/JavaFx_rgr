package sample.packFileManager;

import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;

import java.io.File;
import java.util.List;

public class DragAndDrop extends Loadable {
    public DragAndDrop(StackPane stackPane, ProgressIndicator progressIndicator, Label labelIndicator, TreeTableController treeTableController) {
        super(stackPane, progressIndicator, labelIndicator, treeTableController);

        treeTableController.getRefTableView().setOnDragOver(event -> {
            if (event.getGestureSource() != treeTableController.getRefTableView()
                    && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.ANY);
            }
            event.consume();

        });


        treeTableController.getRefTableView().setOnDragEntered(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                treeTableController.getRefTableView().setStyle("-fx-border-color: #adb2ff; -fx-border-width: 2px");
            }
            event.consume();
        });
        treeTableController.getRefTableView().setOnDragExited(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles()) {
                treeTableController.getRefTableView().setStyle("-fx-border-color: transparent; -fx-border-width: 0px");
            }
            event.consume();
        });
        treeTableController.getRefTableView().setOnDragDone(event -> {
            treeTableController.getRefTableView().setStyle("-fx-border-color: transparent; -fx-border-width: 0px");
        });
        treeTableController.getRefTableView().setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasFiles())
            {
                List<File> files = db.getFiles();
                if (files.size() < 2) {
                    File file = files.get(0);
                    if (file.isDirectory()) {
                        uploadPath(file);
                    }
                    else {
                        uploadFile(file);
                    }
                    event.setDropCompleted(true);
                    event.consume();
                }
                else {
                    event.setDropCompleted(true);
                    event.consume();
                    new Alert(stackPane,"Можно перетащить только один файл").show();
                }
            }
        });



    }
}
