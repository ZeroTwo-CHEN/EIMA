package top.remake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import top.remake.controller.DisplayWindowsController;

/**
 * @author ZeroTwo_CHEN
 */
public class DisplayWindow extends Application {
    private static String path = null;

    public static void main(String[] args) {
        DisplayWindow.path = args[0];
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
        DisplayWindowsController controller = fxmlLoader.getController();
        stage.setScene(scene);
        stage.show();
        if (path != null) {
            controller.init(stage, path);
        } else {
            controller.init(stage, getParameters().getRaw().get(0));
        }
    }
}
