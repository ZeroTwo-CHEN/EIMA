package top.remake.component;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import top.remake.entity.ImageFile;

/**
 * 缩略图面板
 *
 * @author ZeroTwo_CHEN
 */
public class ThumbnailPanel extends VBox {
    /**
     * 缩略图
     */
    private ImageView imageView;

    /**
     * 图片名称
     */
    private Label imageName;

    private  Boolean isChoice=false;
    public Boolean getIsChoice() {
        return isChoice;
    }

    public void setIsChoice(Boolean value) {
        this.isChoice = value;
    }


    public ThumbnailPanel(ImageFile imageFile) {
        //保持原比例，启用更好质量的加载算法，启用后台加载
        this.imageView = new ImageView(new Image(imageFile.getURL(),true));
        this.imageView.setFitWidth(100);
        this.imageView.setFitHeight(100);
        this.imageView.setPreserveRatio(true);
        this.imageName = new Label(imageFile.getFileName());
        this.imageName.setMaxWidth(90);
        BorderPane pane=new BorderPane();
        pane.setMaxSize(110,110);
        pane.setMinSize(110,110);
        pane.setCenter(imageView);
       // pane.setStyle("-fx-background-color: red");
       // this.imageName.
        this.imageName.setAlignment(Pos.CENTER);
       // this.imageName.setTextAlignment(TextAlignment.CENTER);
        getChildren().addAll(pane,imageName);
       this.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
           @Override
            public void handle(MouseEvent event) {
               setStyle("-fx-background-color: #cce8ff");
               isChoice=true;

            }
        });
        //this.addEventHandler();

    }

    /**
     * 标记该面板是否被选中
     */
    public void removeChoice(){
        this.isChoice=false;
        this.setStyle("-fx-background-color: white");
    }
    public void chooseIt(){
        this.isChoice=true;
    }
}
