package top.remake.controller;

import com.leewyatt.rxcontrols.controls.RXTranslationButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.BreadCrumbBar;
import top.remake.component.DirectoryLoader;
import top.remake.component.FileTreeItem;
import top.remake.component.LazyFileTreeCell;
import top.remake.component.PreviewFlowPane;
import top.remake.entity.SortOrder;

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
    private AnchorPane root;

    @FXML
    private AnchorPane top;

    @FXML
    private SplitPane middle;

    @FXML
    private AnchorPane bottom;

    @FXML
    private TreeView<String> fileTreeView;

    @FXML
    private BreadCrumbBar<String> breadCrumbBar;

    @FXML
    private ScrollPane imagePreviewPane;


    private PreviewFlowPane previewFlowPane;

    @FXML
    private Label tipsLabel;

    @FXML
    private ComboBox<String> sortOrderComboBox;

    @FXML
    private RXTranslationButton refreshButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFileTreeView();
        initBreadCrumbBar();
        initPreviewFlowPane();
        initAdaptiveLayout();
        initToolsPane();
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
     * 初始化缩略图面板
     */
    private void initPreviewFlowPane() {
        previewFlowPane = new PreviewFlowPane();
        imagePreviewPane.setContent(previewFlowPane);
    }

    /**
     * 初始树形目录导航栏
     */
    private void initBreadCrumbBar() {
        breadCrumbBar.selectedCrumbProperty()
                .addListener((observable, oldValue, newValue) -> {
                    fileTreeView.getSelectionModel().select(newValue);
                    previewFlowPane.update(((FileTreeItem) newValue).getDirectory());
                });
    }

    /**
     * 设置自适应布局
     */
    private void initAdaptiveLayout() {
        middle.setPrefHeight(root.getHeight() - top.getPrefHeight() - bottom.getPrefHeight());
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double temp = top.getPrefHeight() + bottom.getPrefHeight();
            middle.setPrefHeight(newValue.doubleValue() - temp);
        });
    }

    /**
     * 初始化工具栏
     */
    private void initToolsPane() {
        //排序选择框
        sortOrderComboBox.getItems()
                .addAll(SortOrder.ASC_SORT_BY_NAME, SortOrder.DESC_SORT_BY_NAME,
                        SortOrder.ASC_SORT_BY_TIME, SortOrder.DESC_SORT_BY_TIME,
                        SortOrder.ASC_SORT_BY_SIZE, SortOrder.DESC_SORT_BY_SIZE);
        sortOrderComboBox.getSelectionModel().select(0);
        sortOrderComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> previewFlowPane.update());

        //初始化刷新按钮
        ImageView imageView = new ImageView(new Image(getClass().getResource("/image/btn-refresh.png").toExternalForm(), true));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(18);
        refreshButton.setGraphic(imageView);
        refreshButton.setOnAction(event -> previewFlowPane.update());
    }

    /**
     * 左下角提示栏
     */
    public void setTipsLabelText(int number, double size) {
        tipsLabel.setText("共 " + number + " 张照片(" + String.format("%.2f", size) + " MB)");
    }

    /**
     * 返回comboBox中的值
     */
    public String getSortOrder() {
        return sortOrderComboBox.getSelectionModel().getSelectedItem();
    }
}
