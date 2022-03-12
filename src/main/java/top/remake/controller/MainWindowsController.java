package top.remake.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.controlsfx.control.BreadCrumbBar;
import top.remake.component.DirectoryLoader;
import top.remake.component.FileTreeItem;
import top.remake.component.LazyFileTreeCell;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author ZeroTwo_CHEN
 */
public class MainWindowsController implements Initializable {
    @FXML
    private TreeView<String> fileTreeView;

    @FXML
    private BreadCrumbBar<String> breadCrumbBar;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFileTreeView();
        initBreadCrumbBar();
    }

    /**
     * 初始化树形目录
     */
    private void initFileTreeView() {
        fileTreeView.setCellFactory(LazyFileTreeCell.forTreeView());

        TreeItem<String> treeItem = new TreeItem<>();

        //读取文件系统的根并将其添加到根节点中
        File root = FileSystemView.getFileSystemView().getRoots()[0];
        File[] allFiles = root.listFiles();
        File[] directorFiles = root.listFiles(File::isDirectory);
        List<File> list = new ArrayList<>(Arrays.asList(allFiles));
        list.removeAll(Arrays.asList(directorFiles));
        for (File file : list) {
            //过滤文件及快捷方式
            if (file.isDirectory() && !file.getName().endsWith("lnk")) {
                treeItem.getChildren().add(new FileTreeItem(file, new DirectoryLoader(file)));
            }
        }

        fileTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        breadCrumbBar.selectedCrumbProperty().set(newValue));
        fileTreeView.setRoot(treeItem);
        fileTreeView.setShowRoot(false);
    }

    /**
     * 初始树形目录导航栏
     */
    private void initBreadCrumbBar() {
        breadCrumbBar.selectedCrumbProperty()
                .addListener((observable, oldValue, newValue) ->
                        fileTreeView.getSelectionModel().select(newValue));
    }
}
