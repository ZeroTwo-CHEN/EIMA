package top.remake.component;

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

    public PreviewFlowPane() {
        setCache(true);
        setVgap(5);
    }

    public void update(File directory) {
        this.thumbnailPanels.clear();
        File[] files = directory.listFiles(FileUtil::isSupportImageFormat);
        if (files != null) {
            for (File file : files) {
                ImageFile imageFile = new ImageFile(file, 100, 100);
                ThumbnailPanel thumbnailPanel = new ThumbnailPanel(imageFile);
                this.thumbnailPanels.add(thumbnailPanel);
            }
        }
        for (ThumbnailPanel thumbnailPanel : thumbnailPanels) {
            getChildren().add(thumbnailPanel);
        }
        //getChildren().setAll(thumbnailPanels);
    }
}
