package top.remake.component;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import top.remake.entity.ImageFile;
import top.remake.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片预览面板
 *
 * @author ZeroTwo_CHEN
 */
public class PreviewFlowPane extends FlowPane {
    /**
     * 照片列表
     */
    private List<ThumbnailPanel> thumbnailPanels = new ArrayList<>();

    /**
     * 被选中图片的数组
     */
    private List<ThumbnailPanel> newChoices =new ArrayList<>();
    private List<ThumbnailPanel> oldChoices=new ArrayList<>();
    private double x,y,x2,y2;

    public PreviewFlowPane() {
        setCache(true);
        setVgap(10);
        setHgap(10);
        setStyle("-fx-background-color: transparent");
        this.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            x=event.getX();
            y=event.getY();
        });
        this.addEventHandler(MouseEvent.MOUSE_RELEASED,event -> {

        });

    }

    public void update(File directory) {
        this.thumbnailPanels.clear();
        File[] files = directory.listFiles(FileUtil::isSupportImageFormat);
        if (files != null) {
            for (File file : files) {
                ImageFile imageFile = new ImageFile(file, 110, 110);
                ThumbnailPanel thumbnailPanel = new ThumbnailPanel(imageFile);
                this.thumbnailPanels.add(thumbnailPanel);
            }
        }
        getChildren().setAll(thumbnailPanels);
    }
}
