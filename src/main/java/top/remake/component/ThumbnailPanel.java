package top.remake.component;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.TextAlignment;
import top.remake.DisplayWindow;
import top.remake.controller.ControllerMap;
import top.remake.controller.MainWindowsController;
import top.remake.entity.ImageFile;

/**
 * 缩略图面板
 *
 * @author ZeroTwo_CHEN
 */
public class ThumbnailPanel extends BorderPane {
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

    private static MainWindowsController mainWindowsControl;

    static {
        mainWindowsControl = (MainWindowsController) ControllerMap.getController(MainWindowsController.class);
    }

    public ThumbnailPanel(ImageFile imageFile) {
        this.setMaxSize(110, 150);
        this.setMinSize(110, 150);
        //关闭cache防止占用内存过大
        this.setCache(false);
        this.setPadding(new Insets(0, 5, 0, 5));
        this.imageFile = imageFile;
        //保持原比例，启用更好质量的加载算法，启用后台加载
        this.imageView = new ImageView(new Image(imageFile.getURL(), 90, 90, true, true, true));
        this.imageView.setFitWidth(100);
        this.imageView.setFitHeight(100);
        this.imageView.setPreserveRatio(true);
        this.imageName = new Label(imageFile.getFileName());
        this.imageName.setWrapText(true);
        this.imageName.setMaxHeight(50);
        ifSelected = false;
        this.imageName.setTextAlignment(TextAlignment.CENTER);
        setCenter(imageView);
        setBottom(imageName);


        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String[] args = {imageFile.getAbsolutePath()};
                Platform.runLater(() -> DisplayWindow.main(args));
            }
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                //按下Ctrl键时对图片进行选择
                PreviewFlowPane parent = (PreviewFlowPane) this.getParent();
                if (event.isControlDown()) {
                    //图片已经选中，则取消选中
                    if (this.getIfSelected()) {
                        // this.ifSelected = false;
                        parent.deleteImgFromList(this);
                    }
                    //图片未被选中，则选择该图片
                    else {
                        //   this.ifSelected = true;
                        parent.addImgToList(this);
                    }
                    //更新主界面右下角提示栏
                    mainWindowsControl.updateTipsLabelText(parent.getTotalCount(), parent.getTotalSize(),
                            parent.getSelectedCount(), parent.getSelectedSize());
                }
                //图片单选
                else {
                    //如果图片已经被选中，则取消选中
                    if (this.getIfSelected()) {
                        parent.clearSelect();
                    }
                    //否则就选中图片
                    else {
                        parent.clearSelect();
                        parent.addImgToList(this);
                    }
                }
            }
        });
    }

    /**
     * 标记该面板是否被选中
     */
    public void select() {
        this.setStyle("-fx-background-color: #cce8ff");
        this.ifSelected = true;
    }

    public void removeSelect() {
        this.setStyle("-fx-background-color: transparent");
        this.ifSelected = false;
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
