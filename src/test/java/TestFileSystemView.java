import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileSystemView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFileSystemView {
    @Test
    public void test01() {
        File[] roots = FileSystemView.getFileSystemView().getRoots();
        File[] files = roots[0].listFiles();
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }
    }

    @Test
    public void test02() {
        File[] files = File.listRoots();
        for (File file : files) {
            System.out.println(file.getAbsolutePath());
        }
    }

    @Test
    public void test03() {
        File f = FileSystemView.getFileSystemView().getRoots()[0];
        File[] allFiles = f.listFiles();
        File[] directorFiles = f.listFiles(File::isDirectory);
        List<File> list = new ArrayList<>(Arrays.asList(allFiles));
        list.removeAll(Arrays.asList(directorFiles));
        for (File file : list) {
            if (file.isDirectory() && !file.getName().endsWith("lnk")) {
                System.out.println(file.getAbsolutePath());
            }
        }
    }

    @Test
    public void test04() {
        LocalDateTime dateTime = LocalDateTime.now();
        //使用预定义实例来转换
        DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
        String dateStr = dateTime.format(fmt);
        System.out.println("LocalDateTime转String[预定义]:" + dateStr);
        //使用pattern来转换
        //12小时制与24小时制输出由hh的大小写决定
        DateTimeFormatter fmt12 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS");
        String dateStr12 = dateTime.format(fmt12);
        System.out.println("LocalDateTime转String[pattern](12小时制):" + dateStr12);
        DateTimeFormatter fmt24 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
        String dateStr24 = dateTime.format(fmt24);
        System.out.println("LocalDateTime转String[pattern](24小时制):" + dateStr24);
        //如果想要给12小时制时间加上am/pm,这样子做：
        fmt12 = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss:SSS a");
        dateStr12 = dateTime.format(fmt12);
        System.out.println("LocalDateTime转String[pattern](12小时制带am/pm):" + dateStr12);
        System.out.println(fmt24.format(dateTime));
    }

    @Test
    public void test05() {
        File file = new File("C:\\Users\\cjhaz\\Pictures\\God of War\\ScreenShot-2022-2-1_10-46-30.png");
        System.out.println(file.getName().toUpperCase().substring(file.getName().lastIndexOf(".") + 1));
        try {
            BufferedImage read = ImageIO.read(new FileInputStream(file));
            int pixelSize = read.getColorModel().getPixelSize();
            System.out.println(pixelSize);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
