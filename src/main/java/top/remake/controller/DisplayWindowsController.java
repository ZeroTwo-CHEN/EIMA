package top.remake.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import top.remake.entity.ImageFile;
import top.remake.entity.SortOrder;
import top.remake.utils.FileUtil;
import top.remake.utils.SortUtil;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author ZeroTwo_CHEN
 */
public class DisplayWindowsController implements Initializable {
    @FXML
    private ImageView imageView;

    @FXML
    private AnchorPane imagePane;

    private File directory;

    private Stage stage;

    private List<ImageFile> imageFiles = new ArrayList<>();

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void init(Stage stage, String path) {
        File file = new File(path);
        this.directory = file.getParentFile();
        this.stage = stage;
        File[] images = directory.listFiles(FileUtil::isSupportImageFormat);
        if (images != null) {
            for (File image : images) {
                ImageFile imageFile = new ImageFile(image);
                this.imageFiles.add(imageFile);
            }
        }
        initImageList();

        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();

        Image image = new Image(file.toPath().toUri().toString(), true);
        imageView.setImage(image);
        imageView.setFitWidth(stageWidth - 20);
    }

    private void initImageList() {
        MainWindowsController controller = (MainWindowsController) ControllerMap.getController(MainWindowsController.class);
        if (controller != null) {
            SortUtil.sortImageFile(imageFiles, controller.getSortOrder());
        } else {
            SortUtil.sortImageFile(imageFiles, SortOrder.ASC_SORT_BY_NAME);
        }
    }

    private void setTile() {

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
}
