package top.remake.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import top.remake.entity.BrushType;
import top.remake.entity.ImageFile;
import top.remake.utils.DrawShapeUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author ZeroTwo_CHEN
 */
public class EditWindowController implements Initializable {
    @FXML
    private ImageView imageView;

    @FXML
    private StackPane imagePane;

    /**
     * 当做画布用
     */
    @FXML
    private Group canvasGroup;

    @FXML
    private Button btnLine;

    @FXML
    private Button btnPencil;

    @FXML
    private Button btnRectangle;

    @FXML
    private Button btnCircle;

    @FXML
    private Slider brushWidth;

    @FXML
    private ColorPicker colorPicker;

    /**
     * 传入的图片文件
     */
    private File file;

    /**
     * 编辑的图片
     */
    private Image image;

    private Stage stage;

    private final DrawShapeUtil drawShapeUtil = new DrawShapeUtil();

    /**
     * 图片与窗口的间隔
     * 实际间隔为 MARGIN/2
     */
    private final static int MARGIN = 10;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initBrushType();
        drawShapeUtil.getStrokeWidthProperty().bind(brushWidth.valueProperty());
        drawShapeUtil.getBrushColorProperty().bind(colorPicker.valueProperty());
    }


    public void init(Stage stage, String path) {
        this.file = new File(path);
        this.stage = stage;
        this.image = new Image(file.toPath().toUri().toString());

        updateImageView();

    }

    private boolean isOutOfBounds;

    /**
     * 当鼠标按下时根据当前的画笔类型生成图形
     * 记录起始的位置
     */
    @FXML
    private void onMousePressed(MouseEvent e) {
        isOutOfBounds = false;
        Shape shape = drawShapeUtil.createShape(e.getX(), e.getY());
        shape.setMouseTransparent(true);
        canvasGroup.getChildren().add(shape);
    }

    /**
     * 鼠标拖动时实时更新
     */
    @FXML
    private void onMouseDragged(MouseEvent e) {
        if (!isOutOfBounds) {
            drawShapeUtil.updateShape(e.getX(), e.getY());
        }
    }

    /**
     * 防止画的图形超出画布
     */
    @FXML
    private void onMouseExited() {
        isOutOfBounds = true;
    }

    @FXML
    private void onMouseEntered() {
        isOutOfBounds = false;
    }

    /**
     * 保存
     */
    @FXML
    private void save() {
        WritableImage writableImage = canvasGroup.snapshot(null, null);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存图片");
        fileChooser.setInitialFileName("Edit-"+file.getName());
        fileChooser.setInitialDirectory(file.getParentFile());
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", ".png"));

        File file = fileChooser.showSaveDialog(canvasGroup.getScene().getWindow());

        if (file != null) {
            try {
                ImageIO.write(bufferedImage, "PNG", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void undo() {
        int size = canvasGroup.getChildren().size();
        if (size > 1) {
            canvasGroup.getChildren().remove(size - 1);
        }
    }

    private void updateTile() {
        ImageFile imageFile = new ImageFile(file);
        stage.setTitle(String.format("%s(%.2fMB,%.0fx%.0f像素)",
                imageFile.getFileName(),
                imageFile.getSizeInMagaBytes(),
                image.getWidth(),
                image.getHeight()
        ));
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
     * 实现笔刷类型的互斥
     */
    private void initBrushType() {
        btnPencil.setStyle("-fx-background-color: #ffd65b");
        btnLine.setOnAction(e -> {
            btnCircle.setStyle("-fx-background-color: transparent");
            btnPencil.setStyle("-fx-background-color: transparent");
            btnRectangle.setStyle("-fx-background-color: transparent");
            drawShapeUtil.setBrushType(BrushType.LINE);
            btnLine.setStyle("-fx-background-color: #ffd65b");
        });
        btnCircle.setOnAction(e -> {
            btnLine.setStyle("-fx-background-color: transparent");
            btnPencil.setStyle("-fx-background-color: transparent");
            btnRectangle.setStyle("-fx-background-color: transparent");
            drawShapeUtil.setBrushType(BrushType.ELLIPSE);
            btnCircle.setStyle("-fx-background-color: #ffd65b");
        });
        btnPencil.setOnAction(e -> {
            btnLine.setStyle("-fx-background-color: transparent");
            btnRectangle.setStyle("-fx-background-color: transparent");
            btnCircle.setStyle("-fx-background-color: transparent");
            drawShapeUtil.setBrushType(BrushType.PENCIL);
            btnPencil.setStyle("-fx-background-color: #ffd65b");
        });
        btnRectangle.setOnAction(e -> {
            btnLine.setStyle("-fx-background-color: transparent");
            btnPencil.setStyle("-fx-background-color: transparent");
            btnCircle.setStyle("-fx-background-color: transparent");
            drawShapeUtil.setBrushType(BrushType.RECTANGLE);
            btnRectangle.setStyle("-fx-background-color: #ffd65b");
        });
    }
}
