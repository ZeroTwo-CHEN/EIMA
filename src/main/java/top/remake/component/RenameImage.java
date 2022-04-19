package top.remake.component;


import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import top.remake.entity.RenameData;

/**
 * 重命名窗口
 *
 * @author gzz
 */

public class RenameImage extends Dialog<RenameData> {
    private static final Label LABEL_1 = new Label("名称前缀:");
    private static final Label LABEL_2 = new Label("起始编号:");
    private static final Label LABEL_3 = new Label("编号位数:");
    private final TextField textField = new TextField();
    private final Spinner<Integer> startNum = new Spinner<>(1, 10000, 1);
    private final Spinner<Integer> digit = new Spinner<>(1, 8, 3);

    public RenameImage() {
        startNum.setEditable(true);
        startNum.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        digit.setEditable(true);
        digit.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_RIGHT_HORIZONTAL);
        GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10, 10, 10, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        textField.setPromptText("输入名称前缀");
        gridPane.add(LABEL_1, 0, 0);
        gridPane.add(LABEL_2, 0, 1);
        gridPane.add(LABEL_3, 0, 2);
        gridPane.add(textField, 1, 0);
        gridPane.add(startNum, 1, 1);
        gridPane.add(digit, 1, 2);
        this.setTitle("重命名");
        this.getDialogPane().setContent(gridPane);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return new RenameData(textField.getText(), startNum.getValue(), digit.getValue());
            }
            return null;
        });
    }

}
