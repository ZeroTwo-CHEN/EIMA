package top.remake.component;

import com.leewyatt.rxcontrols.controls.RXHighlightText;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import top.remake.DisplayWindow;
import top.remake.controller.ControllerMap;
import top.remake.controller.MainWindowController;
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

    private static final MainWindowController MAIN_WINDOWS_CONTROLLER;

    private static TextField searchKey;
    private Tooltip tooltip;

    /**
     * 图片的阴影效果
     */
    private static final DropShadow DROP_SHADOW = new DropShadow(8, 3, 3, Color.GRAY);

    /**
     * 边距
     */
    private static final Insets INSETS = new Insets(5, 5, 0, 5);


    static {
        MAIN_WINDOWS_CONTROLLER = (MainWindowController) ControllerMap.getController(MainWindowController.class);
        searchKey = MAIN_WINDOWS_CONTROLLER.getSearchField();
    }

    public ThumbnailPanel(ImageFile imageFile,double size) {
        this.setMaxSize(size+10, size+50);
        this.setMinSize(size+10, size+50);
        //关闭cache防止占用内存过大
        this.setCache(false);
        this.setPadding(INSETS);
        this.imageFile = imageFile;
        //保持原比例，启用更好质量的加载算法，启用后台加载
        this.imageView = new ImageView(new Image(imageFile.getURL(), 90, 90, true, true, true));
        this.imageView.setFitWidth(size);
        this.imageView.setFitHeight(size);
        this.imageView.setPreserveRatio(true);

        //给图片添加阴影效果
        this.imageView.setEffect(DROP_SHADOW);

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

        this.isSelected = false;
        this.imageName.setTextAlignment(TextAlignment.CENTER);
        setCenter(imageView);
        setBottom(imageName);


        this.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                String[] args = {imageFile.getAbsolutePath()};
                Platform.runLater(() -> DisplayWindow.main(args));
            }
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                PreviewFlowPane parent = (PreviewFlowPane) this.getParent();
                //按下Ctrl键时对图片进行选择
                if (event.isControlDown()) {
                    //图片已经选中，则取消选中
                    if (this.getIsSelected()) {
                        parent.deleteImgFromList(this);
                    }
                    //图片未被选中，则选择该图片
                    else {
                        parent.addImgToList(this);
                    }
                }
                //shift多选
                else if(event.isShiftDown()){
                    parent.setTo(parent.getThumbnailPanels().indexOf(this));
                    parent.shiftSelect();
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
                        //标记为shift多选的起始位置
                        parent.setShiftSign(true);
                        parent.setFrom(parent.getThumbnailPanels().indexOf(this));
                    }

                }
                //更新主界面右下角提示栏
                MAIN_WINDOWS_CONTROLLER.updateTipsLabelText(parent.getTotalCount(), parent.getTotalSize(),
                        parent.getSelectedCount(), parent.getSelectedSize());
            }
        });

        this.setOnMouseEntered(e -> toolTips());
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

    /**
     * 添加图片信息
     */
    private void toolTips() {
        String format = String.format("%.2f", this.imageFile.getSizeInMagaBytes());
        tooltip = new Tooltip("名称:" + this.imageFile.getFileName() + "\n类型:" + this.imageFile.getFileType() + "\n大小:" + format + "MB");
        tooltip.setAutoHide(true);
        tooltip.setAutoFix(true);
        Tooltip.install(this, tooltip);
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
