package sample.packFileManager.newtreeitem;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.TreeItem;

import java.lang.reflect.Field;

public class FilterableTreeItem<T> extends TreeItem<T> {
    private final ObservableList<TreeItem<T>> sourceList;

    private FilterableTreeItem<T> parentMy;

    private final ObjectProperty<TreeItemPredicate<T>> predicate = new SimpleObjectProperty<>();

    public FilterableTreeItem(T value) {
        super(value);
        sourceList = FXCollections.observableArrayList();
        FilteredList<TreeItem<T>> filteredList = new FilteredList<>(sourceList);

        filteredList.predicateProperty().bind(Bindings.createObjectBinding(() -> {
            return child -> {
                // Set the predicate of child items to force filtering
                if (child instanceof FilterableTreeItem) {
                    FilterableTreeItem<T> filterableChild = (FilterableTreeItem<T>) child;
                    filterableChild.setPredicate(predicate.get());
                }
                // If there is no predicate, keep this tree item
                if (predicate.get() == null) {
                    return true;
                }
                // If there are children, keep this tree item
                if (!child.getChildren().isEmpty()) {
                    return true;
                }
                // Otherwise ask the TreeItemPredicate
                return predicate.get().test(this, child.getValue());
            };
        }, predicate));
        setHiddenFieldChildren(filteredList);
    }

    protected void setHiddenFieldChildren(ObservableList<TreeItem<T>> list) {
        try {
            Field childrenField = TreeItem.class.getDeclaredField("children"); //$NON-NLS-1$
            childrenField.setAccessible(true);
            childrenField.set(this, list);

            Field declaredField = TreeItem.class.getDeclaredField("childrenListener"); //$NON-NLS-1$
            declaredField.setAccessible(true);
            list.addListener((ListChangeListener<? super TreeItem<T>>) declaredField.get(this));
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException("Could not set TreeItem.children", e); //$NON-NLS-1$
        }
    }

    public void setParentMy(FilterableTreeItem<T> parentMy) {
        this.parentMy = parentMy;
    }

    public FilterableTreeItem<T> getParentMy() {
        return parentMy;
    }

    /**
     * Returns the list of children that is backing the filtered list.
     *
     * @return underlying list of children
     */


    public ObservableList<TreeItem<T>> getInternalChildren() {
        return sourceList;
    }

    public final ObjectProperty<TreeItemPredicate<T>> predicateProperty() {
        return predicate;
    }

    public final TreeItemPredicate<T> getPredicate() {
        return predicate.get();
    }

    public final void setPredicate(TreeItemPredicate<T> predicate) {
        this.predicate.set(predicate);
    }
}
