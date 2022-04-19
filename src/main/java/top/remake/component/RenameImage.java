package top.remake.component;


import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import top.remake.entity.RenameData;

/**
 * 重命名窗口
 */

public class RenameImage extends Dialog<RenameData> {
    private static final Label label1=new Label("名称前缀:");
    private static final Label label2=new Label("起始编号:");
    private static final Label label3=new Label("编号位数:");
    private final TextField textField=new TextField();
    private final Spinner<Integer> startNum=new Spinner<>(1,10000,1);
    private final Spinner<Integer> digit=new Spinner<>(1,8,3);

    public  RenameImage() {
        startNum.setEditable(true);
        digit.setEditable(true);
        GridPane gridPane=new GridPane();
        gridPane.setPadding(new Insets(10,8,10,8));
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        textField.setPromptText("输入名称前缀");
        gridPane.add(label1,0,0);
        gridPane.add(label2,0,1);
        gridPane.add(label3,0,2);
        gridPane.add(textField,1,0);
        gridPane.add(startNum,1,1);
        gridPane.add(digit,1,2);
        this.setTitle("重命名");
        this.getDialogPane().setContent(gridPane);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                    return new RenameData(textField.getText(),startNum.getValue(),digit.getValue());
            }
            return null;
        });
    }

}
