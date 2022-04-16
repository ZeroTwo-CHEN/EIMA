package top.remake.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import top.remake.entity.BrushType;

/**
 * 生成各种图形，储存鼠标的起始与结束坐标
 * 每幅画布对应一个该类
 *
 * @author ZeroTwo_CHEN
 */
public class DrawShapeUtil {
    /**
     * 笔刷类型
     */
    private BrushType brushType = BrushType.PENCIL;

    /**
     * 当前笔刷的颜色
     * 默认为红色
     */
    //private Color brushColor = Color.RED;

    private ObjectProperty<Color> brushColor=new SimpleObjectProperty<>(Color.RED);

    /**
     * 笔刷粗细
     * 默认为3
     */
    private DoubleProperty strokeWidth = new SimpleDoubleProperty(3.0);

    /**
     * 鼠标开始按下的X坐标
     */
    private double startX;

    /**
     * 鼠标开始按下的Y坐标
     */
    private double startY;

    /**
     * 鼠标的最后X坐标
     */
    private double endX;

    /**
     * 鼠标的最后Y坐标
     */
    private double endY;

    /**
     * 当笔刷为铅笔时使用
     * 保存其最新引用,下同
     * 用于鼠标拖动时实时更新位置
     */
    private Polyline polyline;

    /**
     * 当笔刷为直线时使用
     */
    private Line line;

    /**
     * 当笔刷为矩形时使用
     */
    private Rectangle rectangle;

    /**
     * 当笔刷为椭圆时使用
     */
    private Ellipse ellipse;

    public Shape createShape(double x, double y) {
        startX = x;
        startY = y;

        switch (brushType) {
            case PENCIL -> {
                polyline = new Polyline(x, y);
                polyline.setStroke(brushColor.get());
                polyline.setStrokeWidth(strokeWidth.doubleValue());
                return polyline;
            }
            case LINE -> {
                //刚开始开始与结束坐标相同
                line = new Line(x, y, x, y);
                line.setStroke(brushColor.get());
                line.setStrokeWidth(strokeWidth.doubleValue());
                return line;
            }
            case RECTANGLE -> {
                rectangle = new Rectangle(x, y, 0, 0);
                rectangle.setFill(null);
                rectangle.setStroke(brushColor.get());
                rectangle.setStrokeWidth(strokeWidth.doubleValue());
                return rectangle;
            }
            case ELLIPSE -> {
                ellipse = new Ellipse(x, y, 0, 0);
                ellipse.setFill(null);
                ellipse.setStroke(brushColor.get());
                ellipse.setStrokeWidth(strokeWidth.doubleValue());
                return ellipse;
            }
            default -> throw new IllegalStateException("Unexpected value: " + brushType);
        }
    }

    public void updateShape(double x, double y) {
        endX = x;
        endY = y;

        switch (brushType) {
            case PENCIL -> polyline.getPoints().addAll(x, y);
            case LINE -> {
                line.setEndX(x);
                line.setEndY(y);
            }
            case RECTANGLE -> {
                rectangle.setX(Math.min(startX, x));
                rectangle.setY(Math.min(startY, y));
                rectangle.setWidth(Math.abs(startX - x));
                rectangle.setHeight(Math.abs(startY - y));
            }
            case ELLIPSE -> {
                double radiusX = Math.abs(startX - x) / 2;
                double radiusY = Math.abs(startY - y) / 2;
                ellipse.setCenterX(Math.min(startX, x) + radiusX);
                ellipse.setCenterY(Math.min(startY, y) + radiusY);
                ellipse.setRadiusX(radiusX);
                ellipse.setRadiusY(radiusY);
            }
            default -> throw new IllegalStateException("Unexpected value: " + brushType);
        }
    }

    public BrushType getBrushType() {
        return brushType;
    }

    public void setBrushType(BrushType brushType) {
        this.brushType = brushType;
    }

    public ObjectProperty<Color> getBrushColorProperty() {
        return brushColor;
    }

    public void setBrushColor(Color brushColor) {
        this.brushColor.set(brushColor);
    }

    public DoubleProperty getStrokeWidthProperty() {
        return strokeWidth;
    }

    public void setStrokeWidth(double strokeWidth) {
        this.strokeWidth.set(strokeWidth);
    }
}

