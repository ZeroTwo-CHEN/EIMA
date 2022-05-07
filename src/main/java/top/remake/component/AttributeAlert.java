package top.remake.component;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import top.remake.entity.ImageFile;
import top.remake.utils.FileUtil;

/**
 * @author ZeroTwo_CHEN
 */
public class AttributeAlert extends Alert {

    private static final VBox KEY = new VBox();

    static {
        KEY.getChildren()
                .addAll(new Label("图片名称：  "),
                        new Label("图片类型：  "),
                        new Label("图片大小：  "),
                        new Label("图片尺寸：  "),
                        new Label("图片位置：  "),
                        new Label("创建时间：  "),
                        new Label("修改时间：  "),
                        new Label("访问时间：  ")
                );
        KEY.setStyle("-fx-spacing: 15px");
    }

    public AttributeAlert(ImageFile file, Image image) {
        super(Alert.AlertType.INFORMATION);
        initStyle(StageStyle.UTILITY);
        setTitle(file.getFileName() + " 属性");
        setGraphic(null);
        setHeaderText(null);

        VBox value = new VBox();
        value.getChildren()
                .addAll(new Label(file.getFileName()),
                        new Label(file.getFileType()),
                        new Label(FileUtil.fileSizeByString(file.getSizeInBytes())),
                        new Label(image.getWidth() + "x" + image.getHeight()),
                        new Label(file.getAbsolutePath()),
                        new Label(file.getCreationTime()),
                        new Label(file.getLastModifiedTime()),
                        new Label(file.getLastAccessTime())
                );
        value.setStyle("-fx-spacing: 15px");

        HBox hBox = new HBox();
        hBox.getChildren().addAll(KEY, value);

        getDialogPane().setContent(hBox);
        initModality(Modality.NONE);
    }
}
