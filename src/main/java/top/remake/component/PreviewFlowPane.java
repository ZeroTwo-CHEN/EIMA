package top.remake.component;

import javafx.scene.layout.FlowPane;
import top.remake.controller.ControllerMap;
import top.remake.controller.MainWindowController;
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

    public File getDirectory() {
        return directory;
    }

    /**
     * 当前展示的文件夹
     */
    private File directory;

    /**
     * 被选中图片的数组
     */
    private List<ThumbnailPanel> newChoices = new ArrayList<>();
    private List<ThumbnailPanel> oldChoices = new ArrayList<>();
    private MainWindowController mainWindowController;


    public PreviewFlowPane() {
        setCache(true);
        setVgap(5);
        setHgap(5);
    }


    public void update(File directory) {
        clearSelect();
        this.directory = directory;
        if (mainWindowController == null) {
            mainWindowController = (MainWindowController) ControllerMap.getController(MainWindowController.class);
        }
        this.thumbnailPanels.clear();
        //TODO: 优化加载效率，读取到文件之后不要一次性加载所有缩略图
        File[] files = directory.listFiles(FileUtil::isSupportImageFormat);
        if (files != null) {
            for (File file : files) {
                ImageFile imageFile = new ImageFile(file);
                ThumbnailPanel thumbnailPanel = new ThumbnailPanel(imageFile);
                this.thumbnailPanels.add(thumbnailPanel);
            }
        }
        SortUtil.sortThumbnailPanel(this.thumbnailPanels, mainWindowController.getSortOrder());
        getChildren().setAll(this.thumbnailPanels);
        //更新主界面右下角提示栏
        mainWindowController.updateTipsLabelText(getTotalCount(), getTotalSize(), getSelectedCount(), getSelectedSize());
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

    public void addSelect(ThumbnailPanel pane) {
        newChoices.add(pane);
        pane.select();
    }

    /**
     * 清空存放的已选择图片
     */
    public void clearSelect() {
        oldChoices.clear();
        oldChoices.addAll(newChoices);
        for (ThumbnailPanel pane : newChoices) {
            pane.removeSelect();
        }
        newChoices.clear();
    }

    /**
     * 删除一张已选择的图片
     */
    public void deleteImgFromList(Object obj) {
        ThumbnailPanel img = (ThumbnailPanel) obj;
        oldChoices.clear();
        oldChoices.addAll(newChoices);
        img.removeSelect();
        newChoices.remove(img);

    }

    /**
     * 增加一张图片到已选择图片中
     */
    public void addImgToList(Object obj) {
        ThumbnailPanel img = (ThumbnailPanel) obj;
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
    public void selectAll() {
        oldChoices.clear();
        oldChoices.addAll(newChoices);
        for (ThumbnailPanel img : thumbnailPanels) {
            if (img.getIsSelected()) {
                img.removeSelect();
                newChoices.remove(img);
            } else {
                img.select();
                newChoices.add(img);
            }
        }
    }

    /**
     * @return 当前目录的图片总数
     */
    public int getTotalCount() {
        return thumbnailPanels.size();
    }

    /**
     * @return 所有图片的总大小
     */
    public double getTotalSize() {
        double totalSize = 0.0;
        for (ThumbnailPanel thumbnailPanel : thumbnailPanels) {
            totalSize += thumbnailPanel.getImageFile().getSizeInMagaBytes();
        }
        return totalSize;
    }

    /**
     * @return 被选中的图片的数量
     */
    public int getSelectedCount() {
        return newChoices.size();
    }

    /**
     * @return 被选中的图片的大小
     */
    public double getSelectedSize() {
        double size = 0.0;
        for (ThumbnailPanel thumbnailPanel : newChoices) {
            size += thumbnailPanel.getImageFile().getSizeInMagaBytes();
        }
        return size;
    }

    public List<ThumbnailPanel> getNewChoices() {
        return newChoices;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getFrom() {
        return from;
    }

    private int from;

    public void setTo(int to) {
        this.to = to;
    }

    private int to;

    public void setShiftSign(Boolean shiftSign) {
        this.shiftSign = shiftSign;
    }

    private Boolean shiftSign=false;

    public void shiftSelect(){
        clearSelect();
        if(!shiftSign){
            from=0;
        }
        if(from>to){
            newChoices.addAll(thumbnailPanels.subList(to,from+1));
        }
        else {
            newChoices.addAll(thumbnailPanels.subList(from,to+1));
        }
        for(ThumbnailPanel pane:newChoices){
            pane.select();
        }
    }
}
