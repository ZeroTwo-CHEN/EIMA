package top.remake.component;

import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.util.Callback;

/**
 * 懒加载TextFieldTreeCell
 *
 * @author ZeroTwo_CHEN
 */
public class LazyFileTreeCell extends TextFieldTreeCell<String> {

    /**
     * 占位符，在未加载完成时显示
     */
    private static final String PLACE_HOLDER_TEXT = "...加载中...";

    public static Callback<TreeView<String>, TreeCell<String>> forTreeView() {
        return tree -> new LazyFileTreeCell();
    }


    /**
     * 重写更新方法
     */
    @Override
    public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else if (item == null && getTreeItem().getParent() instanceof FileTreeItem) {
            setText(PLACE_HOLDER_TEXT);
        } else {
            setText(item);
        }
    }
}
