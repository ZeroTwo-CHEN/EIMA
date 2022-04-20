package top.remake.controller;

import com.leewyatt.rxcontrols.utils.StringUtil;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.Notifications;
import top.remake.DisplayWindow;
import top.remake.component.*;
import top.remake.entity.RenameData;
import top.remake.entity.SortOrder;
import top.remake.utils.FileUtil;
import top.remake.utils.RandomUtil;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.text.NumberFormat;
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
    private Menu menu=new Menu();

    private void addHandler() {
        //对FlowPane添加监听器记录鼠标按下时的坐标，存放于x，y中。
        previewFlowPane.setOnMousePressed(event -> {
            x = event.getX();
            y = event.getY();
            menu.close();

            //处理单击空白部分时取消已选择的图片
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1 && !event.isControlDown()) {
                if (previewFlowPane.equals(event.getPickResult().getIntersectedNode())) {
                    previewFlowPane.clearSelect();
                }
                updateTipsLabelText();
            }
            //右键菜单
            if(event.getButton()==MouseButton.SECONDARY){
                menu.show(event.getScreenX(),event.getScreenY());
            }

            //拖拽鼠标向上超出边界时，将ScrollPane上滑

        });

        //通过对FlowPane添加监听器记录并计算矩形（多选框）的坐标，宽高；
        //根据得到的坐标和宽高绘制矩形并动态选择图片
        previewFlowPane.setOnMouseDragged(event -> {
            double x2 = event.getX();
            double y2 = event.getY();
            double endX = Math.min(x, x2);
            double endY = Math.min(y, y2);
            width = Math.abs(x - x2);
            height = Math.abs(y - y2);
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
                        (event.getSceneY() - 100) / previewFlowPane.getHeight() / 10);
            }
            //拖拽鼠标向下超出边界时。将ScrollPane下滑
            if (event.getSceneY() > 100 + imagePreviewPane.getHeight()) {
                imagePreviewPane.setVvalue(imagePreviewPane.getVvalue() +
                        (event.getSceneY() - imagePreviewPane.getHeight() - 100) / previewFlowPane.getHeight() / 10);
            }

        });
        previewFlowPane.setOnMouseReleased(event -> {
            //选中图片
            rectangle.setVisible(false);
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

    public List<ThumbnailPanel> getCopyImg() {
        return copyImg;
    }

    private List<ThumbnailPanel> copyImg = new ArrayList<>();

    /**
     * 复制图片
     */
    @FXML
    private void copyImage() {
        copyImg.clear();
        copyImg.addAll(previewFlowPane.getNewChoices());

    }

    @FXML
    private void pasteImage() {
        List<ThumbnailPanel> images = getCopyImg();
        if (images.size() == 0) {
            return;
        }
        File resource, target;
        File directory = previewFlowPane.getDirectory();
        for (ThumbnailPanel image : images) {
            resource = image.getImageFile().getFile();
            String out = directory.getAbsolutePath() + "\\" + resource.getName();
            target = new File(out);
            try {
                //目标文件已存在时，在文件名后加“-副本后缀”
                while (target.exists()) {
                    String suffix = out.substring(out.lastIndexOf("."));
                    out = out.replace(suffix, "-副本") + suffix;
                    target = new File(out);
                }
                Files.copy(resource.toPath(), target.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        updateFlowPane();
    }

    @FXML
    private void renameImage() {
        //判断是否有图片被选中
        if (previewFlowPane.getNewChoices().isEmpty()) {
            Dialog<RenameData> dialog = new Dialog<>();
            Label label = new Label("未选择图片！");
            label.setTextAlignment(TextAlignment.CENTER);
            dialog.getDialogPane().setContent(label);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == ButtonType.OK) {
                    return null;
                }
                return null;
            });
            dialog.showAndWait();
            return;
        }
        //确认已有选中图片
        RenameImage renameImage = new RenameImage();
        Optional<RenameData> data = renameImage.showAndWait();
        data.ifPresent(e -> {
            RenameData data1 = data.get();
            renameImage(data1.getName(), data1.getStartNum(), data1.getDigit());
            refresh();
        });
    }

    /**
     * 根据用户所给信息对选中文件进行批量重命名
     */
    private void renameImage(String name, int startNum, int digit) {
        File file;
        List<ThumbnailPanel> images = previewFlowPane.getNewChoices();
        List<String> pasts=new ArrayList<>();
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMinimumIntegerDigits(digit);
        nf.setGroupingUsed(false);

        for (ThumbnailPanel image : images) {
            pasts.add(image.getImageFile().getFile().getParentFile().getAbsolutePath() + "\\"+image.getImageFile().getFileName());
            String tempName = image.getImageFile().getFile().getParentFile().getAbsolutePath() + "\\" + RandomUtil.randomName();
            String type = image.getImageFile().getFileType();
            File newFile = new File(tempName + "." + type);
            image.getImageFile().getFile().renameTo(newFile);
            image.getImageFile().setFile(newFile);
        }

        for (ThumbnailPanel image : images) {
            StringBuilder newName = new StringBuilder();

            //生成新图片的绝对路径 eg：C:\\User\Code\abc001.png
            file = image.getImageFile().getFile();
            newName.append(file.getParentFile().getAbsolutePath()).append("\\");

            //生成图片新名称 eg：abc001.png
            newName.append(name)
                    .append(nf.format(startNum))
                    .append(".")
                    .append(image.getImageFile().getFileType().toLowerCase(Locale.ROOT));

            File dest = new File(newName.toString());
            int choice = renameCheck(dest);
            if (choice == 1) {
                //替换已存在图片
                dest.delete();
                file.renameTo(dest);
                image.getImageFile().setFile(dest);
            }

            if (choice == -1) {
                //跳过该图片的重命名
                dest=new File(pasts.get(images.indexOf(image)));
                file.renameTo(dest);
                image.getImageFile().setFile(dest);
            }

            if (choice == 0) {
                //取消所有的重命名操作
                for(ThumbnailPanel image1 : images){
                    file = image1.getImageFile().getFile();
                    dest=new File(pasts.get(images.indexOf(image1)));
                    file.renameTo(dest);
                    image1.getImageFile().setFile(dest);
                }
                break;
            }
            if (choice == 2) {
                file.renameTo(dest);
                image.getImageFile().setFile(dest);
            }
            startNum++;
        }

    }

    /**
     * 判断该文件新名称在该路径下是否已经存在
     * 如果已存在
     */
    private int renameCheck(File file) {
        if (file.exists()) {
            Dialog<Integer> dialog = new Dialog<>();
            GridPane gridPane = new GridPane();
            dialog.setTitle(file.getName() + "已存在！");
            Label label1 = new Label("替换:替换已存在的图片");
            Label label2 = new Label("跳过:跳过该图片重命名");
            Label label3 = new Label("取消:取消重命名图片  ");
            label1.setAlignment(Pos.CENTER);
            label2.setAlignment(Pos.CENTER);
            label3.setAlignment(Pos.CENTER);
            gridPane.add(label1, 0, 0);
            gridPane.add(label2, 0, 1);
            gridPane.add(label3, 0, 2);
            gridPane.setPadding(new Insets(10, 10, 10, 10));
            gridPane.setHgap(10);

            ButtonType buttonType1 = new ButtonType("替换");
            ButtonType buttonType2 = new ButtonType("跳过");
            dialog.getDialogPane().setContent(gridPane);
            dialog.getDialogPane().getButtonTypes().addAll(buttonType1, buttonType2, ButtonType.CANCEL);
            dialog.setResultConverter(e -> {
                if (e == buttonType1) {
                    //替换按钮
                    return 1;
                }
                if (e == buttonType2) {
                    //跳过按钮
                    return -1;
                }
                //取消按钮
                return 0;
            });
            Optional<Integer> data = dialog.showAndWait();
            if (data.isPresent()) {
                return data.get();
            }
        }
        return 2;
    }

    class Menu {
        public  ContextMenu menu = new ContextMenu();

          Menu() {
            MenuItem delete = new MenuItem("删除");
            MenuItem copy = new MenuItem("复制");
            MenuItem paste = new MenuItem("粘贴");
            MenuItem rename = new MenuItem("重命名");
            delete.setOnAction(e -> deleteImage());
            copy.setOnAction(e -> copyImage());
            paste.setOnAction(e -> pasteImage());
            rename.setOnAction(e -> renameImage());
            menu.getItems().addAll(delete, copy, paste, rename);
        }
        void show(double x,double y){
            menu.show(pane,x,y);
        }
        void close(){
             menu.hide();
        }
    }
}
