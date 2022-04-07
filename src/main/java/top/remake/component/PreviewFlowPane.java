package top.remake.component;

import javafx.scene.layout.FlowPane;
import top.remake.controller.ControllerMap;
import top.remake.controller.MainWindowsController;
import top.remake.entity.ImageFile;
import top.remake.utils.FileUtil;
import top.remake.utils.SortUtil;

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
     * 当前展示的文件夹
     */
    private File directory;

    /**
     * 被选中图片的数组
     */
    private List<ThumbnailPanel> newChoices =new ArrayList<>();
    private List<ThumbnailPanel> oldChoices=new ArrayList<>();
    private double x,y,x2,y2;
    private MainWindowsController mainWindowsController;

    public PreviewFlowPane() {
        setCache(true);
        setVgap(5);
        setHgap(5);
    }


    public void update(File directory) {
        this.directory = directory;
        if (mainWindowsController == null) {
            mainWindowsController = (MainWindowsController) ControllerMap.getController(MainWindowsController.class);
        }
        this.thumbnailPanels.clear();
        File[] files = directory.listFiles(FileUtil::isSupportImageFormat);
        if (files != null) {
            for (File file : files) {
                ImageFile imageFile = new ImageFile(file);
                ThumbnailPanel thumbnailPanel = new ThumbnailPanel(imageFile);
                this.thumbnailPanels.add(thumbnailPanel);
            }
        }
        SortUtil.sortThumbnailPanel(this.thumbnailPanels, mainWindowsController.getSortOrder());
        getChildren().setAll(this.thumbnailPanels);
        double totalSize = 0.0;
        for (ThumbnailPanel thumbnailPanel : thumbnailPanels) {
            totalSize += thumbnailPanel.getImageFile().getSizeInMagaBytes();
        }
        mainWindowsController.updateTipsLabelText(thumbnailPanels.size(), totalSize);
    }

    /**
     * 当改变排序方式时刷新缩略图面板
     */
    public void update() {
        if (directory != null) {
            update(directory);
        }
    }

    public List<ThumbnailPanel> getThumbnailPanels() {
        return thumbnailPanels;
    }

    public void addSelect(ThumbnailPanel pane){
        newChoices.add(pane);
        pane.select();
    }

    /**
     * 清空存放的已选择图片
     */
    public void clearSelect(){
        oldChoices.clear();
        oldChoices.addAll(newChoices);
        for(ThumbnailPanel pane :newChoices) {
            pane.removeSelect();
        }
        newChoices.clear();
    }

    /**
     *删除一张已选择的图片
     */
    public void deleteImgFromList(Object obj){
        ThumbnailPanel img=(ThumbnailPanel) obj;
        oldChoices.clear();
        oldChoices.addAll(newChoices);
        img.removeSelect();
        newChoices.remove(img);

    }

    /**
     *增加一张图片到已选择图片中
     */
    public void addImgToList(Object obj){
        ThumbnailPanel img=(ThumbnailPanel) obj;
        oldChoices.clear();
        oldChoices.addAll(newChoices);
        newChoices.add(img);
        img.select();
    }

    /**
     * 全选（反选）
     * 选择对未选择的图片
     * 取消选择已选择的图片
     */
    public void selectAll(){
        oldChoices.clear();
        oldChoices.addAll(newChoices);
        for(ThumbnailPanel img:thumbnailPanels) {
            if (img.getIfSelected()) {
                img.removeSelect();
                newChoices.remove(img);
            }
            else {
                img.select();
                newChoices.add(img);
            }
        }
    }
}
