package top.remake;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import top.remake.controller.EditWindowController;

/**
 * @author ZeroTwo_CHEN
 */
public class EditWindow extends Application {
    private static String path;

    public static void main(String[] args) {
        EditWindow.path = args[0];

        if (Platform.isFxApplicationThread()) {
            Stage stage = new Stage();
            EditWindow editWindow = new EditWindow();
            try {
                editWindow.start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            launch(args);
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/edit-window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1100, 700);
        EditWindowController controller = fxmlLoader.getController();
        stage.setScene(scene);
        stage.show();
        if (path != null) {
            controller.init(stage, path);
        } else {
            controller.init(stage, getParameters().getRaw().get(0));
        }
    }
}
