package com.leewyatt.td.ui;

import com.almasb.fxgl.entity.Entity;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 *
 * 范围指示器: 当按下炮塔的时候, 显示炮塔个攻击范围
 * 一个大圈表示范围,里面有一条直线,围绕中心旋转
 */
public class RangeIndicator extends Group {

    private final Circle circle;
    private final Line line;
    private final Timeline tl;
    private final Rotate rotate;
    private final int strokeWidth = 5;

    public RangeIndicator() {
        //创建UI
        RadialGradient okFill = new RadialGradient(
                0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.9, new Color(1.0, 0.0, 0.0, 0.0)),
                new Stop(1.0, new Color(0.0, 1.0, 0.39, 0.38)));
        //圆圈表示范围
        circle = new Circle(0, okFill);
        //直线用于旋转动画
        line = new Line();
        line.setStroke(new Color(0.0, 0.7, 0.2, 0.6));
        line.setStrokeWidth(strokeWidth);
        getChildren().setAll(circle, line);
        //创建动画
        rotate = new Rotate();
        line.getTransforms().add(rotate);
        tl = new Timeline(
                new KeyFrame(Duration.seconds(0), new KeyValue(rotate.angleProperty(), 0)),
                new KeyFrame(Duration.seconds(3.5), new KeyValue(rotate.angleProperty(), 360))
        );
        tl.setCycleCount(Animation.INDEFINITE);
    }

    public void show(Entity entity, int radius) {
        tl.stop();
        double x = entity.getX() + entity.getWidth() / 2;
        double y = entity.getY() + entity.getHeight() / 2;
        circle.setCenterX(x);
        circle.setCenterY(y);
        circle.setRadius(radius);
        line.setStartX(x - radius);
        line.setStartY(y);
        line.setEndX(x);
        line.setEndY(y);
        rotate.setPivotX(x);
        rotate.setPivotY(y);
        tl.play();
        setVisible(true);
    }

    public void hide() {
        tl.stop();
        setVisible(false);
    }

}
