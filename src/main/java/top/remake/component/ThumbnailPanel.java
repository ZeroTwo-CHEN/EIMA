package top.remake.component;

import com.leewyatt.rxcontrols.controls.RXHighlightText;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
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
     * 能显示的最长文件名
     */
    private static final int MAX_LENGTH = 25;

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
    private RXHighlightText imageName;

    private Boolean isSelected;

    private static MainWindowsController mainWindowsControl;

    private static TextField searchKey;

    static {
        mainWindowsControl = (MainWindowsController) ControllerMap.getController(MainWindowsController.class);
        searchKey = mainWindowsControl.getSearchField();
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

        int length = imageFile.getFileName().length();
        //名字长度大于限定就剪切
        if (length > MAX_LENGTH) {
            this.imageName = new RXHighlightText(imageFile.getFileName().substring(0, MAX_LENGTH) + "...");
        } else {
            this.imageName = new RXHighlightText(imageFile.getFileName());
        }
        this.imageName.setMaxHeight(50);
        this.imageName.setPrefHeight(50);

        //绑定首页搜索框
        this.imageName.keywordsProperty().bind(searchKey.textProperty());
        this.imageName.setMatchRules(RXHighlightText.MatchRules.IGNORE_CASE);

        isSelected = false;
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
                    if (this.getIsSelected()) {
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
                    if (this.getIsSelected()) {
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
        this.isSelected = true;
    }

    public void removeSelect() {
        this.setStyle("-fx-background-color: transparent");
        this.isSelected = false;
    }


    public ImageFile getImageFile() {
        return imageFile;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public RXHighlightText getImageName() {
        return imageName;
    }

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean isSelected) {
        this.isSelected = isSelected;
    }
}
