package com.itcodebox.td.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.Texture;
import com.itcodebox.td.constant.Config;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;

/**
 * @author LeeWyatt
 */
public class BuildIndicatorComponent extends Component {

    private final RadialGradient okFill;
    private final RadialGradient disabledFill;
    private Image towerIcon;
    private Texture texture;
    private Circle circle;

    public BuildIndicatorComponent() {
        //图片
        towerIcon = FXGL.image("tower/laser/tower_icon.png", 29, 51);
        texture = new Texture(towerIcon);

        //范围
        okFill = new RadialGradient(
                0.0,0.0,0.5,0.5,0.5,true,CycleMethod.NO_CYCLE,
                new Stop(0.9,new Color( 1.0, 0.0, 0.0, 0.0)),
                new Stop(1.0,new Color( 0.0, 1.0, 0.39, 0.4)));;
        disabledFill = new RadialGradient(
                0.0,0.0,0.5,0.5,0.5,true,CycleMethod.NO_CYCLE,
                new Stop(0.9,new Color( 1.0, 0.0, 0.0, 0.0)),
                new Stop(1.0,new Color( 1.0, 0.0, 0.0, 0.38)));
        circle = new Circle(Config.LASER_TOWER_DATA.getAttackRadius(),disabledFill);
        circle.setTranslateX(texture.getWidth()/2.0);
        circle.setTranslateY(texture.getHeight()/2.0);

    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        entity.getViewComponent().addChild(circle);
    }

    public void canBuild(boolean canBuild) {
        circle.setFill(canBuild?okFill:disabledFill);
    }

    public void resetIndicator(Image towerIcon,double radius) {
        texture.setImage(towerIcon);
        circle.setTranslateX(texture.getWidth()/2.0);
        circle.setTranslateY(texture.getHeight()/2.0);
        circle.setRadius(radius);
    }

}
