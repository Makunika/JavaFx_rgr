package sample.packFileManager;

import com.jfoenix.controls.JFXButton;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.shape.SVGPath;
import sample.client.DataClient;
import sample.client.SVGIcons;
import sample.packFileManager.newtreeitem.FilterableTreeItem;
import sample.packFileManager.newtreeitem.TreeItemPredicate;

import java.util.Date;
import java.util.regex.Pattern;

public class TreeTableController {
    private final TableView<DataFile>       refTableView;
    private final TreeView<DataFile>        refTreeView;
    private final ObservableList<DataFile>  files;
    private final JFXButton                 backPath;
    private final Label                     pathName;
    private FilterableTreeItem<DataFile>    parent;
    private Moved                           moved;

    public TreeTableController(TableView<? extends DataFile> tableView, TreeView<? extends DataFile> treeView,
                               JFXButton backPath, Label pathName)
    {
        refTableView = (TableView<DataFile>) tableView;
        refTreeView = (TreeView<DataFile>) treeView;
        this.backPath = backPath;
        this.pathName = pathName;
        backPath.setText("");
        SVGPath path = new SVGPath();
        path.setContent(SVGIcons.ARROW_BACK.getPath());
        path.setStyle("-fx-fill: #79a6f2");
        backPath.setGraphic(path);
        files = FXCollections.observableArrayList();
        init();
    }

    private void init() {
        refTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        refTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        FilterableTreeItem<DataFile> root = new FilterableTreeItem<>(new DataFile("path", DataClient.login,"2020-05-04 20:20:20",""));

        if (DataClient.tree != null) {
            Pattern pattern = Pattern.compile("\n");
            parseTree(root, pattern.split(DataClient.tree), new Index(0), 0);
        }
        refTreeView.setRoot(root);

        TextField textField = new TextField();
        textField.setText(".path");
        root.predicateProperty().bind(Bindings.createObjectBinding(()
                        -> TreeItemPredicate.<DataFile> create(dataFile
                        -> dataFile.getSuffix().equals(textField.getText()))
                , textField.textProperty()));


        refTreeView.setShowRoot(true);
        root.setExpanded(true);
        parent = root;
        for (TreeItem<DataFile> it : root.getInternalChildren()) {
            files.add(it.getValue());
        }
        refTableView.setItems(files);
    }

    public void updateTree() {
        getParent().setExpanded(!getParent().isExpanded());
        getParent().setExpanded(!getParent().isExpanded());
    }

    public void updateTable() {
        files.clear();

        for (TreeItem<DataFile> it : parent.getInternalChildren()) {
            files.add(it.getValue());
        }
        refTableView.setItems(files);
        refTableView.sort();
    }

    private class Index
    {
        public int index;

        public Index(int index)
        {
            this.index = index;
        }
    }


    private void parseTree(FilterableTreeItem<DataFile> root, String[] strings, Index index, int rank) {
        Pattern pattern;

        FilterableTreeItem<DataFile> newRoot = root;
        while (index.index != strings.length)
        {
            pattern = Pattern.compile("\t");
            String[] stringsItem = pattern.split(strings[index.index]);
            if (Integer.parseInt(stringsItem[0]) > rank)
            {
                parseTree(newRoot, strings, index,rank + 1);
                continue;
            }
            else if (Integer.parseInt(stringsItem[0]) < rank)
            {
                return;
            }
            else
            {
                pattern = Pattern.compile("\\\\");
                String[] regex = pattern.split(stringsItem[2]);
                String type = regex[0].equals("-1") ? "path" : "file";
                String size = regex[0].equals("-1") ? "" : regex[0];
                FilterableTreeItem<DataFile> newItem = null;
                regex[1] = regex[1].replace("T", " ");
                regex[1] = regex[1].substring(0,regex[1].length() - 8);
                newItem = new FilterableTreeItem<>(new DataFile(type, stringsItem[1],regex[1], size));
                root.getInternalChildren().add(newItem);
                newRoot = newItem;
            }




            index.index++;
        }

    }

    public void treeChildToTable() {
        TreeItem<DataFile> item = refTreeView.getSelectionModel().getSelectedItem();
        if (item != null && !item.getValue().isFile()) {
            backPath.setVisible(item.getParent() != null);
            parent = (FilterableTreeItem<DataFile>) item;
            updateTable();

            StringBuffer sb = new StringBuffer("");
            while (item != null) {
                sb.insert(0, item.getValue().getName() + "\\");
                item = item.getParent();
            }
            pathName.setText(sb.toString());
        }
    }


    public FilterableTreeItem<DataFile> getParent() {
        return parent;
    }

    public void setParent(FilterableTreeItem<DataFile> parent) {
        this.parent = parent;
    }

    public ObservableList<DataFile> getFiles() {
        return files;
    }

    public void addItem(FilterableTreeItem<DataFile> newElement)
    {
        parent.getInternalChildren().add(newElement);
        treeChildToTable();
        updateTree();
        updateTable();
    }

    public void setMoved(FilterableTreeItem<DataFile> moved, String path) {
        this.moved = new Moved();
        this.moved.movedDataFile = moved;
        this.moved.oldPathNameMoved = path;
    }

    public Moved getMoved() {
        moved.newParent = parent;
        return moved;
    }

    public Moved initMoved() {
        moved.movedDataFile.getParentMy().getInternalChildren().remove(moved.movedDataFile);
        moved.newParent.getInternalChildren().add(moved.movedDataFile);
        updateTree();
        updateTable();

        return moved;
    }

    public FilterableTreeItem<DataFile> findByDataFile(DataFile item)
    {
        TreeItem<DataFile> find = getParent().getInternalChildren().get(0);
        for (TreeItem<DataFile> it:
                getParent().getInternalChildren()) {
            if (it.getValue() == item)
            {
                find = it;
                ((FilterableTreeItem<DataFile>)find).setParentMy(parent);
                break;
            }
        }
        return (FilterableTreeItem<DataFile>)find;
    }

    public void deleteItem(DataFile item)
    {
        FilterableTreeItem<DataFile> it = findByDataFile(item);
        it.getParentMy().getInternalChildren().remove(it);
        treeChildToTable();
        updateTable();
        updateTree();
    }

    public void deleteItem(TreeItem<DataFile> item)
    {
        item.getParent().getChildren().remove(item);
        updateTable();
        updateTree();
    }

    public void addItem(DataFile item)
    {
        addItem(new FilterableTreeItem<>(item));
    }

    public Button getBackPath() {
        return backPath;
    }

    public Label getPathName() {
        return pathName;
    }

    public TableView<DataFile> getRefTableView() {
        return refTableView;
    }

    public TreeView<DataFile> getRefTreeView() {
        return refTreeView;
    }
}
