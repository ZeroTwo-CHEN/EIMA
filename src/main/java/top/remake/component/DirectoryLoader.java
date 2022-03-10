package top.remake.component;

import javafx.scene.control.TreeItem;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * @author ZeroTwo_CHEN
 */
public class DirectoryLoader implements Callable<List<? extends TreeItem<String>>> {

    private final File directory;

    public DirectoryLoader(File directory) {
        this.directory = directory;
    }

    private static final Comparator<File> COMPARATOR = (left, right) -> {
        boolean leftIsDir = left.isDirectory();
        if (leftIsDir ^ right.isDirectory()) {
            return leftIsDir ? -1 : 1;
        }
        return left.compareTo(right);
    };

    /**
     * 转换为FileTreeItem
     * @param file 待转换的文件
     * @return 结果
     */
    private TreeItem<String> toFileTreeItem(File file) {
        return file.isDirectory()
                ? new FileTreeItem(file, new DirectoryLoader(file))
                : null;
    }

    @Override
    public List<? extends TreeItem<String>> call() {
        return Arrays.stream(directory.listFiles())
                //过滤文件
                .filter(File::isDirectory)
                //排序
                //.sorted(COMPARATOR)
                //转换
                .map(this::toFileTreeItem)
                .collect(Collectors.toList());
    }
}
