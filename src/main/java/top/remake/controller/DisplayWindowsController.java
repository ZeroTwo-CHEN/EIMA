package top.remake.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.controlsfx.control.Notifications;
import top.remake.entity.ImageFile;
import top.remake.entity.SortOrder;
import top.remake.utils.FileUtil;
import top.remake.utils.SortUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * @author ZeroTwo_CHEN
 */
public class DisplayWindowsController implements Initializable {
    @FXML
    private ImageView imageView;

    @FXML
    private StackPane imagePane;

    @FXML
    private AnchorPane toolBar;

    private File directory;

    private Stage stage;

    private ArrayList<ImageFile> imageFiles = new ArrayList<>();

    private MainWindowsController mainWindowsController;


    /**
     * 当前展示的图片
     */
    private Image image;

    /**
     * 当前图片指针
     */
    private int currentIndex;

    /**
     * 图片放大比例
     */
    private int scale = 100;

    /**
     * 最大比例
     */
    private final static int MAX_SCALE = 3200;

    /**
     * 最小比例
     */
    private final static int MIN_SCALE = 10;

    /**
     * 鼠标按下时的坐标
     */
    private Point2D point;

    /**
     * 图片与窗口的间隔
     * 实际间隔为 MARGIN/2
     */
    private final static int MARGIN = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void init(Stage stage, String path) {
        File file = new File(path);
        this.directory = file.getParentFile();
        this.stage = stage;
        this.mainWindowsController = (MainWindowsController) ControllerMap.getController(MainWindowsController.class);
        File[] images = directory.listFiles(FileUtil::isSupportImageFormat);
        if (images != null) {
            for (File image : images) {
                ImageFile imageFile = new ImageFile(image);
                this.imageFiles.add(imageFile);
            }
        }
        initImageList(file);

        //给stage增加监听事件
        this.stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                stage.setAlwaysOnTop(true);
                //隐藏下方工具栏
                toolBar.setVisible(false);
                toolBar.setManaged(false);
            } else {
                //还原图片比例
                originalScale();
                updateImageView();
                stage.setAlwaysOnTop(false);
                toolBar.setManaged(true);
                toolBar.setVisible(true);
                if (timeline != null) {
                    timeline.stop();
                }
                //清空展示面板的监听事件
                imagePane.setOnKeyPressed(e -> {
                });
            }
        });


        //TODO: 实现图片的异步加载
        image = new Image(file.toPath().toUri().toString());
        updateImageView();
    }

    /**
     * 按照主窗口的排序规则排序
     * 获取当前图片在数组中的下标
     */
    private void initImageList(File file) {
        if (mainWindowsController != null) {
            SortUtil.sortImageFile(imageFiles, mainWindowsController.getSortOrder());
        } else {
            mainWindowsController = (MainWindowsController) ControllerMap.getController(MainWindowsController.class);
            SortUtil.sortImageFile(imageFiles, SortOrder.ASC_SORT_BY_NAME);
        }
        for (int i = 0; i < imageFiles.size(); i++) {
            if (imageFiles.get(i).getAbsolutePath().equals(file.getAbsolutePath())) {
                currentIndex = i;
            }
        }
    }

    private void updateImageView() {
        updateTile();
        imageView.setImage(image);

        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        //图片比面板大
        if (imageWidth > imagePane.getWidth() - MARGIN || imageHeight > imagePane.getHeight() - MARGIN) {
            imageView.fitWidthProperty().bind(imagePane.widthProperty().subtract(MARGIN));
            imageView.fitHeightProperty().bind(imagePane.heightProperty().subtract(MARGIN));
        } else {
            //图片比面板小
            imageView.fitWidthProperty().bind(image.widthProperty());
            imageView.fitHeightProperty().bind(image.heightProperty());
        }
    }

    /**
     * 更新窗口标题
     */
    private void updateTile() {
        ImageFile imageFile = imageFiles.get(currentIndex);
        stage.setTitle(String.format("%d/%d - %s(%.2fMB,%.0fx%.0f像素)",
                currentIndex + 1,
                imageFiles.size(),
                imageFile.getFileName(),
                imageFile.getSizeInMagaBytes(),
                image.getWidth(),
                image.getHeight()
        ));
    }

    /**
     * 图片放大
     */
    @FXML
    private void zoomIn() {
        if (scale < MAX_SCALE) {
            scale += 10;
            imageView.setScaleX(imageView.getScaleX() + 0.1);
            imageView.setScaleY(imageView.getScaleY() + 0.1);
        }
    }

    /**
     * 缩小
     */
    @FXML
    private void zoomOut() {
        if (scale > MIN_SCALE) {
            scale -= 10;
            imageView.setScaleX(imageView.getScaleX() - 0.1);
            imageView.setScaleY(imageView.getScaleY() - 0.1);
        }
    }

    /**
     * 恢复原比例与位置
     */
    @FXML
    private void originalScale() {
        scale = 100;
        imageView.setScaleX(1);
        imageView.setScaleY(1);
        imageView.setTranslateX(0);
        imageView.setTranslateY(0);
        imageView.setRotate(0);
    }

    /**
     * 鼠标滚动调节图片大小
     */
    @FXML
    private void scrollResize(ScrollEvent event) {
        double deltaY = event.getDeltaY();
        if (deltaY > 0) {
            zoomIn();
        } else {
            zoomOut();
        }
    }

    /**
     * 记录用户按下鼠标时的坐标
     */
    @FXML
    private void onMousePressed(MouseEvent e) {
        point = new Point2D(e.getX(), e.getY());
    }

    /**
     * 鼠标拖动图片
     */
    @FXML
    private void onMouseDragged(MouseEvent e) {
        //计算偏置
        double offsetX = e.getX() - point.getX();
        double offsetY = e.getY() - point.getY();

        //修正坐标
        double modifiedX = offsetX * imageView.getScaleX();
        double modifiedY = offsetY * imageView.getScaleY();

        //移动图片
        imageView.setTranslateX(imageView.getTranslateX() + modifiedX);
        imageView.setTranslateY(imageView.getTranslateY() + modifiedY);
    }

    /**
     * 向左旋转90度
     */
    @FXML
    private void turnLeft() {
        imageView.setRotate(imageView.getRotate() - 90);
    }

    /**
     * 向右旋转90度
     */
    @FXML
    private void turnRight() {
        imageView.setRotate(imageView.getRotate() + 90);
    }

    /**
     * 提示用户图片可以拖动
     */
    @FXML
    private void onMouseEntered() {
        imageView.setCursor(Cursor.CLOSED_HAND);
    }

    /**
     * 上一张图片
     */
    @FXML
    private void nextImage() {
        currentIndex = ++currentIndex >= imageFiles.size() ? 0 : currentIndex;
        showTips();
        image = new Image(imageFiles.get(currentIndex).getURL());
        updateImageView();
    }

    /**
     * 下一张图片
     */
    @FXML
    private void previousImage() {
        currentIndex = --currentIndex < 0 ? imageFiles.size() - 1 : currentIndex;
        showTips();
        image = new Image(imageFiles.get(currentIndex).getURL());
        updateImageView();
    }

    private void showTips() {
        if (currentIndex == imageFiles.size() - 1) {
            Notifications.create()
                    .text("最后一张")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.TOP_CENTER)
                    .owner(stage)
                    .darkStyle()
                    .show();
        } else if (currentIndex == 0) {
            Notifications.create()
                    .text("第一张")
                    .hideAfter(Duration.seconds(2))
                    .position(Pos.TOP_CENTER)
                    .owner(stage)
                    .show();
        }
    }

    /**
     * 弹出一个对话框以展示图片信息
     */
    @FXML
    private void showImageInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);
        alert.setTitle(imageFiles.get(currentIndex).getFileName() + " 属性");
        alert.setGraphic(null);
        alert.setHeaderText(null);

        VBox key = new VBox();
        key.getChildren()
                .addAll(new Label("图片名称：  "),
                        new Label("图片类型：  "),
                        new Label("图片大小：  "),
                        new Label("图片尺寸：  "),
                        new Label("图片位置：  "),
                        new Label("创建时间：  "),
                        new Label("修改时间：  "),
                        new Label("访问时间：  ")
                );

        ImageFile imageFile = imageFiles.get(currentIndex);
        VBox value = new VBox();
        value.getChildren()
                .addAll(new Label(imageFile.getFileName()),
                        new Label(imageFile.getFileType()),
                        new Label(String.format("%.2f", imageFile.getSizeInMagaBytes()) + "MB"),
                        new Label(image.getWidth() + "x" + image.getHeight()),
                        new Label(imageFile.getAbsolutePath()),
                        new Label(imageFile.getCreationTime()),
                        new Label(imageFile.getLastModifiedTime()),
                        new Label(imageFile.getLastAccessTime())
                );

        HBox hBox = new HBox();
        hBox.getChildren().addAll(key, value);


        key.setStyle("-fx-spacing: 15px");
        value.setStyle("-fx-spacing: 15px");

        alert.getDialogPane().setContent(hBox);
        alert.initModality(Modality.NONE);
        alert.show();
    }

    /**
     * 删除当前图片并自动切换到下一张图片
     */
    @FXML
    private void delete() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("确认删除吗？");
        alert.setContentText("删除后可以在系统回收站找回");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                FileUtil.delete(imageFiles.get(currentIndex).getFile());
                imageFiles.remove(imageFiles.get(currentIndex));
                nextImage();
                Platform.runLater(() -> mainWindowsController.updateFlowPane());
            }
        });
    }

    /**
     * 放映幻灯片
     */
    @FXML
    private void play() {
        //先弹窗询问播放间隔
        Dialog<Pair<Double, String>> dialog = new Dialog<>();
        dialog.setTitle("提示");
        dialog.setHeaderText("请输入播放时间");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));

        Spinner<Double> spinner = new Spinner<>(1.0, 600.0, 5.0, 1.0);
        spinner.setEditable(true);
        spinner.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);

        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll("顺序", "逆序");
        comboBox.getSelectionModel().select(0);

        grid.add(new Label("播放间隔(单位: 秒)"), 0, 0);
        grid.add(spinner, 1, 0);
        grid.add(new Label("播放顺序: "), 0, 1);
        grid.add(comboBox, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                if (spinner.getValue() != null) {
                    return new Pair<>(spinner.getValue(), comboBox.getValue());
                } else {
                    return new Pair<>(5.0, comboBox.getValue());
                }
            }
            return null;
        });

        Optional<Pair<Double, String>> result = dialog.showAndWait();

        result.ifPresent(e -> playing(e.getKey(), e.getValue()));
    }

    /**
     * 定时任务
     */
    private Timeline timeline;

    /**
     * 开始播放
     */
    private void playing(double interval, String order) {
        boolean isOrder = "顺序".equals(order);

        stage.setFullScreen(true);

        Notifications.create()
                .text("开始播放，按空格可暂停，按方向键可快速切换")
                .hideAfter(Duration.seconds(3))
                .position(Pos.TOP_CENTER)
                .owner(stage)
                .show();

        //先更新图片，之后开始定时任务
        stage.show();
        imagePane.requestFocus();
        updatePlayingImage();

        //定时任务
        timeline = new Timeline(new KeyFrame(Duration.seconds(interval), event -> {
            imagePane.requestFocus();
            if (isOrder) {
                nextImage();
                originalScale();
            } else {
                previousImage();
                originalScale();
            }
            updatePlayingImage();
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);

        timeline.play();

        //设置监听事件
        imagePane.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                //退出播放
                case ESCAPE -> {
                    timeline.stop();
                    stage.setFullScreen(false);
                    //清空监听事件
                    imagePane.setOnKeyPressed(e2 -> {
                    });
                }
                case SPACE -> {
                    if (timeline.getStatus() == Animation.Status.PAUSED) {
                        timeline.play();
                    } else {
                        timeline.pause();
                    }
                }
                case RIGHT -> {
                    nextImage();
                    updatePlayingImage();
                }
                case LEFT -> {
                    previousImage();
                    updatePlayingImage();
                }
            }
        });
    }

    /**
     * 更新播放时的图片
     * 与不播放时的区别是与面板之间没有间隔
     */
    private void updatePlayingImage() {
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();

        //图片比全屏大
        if (imageWidth > stage.getWidth() || imageHeight > stage.getHeight()) {
            imageView.fitWidthProperty().bind(stage.widthProperty());
            imageView.fitHeightProperty().bind(stage.heightProperty());
        } else {
            //图片比面板小
            imageView.fitWidthProperty().bind(image.widthProperty());
            imageView.fitHeightProperty().bind(image.heightProperty());
        }

        imageView.setImage(image);
    }

    /**
     *
     */
    public void playByMainWindow(Stage stage, String path) {
        init(stage, path);
        play();
        originalScale();
    }
}
