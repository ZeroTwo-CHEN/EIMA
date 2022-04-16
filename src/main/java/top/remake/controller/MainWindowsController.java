package top.remake.controller;

import com.leewyatt.rxcontrols.controls.RXTranslationButton;
import com.leewyatt.rxcontrols.utils.StringUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.Notifications;
import top.remake.DisplayWindow;
import top.remake.component.*;
import top.remake.entity.SortOrder;
import top.remake.utils.FileUtil;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.*;

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
    private HBox bottom;

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

    @FXML
    private RXTranslationButton selectAllButton;

    @FXML
    private TextField searchField;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFileTreeView();
        initPreviewFlowPane();
        initAdaptiveLayout();
        initToolsPane();
        initSearch();
    }

    /**
     * 为搜索框加上监听事件，实现搜索内容的高亮显示
     * 当前为实时搜索，不用按下回车键
     */
    private void initSearch() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {

            List<ThumbnailPanel> thumbnailPanels = previewFlowPane.getThumbnailPanels();

            //为空时显示全部
            if (searchField.getText().isEmpty()) {
                previewFlowPane.getChildren().setAll(thumbnailPanels);
                return;
            }

            //非空时开始过滤匹配
            List<ThumbnailPanel> filtered = thumbnailPanels.stream()
                    .filter(t -> {
                        final boolean[] result = {false};

                        if (t.getImageName().isMatch()) {
                            return result[0] = true;
                        }

                        ArrayList<Pair<String, Boolean>> pairs = StringUtil.parseText(t.getImageFile().getFileName(), newValue, true);
                        pairs.forEach(pair -> {
                            if (pair.getValue()) {
                                result[0] = true;
                            }
                        });

                        return result[0];
                    }).toList();

            previewFlowPane.getChildren().setAll(filtered);
        });
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
                .addListener((observable, oldValue, newValue) -> {
                    if (newValue instanceof FileTreeItem) {
                        breadCrumbBar.selectedCrumbProperty().set(newValue);
                        previewFlowPane.update(((FileTreeItem) newValue).getDirectory());
                    }
                });
        fileTreeView.setRoot(treeItem);
    }

    private Pane pane;
    private Rectangle rectangle;

    /**
     * 初始化缩略图面板
     */
    private void initPreviewFlowPane() {
        previewFlowPane = new PreviewFlowPane();
        rectangle = new Rectangle();
        pane = new Pane();
        rectangle.setFill(Color.rgb(70, 170, 227, 0.6));
        rectangle.setVisible(false);
        pane.getChildren().addAll(previewFlowPane, rectangle);
        addHandler();

        previewFlowPane.heightProperty()
                .addListener((observable, oldValue, newValue) -> pane.setMinHeight(previewFlowPane.getHeight()));

        imagePreviewPane.viewportBoundsProperty()
                .addListener((observable, oldValue, newValue) -> {
                    previewFlowPane.setPrefWidth(newValue.getWidth());
                    if (previewFlowPane.getHeight() < imagePreviewPane.getHeight()) {
                        previewFlowPane.setPrefHeight(imagePreviewPane.getHeight());
                    }
                });

        imagePreviewPane.setContent(pane);
        rectangle.setVisible(false);
        //rectangle.toFront();
        imagePreviewPane.setCache(true);
    }

    /**
     * 树形目录导航栏
     */
    @FXML
    private void onCrumbAction(BreadCrumbBar.BreadCrumbActionEvent<String> event) {
        TreeItem<String> newValue = event.getSelectedCrumb();
        if (newValue instanceof FileTreeItem) {
            fileTreeView.getSelectionModel().select(newValue);
            previewFlowPane.update(((FileTreeItem) newValue).getDirectory());
        }
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
    }

    @FXML
    private void refresh() {
        previewFlowPane.update();
    }

    @FXML
    private void selectAll() {
        previewFlowPane.selectAll();
        updateTipsLabelText();
    }

    /**
     * 更新左下角提示栏
     */
    private void updateTipsLabelText() {
        updateTipsLabelText(previewFlowPane.getTotalCount(), previewFlowPane.getTotalSize(),
                previewFlowPane.getSelectedCount(), previewFlowPane.getSelectedSize());
    }

    /**
     * 更新左下角提示栏
     *
     * @param totalCount    当前目录的图片总数
     * @param size          总大小
     * @param selectedCount 被选中的图片的数量
     * @param selectedSize  被选中的图片的大小
     */
    public void updateTipsLabelText(int totalCount, double size, int selectedCount, double selectedSize) {
        tipsLabel.setText(String.format("共 %d 张照片(%.2f MB) - 选中 %d 张照片(%.2f MB)",
                totalCount,
                size,
                selectedCount,
                selectedSize));
    }

    /**
     * 返回comboBox中的值
     */
    public String getSortOrder() {
        return sortOrderComboBox.getSelectionModel().getSelectedItem();
    }


    /**
     * 鼠标按下时的坐标
     */
    private double x, y;
    private double width;
    private double height;

    private void addHandler() {
        //对FlowPane添加监听器记录鼠标按下时的坐标，存放于x，y中。
        previewFlowPane.setOnMousePressed(event -> {
            x = event.getX();
            y = event.getY();
        });

        //通过对FlowPane添加监听器记录并计算矩形（多选框）的坐标，宽高；
        //根据得到的坐标和宽高绘制矩形并动态选择图片
        previewFlowPane.setOnMouseDragged(event -> {
            double x2 = event.getX();
            double y2 = event.getY();
            double endX = Math.min(x, x2);
            double endY = Math.min(y, y2);
            width = Math.abs(x - x2);
            height = Math.abs(y-y2);
            rectangle.setX(endX);
            rectangle.setY(endY);
            rectangle.setWidth(width);
            rectangle.setHeight(height);
            rectangle.setVisible(true);
            //选择矩形所经过的图片
            if (width >= 10 && height >= 10) {
                selectImg();
            }
            //拖拽鼠标向上超出边界时，将ScrollPane上滑
            if (event.getSceneY() < 100) {
                imagePreviewPane.setVvalue(imagePreviewPane.getVvalue() +
                        (event.getSceneY() - 100) / previewFlowPane.getHeight() / 100);
            }
            //拖拽鼠标向下超出边界时。将ScrollPane下滑
            if (event.getSceneY() > 100 + imagePreviewPane.getHeight()) {
                imagePreviewPane.setVvalue(imagePreviewPane.getVvalue() +
                        (event.getSceneY() - imagePreviewPane.getHeight() - 100) / previewFlowPane.getHeight() / 10);
            }
        });//处理单击图片或者单击空白面板
        previewFlowPane.setOnMouseReleased(event -> {
            //选中图片
            rectangle.setVisible(false);
        });

        //处理单击空白部分时取消已选择的图片
        previewFlowPane.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1 && !event.isControlDown()) {
                if (previewFlowPane.equals(event.getPickResult().getIntersectedNode())) {
                    previewFlowPane.clearSelect();
                }
                updateTipsLabelText();
            }
        });
    }

    /**
     * 根据矩形的数据选择图片
     */
    private void selectImg() {
        previewFlowPane.clearSelect();
        for (ThumbnailPanel pane : previewFlowPane.getThumbnailPanels()) {
            boolean isIntersects = rectangle.intersects(pane.getLayoutX(), pane.getLayoutY(), pane.getWidth(), pane.getHeight());
            if (isIntersects) {
                previewFlowPane.addSelect(pane);
            }
        }
        updateTipsLabelText();
    }

    /**
     * 刷新缩略图面板
     */
    public void updateFlowPane() {
        previewFlowPane.update();
    }

    /**
     * 删除图片
     */
    @FXML
    private void deleteImage() {
        List<ThumbnailPanel> images = previewFlowPane.getNewChoices();
        if (images.size() == 0) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("确认删除这 " + images.size() + " 张图片吗？");
        alert.setContentText("删除后可以在系统回收站找回");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.runLater(() -> {
                for (ThumbnailPanel image : images) {
                    FileUtil.delete(image.getImageFile().getFile());
                }
                updateFlowPane();
            });
        }

    }

    /**
     * 播放幻灯片
     */
    @FXML
    private void play() {
        int selectedCount = previewFlowPane.getSelectedCount();
        //如果未选择图片或选择了多张图片，则从第一张开始播放,否则从选择的图片开始播放
        if (previewFlowPane.getThumbnailPanels().size() == 0) {
            Notifications.create()
                    .text("当前目录下没有图片")
                    .hideAfter(Duration.seconds(3))
                    .position(Pos.BOTTOM_RIGHT)
                    .owner(root)
                    .show();
        } else if (selectedCount == 1) {
            ThumbnailPanel thumbnailPanel = previewFlowPane.getNewChoices().get(0);
            String[] args = {thumbnailPanel.getImageFile().getAbsolutePath(), ""};
            Platform.runLater(() -> DisplayWindow.main(args));
        } else {
            String[] args = {previewFlowPane.getThumbnailPanels().get(0).getImageFile().getAbsolutePath(), ""};
            Platform.runLater(() -> DisplayWindow.main(args));
        }
    }

    public TextField getSearchField() {
        return searchField;
    }
}
