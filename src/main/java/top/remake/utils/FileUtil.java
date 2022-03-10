package top.remake.utils;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

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
}
