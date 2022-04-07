package top.remake.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
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

    private Boolean ifSelected;


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
        ifSelected=false;
        this.imageName.setAlignment(Pos.CENTER);
        getChildren().addAll(pane, imageName);


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
            if(event.getButton()==MouseButton.PRIMARY&&event.getClickCount()==1){


                //按下Ctrl键时对图片进行选择
                if(event.isControlDown()){
                    PreviewFlowPane parent=(PreviewFlowPane) this.getParent();
                    //图片已经选中，则取消选中
                    if(this.getIfSelected()) {
                       // this.ifSelected = false;
                        parent.deleteImgFromList((Object) this);
                    }
                    //图片未被选中，则选择该图片
                    else {
                     //   this.ifSelected = true;
                        parent.addImgToList((Object) this);
                    }
                }
                //图片单选
                else{
                    PreviewFlowPane parent=(PreviewFlowPane) this.getParent();

                    //如果图片已经被选中，则取消选中
                    if(this.getIfSelected()){
                        parent.clearSelect();
                    }
                    //否则就选中图片
                    else{
                        parent.clearSelect();
                        parent.addImgToList((Object) this);
                    }
                }
            }
        });
    }

    /**
     * 标记该面板是否被选中
     */
    public  void select(){
        this.setStyle("-fx-background-color: #2894FF");
        this.ifSelected=true;
    }
    public void removeSelect(){
        this.setStyle("-fx-background-color: #ffffff");
        this.ifSelected=false;
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

    public Boolean getIfSelected() {
        return ifSelected;
    }

    public void setIfSelected(Boolean ifSelected) {
        this.ifSelected = ifSelected;
    }
}
