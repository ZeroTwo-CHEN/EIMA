package top.remake.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.name.Rename;
import top.remake.entity.ImageFile;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 工具类
 *
 * @author ZeroTwo_CHEN
 */
public class FileUtil {

    private FileUtil() {
    }

    /**
     * 获取文件系统中的名字
     */
    public static String getFilename(File file) {
        return FileSystemView.getFileSystemView().getSystemDisplayName(file);
    }

    /**
     * 获取文件系统中的图标
     */
    public static ImageView getFileIcon(File file) {
        Image image = ((ImageIcon) FileSystemView.getFileSystemView().getSystemIcon(file)).getImage();
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.BITMASK);
        bufferedImage.createGraphics().drawImage(image, 0, 0, null);
        WritableImage writableImage = SwingFXUtils.toFXImage(bufferedImage, null);
        return new ImageView(writableImage);
    }

    /**
     * 判断照片是否是支持的格式
     */
    public static boolean isSupportImageFormat(File file) {
        String fileName = file.getName().toUpperCase();
        //支持的照片格式：.JPG、.JPEG、.GIF、.PNG、和.BMP。
        return fileName.endsWith("JPG") ||
                fileName.endsWith("JPEG") ||
                fileName.endsWith("GIF") ||
                fileName.endsWith("PNG") ||
                fileName.endsWith("BMP");
    }

    /**
     * 删除图片
     */
    public static void delete(File file) {
        Desktop.getDesktop().moveToTrash(file);
    }

    /**
     * 压缩照片
     *
     * @param imageFile 要压缩的照片
     * @param quality    压缩质量 取值0-1
     */
    public static void compressImage(ImageFile imageFile, double quality) throws IOException {
            File file = imageFile.getFile();
            if ("PNG".equals(imageFile.getFileType())) {
                Thumbnails.of(file)
                        .scale(1)
                        .outputQuality(quality)
                        .outputFormat("jpg")
                        .toFiles(file.getParentFile(), Rename.PREFIX_HYPHEN_THUMBNAIL);
            }else {
                Thumbnails.of(file)
                        .scale(1)
                        .outputQuality(quality)
                        .toFiles(file.getParentFile(), Rename.PREFIX_HYPHEN_THUMBNAIL);
            }
    }
}
