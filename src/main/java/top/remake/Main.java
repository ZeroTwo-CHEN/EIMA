package top.remake;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import top.remake.controller.ControllerMap;

/**
 * @author ZeroTwo_CHEN
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-windows.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        //addController要在FXMLoader.load调用后再使用
        ControllerMap.addController(fxmlLoader.getController());
        stage.setMinHeight(400);
        stage.setMinWidth(600);
        stage.setTitle("EIMA");
        stage.setScene(scene);
        stage.show();
    }
}
