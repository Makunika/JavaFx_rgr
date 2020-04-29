package sample.packFileManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import sample.client.DataClient;
import sample.packFileManager.controllers.FileManager;

import java.util.regex.Pattern;

public class TreeTableController {
    private TableView<DataFile> refTableView;
    private TreeView<DataFile> refTreeView;
    private ObservableList<DataFile> files;
    private TreeItem<DataFile> parent;


    public TreeTableController(TableView< ? extends DataFile> tableView, TreeView< ? extends DataFile> treeView)
    {
        refTableView = (TableView<DataFile>) tableView;
        refTreeView = (TreeView<DataFile>) treeView;
        files = FXCollections.observableArrayList();
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
                String size = regex[0].equals("-1") ? "" : FuncStatic.getStringStorage(Long.parseLong(regex[0]));
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

    public void treeChildToTable(Label pathName, Button backPath) {
        TreeItem<DataFile> item = refTreeView.getSelectionModel().getSelectedItem();
        if (item != null && !item.getValue().isFile()) {
            backPath.setVisible(item.getParent() != null);
            parent = item;
            files.clear();

            for (TreeItem<DataFile> it : item.getChildren()) {
                files.add(it.getValue());
            }
            System.out.println(item.toString());

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

    public void add(TreeItem<DataFile> newElement)
    {
        files.add(newElement.getValue());
    }
}
