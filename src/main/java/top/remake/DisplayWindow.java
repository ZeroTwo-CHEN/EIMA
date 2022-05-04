package top.remake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import top.remake.controller.DisplayWindowController;

import java.util.Objects;

/**
 * @author ZeroTwo_CHEN
 */
public class DisplayWindow extends Application {
    /**
     * 数组的长度用于标记启动方式
     * 长度为1则为从主窗口双击启动
     * 长度为2则为从主窗口点击播放键启动
     */
    private static String[] path = null;

    public static void main(String[] args) {
        DisplayWindow.path = args;
        if (Platform.isFxApplicationThread()) {
            Stage stage = new Stage();
            DisplayWindow displayWindow = new DisplayWindow();
            try {
                displayWindow.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/display-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        DisplayWindowController controller = fxmlLoader.getController();
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/image/icon.png")).toExternalForm()));
        stage.setScene(scene);
        if (path != null) {
            //从主界面进入展示界面
            if (path.length == 1) {
                stage.show();
                controller.init(stage, path[0]);
            } else {
                //在主界面通过点击播放界面进入 不展示stage
                controller.playByMainWindow(stage, path[0]);
            }
        } else {
            controller.init(stage, getParameters().getRaw().get(0));
        }
    }
}
