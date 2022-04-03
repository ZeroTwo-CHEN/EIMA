package top.remake.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import top.remake.DisplayWindow;
import top.remake.entity.ImageFile;

/**
 * 缩略图面板
 *
 * @author ZeroTwo_CHEN
 */
public class ThumbnailPanel extends VBox {
    /**
     * Image
     */
    private ImageFile imageFile;

    /**
     * 缩略图
     */
    private ImageView imageView;

    /**
     * 图片名称
     */
    private Label imageName;


    public ThumbnailPanel(ImageFile imageFile) {
        //关闭cache防止占用内存过大
        this.setCache(false);
        this.imageFile = imageFile;
        //保持原比例，启用更好质量的加载算法，启用后台加载
        this.imageView = new ImageView(new Image(imageFile.getURL(), true));
        this.imageView.setFitWidth(100);
        this.imageView.setFitHeight(100);
        this.imageView.setPreserveRatio(true);
        this.imageName = new Label(imageFile.getFileName());
        this.imageName.setMaxWidth(90);
        BorderPane pane = new BorderPane();
        pane.setMaxSize(110, 110);
        pane.setMinSize(110, 110);
        pane.setCenter(imageView);
        this.imageName.setAlignment(Pos.CENTER);
        getChildren().addAll(pane, imageName);
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            setStyle("-fx-background-color: #cce8ff");

        });
        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String[] args = {imageFile.getAbsolutePath()};
//                Platform.runLater(() -> {
//                    try {
//                        new DisplayWindow().start(new Stage());
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                });
                DisplayWindow.main(args);
            }
        });
    }

    /**
     * 标记该面板是否被选中
     */
    public  void select(){
        this.setStyle("-fx-background-color: rgba(0.5,0.5,1,.8)");
    }
    public void removeSelect(){
        this.setStyle("-fx-background-color: white");
    }




    public ImageFile getImageFile() {
        return imageFile;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Label getImageName() {
        return imageName;
    }
}
