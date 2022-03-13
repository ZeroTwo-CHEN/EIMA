package top.remake.entity;

import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 图片相关信息的包装类
 *
 * @author ZeroTwo_CHEN
 */
public class ImageFile {
    private static final String DEFAULT_VALUE = "NULL";

    /**
     * 图片的file对象
     */
    private final File file;

    /**
     * Image
     */
    private Image image;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 绝对路径
     */
    private String absolutePath;

    /**
     * 图片大小 单位为字节
     */
    private long sizeInBytes;

    /**
     * 图片大小 单位为MB
     */
    private double sizeInMagaBytes;

    /**
     * 时间格式
     */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm:ss");

    /**
     * 文件创建时间
     */
    private String creationTime = DEFAULT_VALUE;

    /**
     * 修改时间
     */
    private String lastModifiedTime = DEFAULT_VALUE;

    /**
     * 访问时间
     */
    private String lastAccessTime = DEFAULT_VALUE;

    /**
     * 图像宽度
     */
    private double imageWidth;

    /**
     * 图像高度
     */
    private double imageHeight;


    public ImageFile(File file, double requestedWidth, double requestedHeight) {
        this.file = file;
        //保持原比例，启用更好质量的加载算法，启用后台加载
        this.image = new Image(file.toString(), requestedWidth, requestedHeight, true, true, true);
        this.fileName = file.getName();
        this.fileType = this.fileName.toUpperCase().substring(this.fileName.lastIndexOf(".") + 1);
        this.absolutePath = file.getAbsolutePath();
        this.sizeInBytes = file.length();
        this.sizeInMagaBytes = file.length() / (1024.0 * 1024);
        this.imageWidth = this.image.getWidth();
        this.imageHeight = this.image.getHeight();

        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            LocalDateTime createLocalDateTime = attrs.creationTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime lastModifiedLocalDateTime = attrs.lastModifiedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime lastAccessLocalDateTime = attrs.lastAccessTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            if (createLocalDateTime != null) {
                this.creationTime = DATE_TIME_FORMATTER.format(createLocalDateTime);
                this.lastModifiedTime = DATE_TIME_FORMATTER.format(lastModifiedLocalDateTime);
                this.lastAccessTime = DATE_TIME_FORMATTER.format(lastAccessLocalDateTime);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
