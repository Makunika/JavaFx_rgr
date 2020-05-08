package sample.packFileManager;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.JFXTreeView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import sample.client.DataClient;

import java.util.regex.Pattern;

public class TreeTableController {
    private final TableView<DataFile>       refTableView;
    private final TreeView<DataFile>        refTreeView;
    private final ObservableList<DataFile>  files;
    private final Button                    backPath;
    private final Label                     pathName;
    private TreeItem<DataFile>              parent;
    private Moved                           moved;
    public TreeTableController(TableView<? extends DataFile> tableView, TreeView<? extends DataFile> treeView,
                               Button backPath, Label pathName)
    {
        refTableView = (TableView<DataFile>) tableView;
        refTreeView = (TreeView<DataFile>) treeView;
        this.backPath = backPath;
        this.pathName = pathName;
        files = FXCollections.observableArrayList();
        init();
    }

    private void init() {
        refTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        TreeItem<DataFile> root = new TreeItem<>(new DataFile("path", DataClient.login,"date","size"));

        if (DataClient.tree != null) {
            Pattern pattern = Pattern.compile("\n");
            parseTree(root, pattern.split(DataClient.tree), new Index(0), 0);
        }
        refTreeView.setRoot(root);
        refTreeView.setShowRoot(true);
        root.setExpanded(true);
        parent = root;
        for (TreeItem<DataFile> it : root.getChildren()) {
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

        for (TreeItem<DataFile> it : parent.getChildren()) {
            files.add(it.getValue());
        }
        refTableView.setItems(files);
    }

    private class Index
    {
        public int index;

        public Index(int index)
        {
            this.index = index;
        }
    }


    private void parseTree(TreeItem<DataFile> root, String[] strings, Index index, int rank) {
        Pattern pattern;

        TreeItem<DataFile> newRoot = root;
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
                TreeItem<DataFile> newItem = null;
                regex[1] = regex[1].replace("T", " ");
                regex[1] = regex[1].substring(0,regex[1].length() - 8);
                newItem = new TreeItem<>(new DataFile(type, stringsItem[1],regex[1], size));
                root.getChildren().add(newItem);
                newRoot = newItem;

            }




            index.index++;
        }

    }

    public void treeChildToTable() {
        TreeItem<DataFile> item = refTreeView.getSelectionModel().getSelectedItem();
        if (item != null && !item.getValue().isFile()) {
            backPath.setVisible(item.getParent() != null);
            parent = item;
            updateTable();

            StringBuffer sb = new StringBuffer("");
            while (item != null) {
                sb.insert(0, item.getValue().getName() + "\\");
                item = item.getParent();
            }
            pathName.setText(sb.toString());
        }
    }


    public TreeItem<DataFile> getParent() {
        return parent;
    }

    public void setParent(TreeItem<DataFile> parent) {
        this.parent = parent;
    }

    public ObservableList<DataFile> getFiles() {
        return files;
    }

    public void addItem(TreeItem<DataFile> newElement)
    {
        parent.getChildren().add(newElement);
        treeChildToTable();
        updateTree();
        updateTable();
    }

    public void setMoved(TreeItem<DataFile> moved, String path) {
        this.moved = new Moved();
        this.moved.movedDataFile = moved;
        this.moved.oldPathNameMoved = path;
    }

    public Moved getMoved() {
        moved.newParent = parent;
        return moved;
    }

    public Moved initMoved() {
        moved.movedDataFile.getParent().getChildren().remove(moved.movedDataFile);
        moved.newParent.getChildren().add(moved.movedDataFile);
        updateTree();
        updateTable();

        return moved;
    }

    public TreeItem<DataFile> findByDataFile(DataFile item)
    {
        TreeItem<DataFile> find = getParent().getChildren().get(0);
        for (TreeItem<DataFile> it:
                getParent().getChildren()) {
            if (it.getValue() == item)
            {
                find = it;
                break;
            }
        }
        return find;
    }

    public void deleteItem(DataFile item)
    {
        TreeItem<DataFile> it = findByDataFile(item);
        it.getParent().getChildren().remove(it);
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
        addItem(new TreeItem<>(item));
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
