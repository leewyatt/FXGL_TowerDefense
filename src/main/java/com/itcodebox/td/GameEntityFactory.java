package com.itcodebox.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author LeeWyatt
 */
public class GameEntityFactory implements EntityFactory {

    @Spawns("tower")
    public Entity newTower(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.TOWER)
                .bbox(BoundingShape.box(30, 30))
                .with(new TowerComponent())
                .collidable()
                .build();
    }

    @Spawns("point")
    public Entity newPoint(SpawnData data) {
        int index = data.get("index");
        String dir = data.get("dir");
        TowerDefenseApp app = (TowerDefenseApp) (FXGL.getApp());
        app.getPointInfos().put(index, new Pair<>(new Point2D(data.getX(), data.getY()), dir));
        return FXGL.entityBuilder(data)
                .type(GameType.POINT)
                .build();
    }

    @Spawns("space")
    public Entity newSpace(SpawnData data) {
        TowerDefenseApp app = (TowerDefenseApp) (FXGL.getApp());
        app.getSpaceInfos().add(new Rectangle(data.getX(), data.getY(), data.<Integer>get("width"), data.<Integer>get("height")));
        return FXGL.entityBuilder(data)
                .type(GameType.SPACE)
                .build();
    }

    @Spawns("empty")
    public Entity newEmpty(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.EMPTY)
                .bbox(BoundingShape.box(30, 30))
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        int maxHp = 10;
        HealthIntComponent hp = new HealthIntComponent(maxHp);
        ProgressBar hpBar = new ProgressBar(true);
        hpBar.setFill(Color.LIGHTGREEN);
        hpBar.setWidth(48);
        hpBar.setHeight(8);
        hpBar.setTranslateY(-5);
        hpBar.setMaxValue(maxHp);
        hpBar.setCurrentValue(maxHp);
        hpBar.currentValueProperty().bind(hp.valueProperty());
        hp.valueProperty().addListener((ob, ov, nv) -> {
            int value = nv.intValue();
            if (value > maxHp * 0.65) {
                hpBar.setFill(Color.LIGHTGREEN);
            } else if (value > maxHp * 0.25) {
                hpBar.setFill(Color.GOLD);
            } else {
                hpBar.setFill(Color.RED);
            }

        });
        return FXGL.entityBuilder(data)
                .type(GameType.ENEMY)
                .with(hp)
                .view(hpBar)
                .with(new EnemyComponent(hpBar))
                .with(new CollidableComponent(true))
                .bbox(BoundingShape.box(48, 48))
                .build();
    }


    @Spawns("bullet")
    public Entity spawnBullet(SpawnData data) {
        return entityBuilder(data)
                .type(GameType.BULLET)
                .viewWithBBox(FXGL.texture("tower/laser.png", 30, 10))
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanComponent())
                .with(new BulletComponent())
                .build();
    }

    @Spawns("buildIndicator")
    public Entity newBuildIndicator(SpawnData data) {
        return entityBuilder(data)
                .with(new BuildIndicatorComponent())
                .zIndex(100)
                .build();
    }

    @Spawns("bottom")
    public Entity newBottom(SpawnData data) {
        return entityBuilder(data)
                .at(0, FXGL.getAppHeight()-100)
                .view(new Rectangle(FXGL.getAppWidth(), 100, Color.web("#D3D3D399")))
                .build();
    }

    @Spawns("placedButton")
    public Entity newPlacedButton(SpawnData data) {
        Texture texture = FXGL.texture("tower/tower_icon.png", 58, 102);
        texture.setTranslateX((80-58)/2.0);
        texture.setTranslateY(-(80-62)/2.0);

        return entityBuilder(data)
                .at(50, FXGL.getAppHeight()-100)
                .view(texture)
                .with(new PlacedButtonComponent())
                .build();
    }
}
