package top.remake.controller;

import com.leewyatt.rxcontrols.controls.RXTranslationButton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.controlsfx.control.BreadCrumbBar;
import top.remake.component.*;
import top.remake.entity.SortOrder;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author ZeroTwo_CHEN
 */
public class MainWindowsController implements Initializable {
    @FXML
    private AnchorPane root;

    @FXML
    private AnchorPane top;

    @FXML
    private SplitPane middle;

    @FXML
    private AnchorPane bottom;

    @FXML
    private TreeView<String> fileTreeView;

    @FXML
    private BreadCrumbBar<String> breadCrumbBar;

    @FXML
    private ScrollPane imagePreviewPane;


    private PreviewFlowPane previewFlowPane;

    @FXML
    private Label tipsLabel;

    @FXML
    private ComboBox<String> sortOrderComboBox;

    @FXML
    private RXTranslationButton refreshButton;

    @FXML
    private RXTranslationButton selectAllButton;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initFileTreeView();
        initBreadCrumbBar();
        initPreviewFlowPane();
        initAdaptiveLayout();
        initToolsPane();
    }


    /**
     * 初始化树形目录
     */
    private void initFileTreeView() {
        fileTreeView.setCellFactory(LazyFileTreeCell.forTreeView());

        TreeItem<String> treeItem = new TreeItem<>();

        //读取文件系统的根并将其添加到根节点中
        File root = FileSystemView.getFileSystemView().getRoots()[0];
        File[] allFiles = root.listFiles();
        File[] directorFiles = root.listFiles(File::isDirectory);
        List<File> list = new ArrayList<>(Arrays.asList(allFiles));
        list.removeAll(Arrays.asList(directorFiles));
        for (File file : list) {
            //过滤文件及快捷方式
            if (file.isDirectory() && !file.getName().endsWith("lnk")) {
                treeItem.getChildren().add(new FileTreeItem(file, new DirectoryLoader(file)));
            }
        }

        fileTreeView.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) ->
                        breadCrumbBar.selectedCrumbProperty().set(newValue));
        fileTreeView.setRoot(treeItem);
        fileTreeView.setShowRoot(false);
    }

    private Pane pane;
    private Rectangle rectangle;
    /**
     * 初始化缩略图面板
     *
     */
    private void initPreviewFlowPane() {
        previewFlowPane=new PreviewFlowPane();
        rectangle=new Rectangle();
        pane=new Pane();
        rectangle.setFill(Color.rgb(50,150,250,.8));
        rectangle.setVisible(false);
        pane.getChildren().addAll(previewFlowPane,rectangle);
        addHandler();
        previewFlowPane.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                pane.setMinHeight(previewFlowPane.getHeight());
            }
        });
        imagePreviewPane.viewportBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                previewFlowPane.setPrefWidth(newValue.getWidth());
                if(previewFlowPane.getHeight()<imagePreviewPane.getHeight())
                    previewFlowPane.setPrefHeight(imagePreviewPane.getHeight());
            }
        });

        imagePreviewPane.setContent(pane);
        rectangle.setVisible(false);
        //rectangle.toFront();
        imagePreviewPane.setCache(true);

    }

    /**
     * 初始树形目录导航栏
     */
    private void initBreadCrumbBar() {
        breadCrumbBar.selectedCrumbProperty()
                .addListener((observable, oldValue, newValue) -> {
                    fileTreeView.getSelectionModel().select(newValue);
                    previewFlowPane.update(((FileTreeItem) newValue).getDirectory());
                });
    }

    /**
     * 设置自适应布局
     */
    private void initAdaptiveLayout() {
        middle.setPrefHeight(root.getHeight() - top.getPrefHeight() - bottom.getPrefHeight());
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double temp = top.getPrefHeight() + bottom.getPrefHeight();
            middle.setPrefHeight(newValue.doubleValue() - temp);
        });
    }

    /**
     * 初始化工具栏
     */
    private void initToolsPane() {
        //排序选择框
        sortOrderComboBox.getItems()
                .addAll(SortOrder.ASC_SORT_BY_NAME, SortOrder.DESC_SORT_BY_NAME,
                        SortOrder.ASC_SORT_BY_TIME, SortOrder.DESC_SORT_BY_TIME,
                        SortOrder.ASC_SORT_BY_SIZE, SortOrder.DESC_SORT_BY_SIZE);
        sortOrderComboBox.getSelectionModel().select(0);
        sortOrderComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> previewFlowPane.update());

        //初始化刷新按钮
        refreshButton.setOnAction(event -> previewFlowPane.update());
        //初始化全选按钮
        selectAllButton.setOnAction(event -> previewFlowPane.selectAll());
    }

    /**
     * 左下角提示栏
     */
    public void setTipsLabelText(int number, double size) {
        tipsLabel.setText("共 " + number + " 张照片(" + String.format("%.2f", size) + " MB)");
    }

    /**
     * 返回comboBox中的值
     */
    public String getSortOrder() {
        return sortOrderComboBox.getSelectionModel().getSelectedItem();
    }


    //
    private double x,y;
    private double width;
    private double height;
    private void addHandler(){



        /**
         * 对FlowPane添加监听器记录鼠标按下时的坐标，存放于x，y中。
         */
        previewFlowPane.addEventHandler(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override

            public void handle(MouseEvent event) {
                x=event.getX();
                y=event.getY();
            }
        });
        /**
         * 通过对FlowPane添加监听器记录并计算矩形（多选框）的坐标，宽高；
         * 根据得到的坐标和宽高绘制矩形并动态选择图片
         */
        previewFlowPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                double x2=event.getX();
                double y2=event.getY();
                double endX;double endY;
                endX=Math.min(x,x2);
                endY=Math.min(y,y2);
                width=Math.max(x,x2)-Math.min(x,x2);
                height=Math.max(y,y2)-Math.min(y,y2);
                rectangle.setX(endX);
                rectangle.setY(endY);
                rectangle.setWidth(width);
                rectangle.setHeight(height);
                rectangle.setVisible(true);
                //选择矩形所经过的图片
                if(width>=10&&height>=10)
                   selectImg();
                //拖拽鼠标向上超出边界时，将ScrollPane上滑
                if(event.getSceneY()<100)
                   imagePreviewPane.setVvalue(imagePreviewPane.getVvalue()+(event.getSceneY()-100)/previewFlowPane.getHeight()/100);
                //拖拽鼠标向下超出边界时。将ScrollPane下滑
                if(event.getSceneY()>100+imagePreviewPane.getHeight())
                    imagePreviewPane.setVvalue(imagePreviewPane.getVvalue()+(event.getSceneY()-imagePreviewPane.getHeight()-100)/previewFlowPane.getHeight()/10);
            }
        });
        /***
         * 处理单击图片或者单击空白面板
         */
       previewFlowPane.addEventHandler(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //选中图片
                rectangle.setVisible(false);
            }
        });

        /**
         * 处理单击空白部分时取消已选择的图片
         */
        previewFlowPane.setOnMouseClicked(event -> {
           if(event.getButton()== MouseButton.PRIMARY&&event.getClickCount()==1&&!event.isControlDown()) {
               if (previewFlowPane.equals(event.getPickResult().getIntersectedNode())) {
                   previewFlowPane.clearSelect();
               }

           }
       });
    }

    /**
     * 根据矩形的数据选择图片
     */
    private void selectImg(){
        previewFlowPane.clearSelect();
        for(ThumbnailPanel pane:previewFlowPane.getThumbnailPanels()){
            boolean inIntersects = rectangle.intersects(pane.getLayoutX(), pane.getLayoutY(), pane.getWidth(), pane.getHeight());
            if(inIntersects)
                previewFlowPane.addSelect(pane);
        }
    }
}
