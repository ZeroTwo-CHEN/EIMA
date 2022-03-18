package top.remake.component;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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

    public ThumbnailPanel(ImageFile imageFile) {
        //保持原比例，启用更好质量的加载算法，启用后台加载
        this.imageView = new ImageView(new Image(imageFile.getURL(),
                100,
                100,
                true,
                true,
                true));
        this.imageName = new Label(imageFile.getFileName());

        getChildren().addAll(this.imageView, this.imageName);
    }
}
