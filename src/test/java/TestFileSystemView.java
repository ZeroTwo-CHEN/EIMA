import org.junit.jupiter.api.Test;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
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
}
