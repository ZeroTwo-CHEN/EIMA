package top.remake.component;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.TreeItem;
import top.remake.utils.FileUtil;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Supplier;

/**
 * 用于文件夹的TreeItem
 *
 * @author ZeroTwo_CHEN
 */
public class FileTreeItem extends TreeItem<String> {
    /**
     * 用于标记该item是否初始化过
     */
    private boolean isInitialized = false;

    /**
     * 该节点对应的文件夹
     */
    private final File directory;

    /**
     * 用于异步加载子文件夹
     */
    private CompletableFuture<?> future;

    /**
     * 回调
     */
    private final Callable<List<? extends TreeItem<String>>> callable;

    private static final EventType<?> PRE_ADD_LOADED_CHILDREN
            = new EventType<>(treeNotificationEvent(), "PRE_ADD_LOADED_CHILDREN");
    private static final EventType<?> POST_ADD_LOADED_CHILDREN
            = new EventType<>(treeNotificationEvent(), "POST_ADD_LOADED_CHILDREN");

    @SuppressWarnings("unchecked")
    static <T> EventType<TreeModificationEvent<T>> preAddLoadedChildrenEvent() {
        return (EventType<TreeModificationEvent<T>>) PRE_ADD_LOADED_CHILDREN;
    }

    @SuppressWarnings("unchecked")
    static <T> EventType<TreeModificationEvent<T>> postAddLoadedChildrenEvent() {
        return (EventType<TreeModificationEvent<T>>) POST_ADD_LOADED_CHILDREN;
    }

    public FileTreeItem(File directory, Callable<List<? extends TreeItem<String>>> callable) {
        super(FileUtil.getFilename(directory), FileUtil.getFileIcon(directory));
        this.directory = directory;
        this.callable = callable;
        super.getChildren().add(new TreeItem<>());
        addExpandedListener();
    }

    /**
     * 重写该方法，使得该文件夹在展开时加载其子文件夹
     */
    @Override
    public ObservableList<TreeItem<String>> getChildren() {
        if (!isInitialized) {
            isInitialized = true;
            //开始异步加载
            future = CompletableFuture.supplyAsync(new CallableToSupplierAdapter<>(callable))
                    .whenCompleteAsync(this::handleAsyncLoadComplete, Platform::runLater);
        }
        return super.getChildren();
    }

    /**
     * 用于控制展开时是否要重新加载
     */
    @Override
    public boolean isLeaf() {
        return super.isLeaf();
        //以下语句为不重新加载
        //return !directory.isDirectory();
    }

    /**
     * 展开监听器
     * 当未加载完成时关闭的item 则取消异步加载
     */
    @SuppressWarnings("unchecked")
    private void addExpandedListener() {
        expandedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                isInitialized = false;
                if (future != null) {
                    future.cancel(true);
                }
                super.getChildren().setAll(new TreeItem<>());
            }
        });
    }

    /**
     * 异步加载完成的处理
     * @param result 储存加载完成后的item
     * @param th 异常
     */
    private void handleAsyncLoadComplete(List<? extends TreeItem<String>> result, Throwable th) {
        if (th != null) {
            Thread.currentThread().getUncaughtExceptionHandler()
                    .uncaughtException(Thread.currentThread(), th);
        } else {
            Event.fireEvent(this, new TreeModificationEvent<>(preAddLoadedChildrenEvent(), this));
            super.getChildren().setAll(result);
            Event.fireEvent(this, new TreeModificationEvent<>(postAddLoadedChildrenEvent(), this));
        }
        future = null;
    }

    public File getDirectory() {
        return directory;
    }

    private static class CallableToSupplierAdapter<T> implements Supplier<T> {

        private final Callable<T> callable;

        private CallableToSupplierAdapter(Callable<T> callable) {
            this.callable = callable;
        }

        @Override
        public T get() {
            try {
                return callable.call();
            } catch (Exception ex) {
                throw new CompletionException(ex);
            }
        }

    }
}
