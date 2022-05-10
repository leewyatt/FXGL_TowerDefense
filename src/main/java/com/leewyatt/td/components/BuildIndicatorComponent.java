package com.leewyatt.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.leewyatt.td.data.TowerData;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

/**
 * @author LeeWyatt
 *
 * 建造指示器:
 *      一个圆圈,中心有一个炮塔图标;
 *      如果炮塔图标移动到了可以建造的范围,那么绿色表示,如果是不能建造的范围, 红色表示
 *
 *  会频繁使用到, 所以只创建了一次(可以改为单例);
 *  当选择的炮塔变了, 调用updateIndicator修改建造指示器的数据
 *
 *
 */
public class BuildIndicatorComponent extends Component {

    private final RadialGradient okFill;
    private final RadialGradient disabledFill;
    private Texture texture;
    private Circle circle;

    public BuildIndicatorComponent() {
        //范围
        okFill = new RadialGradient(
                0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.9, new Color(1.0, 0.0, 0.0, 0.0)),
                new Stop(1.0, new Color(0.0, 1.0, 0.39, 0.4)));
        disabledFill = new RadialGradient(
                0.0, 0.0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0.9, new Color(1.0, 0.0, 0.0, 0.0)),
                new Stop(1.0, new Color(1.0, 0.0, 0.0, 0.38)));
    }

    @Override
    public void onAdded() {
        //图片
        texture = FXGL.texture("tower/arrow/icon.png");
        //entity.getBoundingBoxComponent().addHitBox(
        //        new HitBox(BoundingShape.box(texture.getWidth(), texture.getHeight())));
        circle = new Circle(100, disabledFill);
        circle.setTranslateX(texture.getWidth() / 2.0);
        circle.setTranslateY(texture.getHeight() / 2.0);
        entity.getViewComponent().addChild(texture);
        entity.getViewComponent().addChild(circle);
    }

    public void canBuild(boolean canBuild) {
        circle.setFill(canBuild ? okFill : disabledFill);
    }

    private TowerData lastTowerData;
    public void updateIndicator(TowerData towerData) {
        //为了性能,先判断下
        if (towerData.equals(lastTowerData)) {
            return;
        }
        lastTowerData = towerData;
        texture.setImage(FXGL.image(towerData.icon()));
        //entity.getBoundingBoxComponent().clearHitBoxes();
        //entity.getBoundingBoxComponent().addHitBox(
        //        new HitBox(BoundingShape.box(texture.getWidth(), texture.getHeight())));
        circle.setTranslateX(texture.getWidth() / 2.0);
        circle.setTranslateY(texture.getHeight() / 2.0);
        circle.setRadius(towerData.bulletData().range());
    }

}