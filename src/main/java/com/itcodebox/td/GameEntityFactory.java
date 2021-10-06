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
import com.itcodebox.td.component.*;
import com.itcodebox.td.constant.Config;
import com.itcodebox.td.constant.GameType;
import com.itcodebox.td.data.TowerData;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author LeeWyatt
 */
public class GameEntityFactory implements EntityFactory {

    /**
     * 激光炮塔
     */
    @Spawns("laserTower")
    public Entity newTower(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(GameType.TOWER)
                .bbox(BoundingShape.box(30, 30))
                .with(new LaserTowerComponent())
                .collidable()
                .build();
    }

    /**
     * 雷系炮塔
     */
    @Spawns("thunderTower")
    public Entity newThunderTower(SpawnData data) {
        return buildFiveElementsTower(data, Config.THUNDER_TOWER_DATA);
    }

    /**
     * 火系炮塔
     */
    @Spawns("flameTower")
    public Entity newFlameTower(SpawnData data) {
        return buildFiveElementsTower(data, Config.FLAME_TOWER_DATA);
    }

    private Entity buildFiveElementsTower(SpawnData data, TowerData towerData) {
        return FXGL.entityBuilder(data)
                .type(GameType.TOWER)
                .bbox(BoundingShape.box(towerData.getWidth(), towerData.getHeight()))
                .with(new FiveElementsTowerComponent(towerData))
                .collidable()
                .build();
    }

    /**
     * 箭塔
     */
    @Spawns("arrowTower")
    public Entity newArrowTower(SpawnData data) {
        return entityBuilder(data)
                .type(GameType.TOWER)
                .viewWithBBox(FXGL.texture("tower/arrow/tower.png"))
                .with(new ArrowTowerComponent())
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
                .collidable()
                .build();
    }

    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        int maxHp = 500;
        HealthIntComponent hp = new HealthIntComponent(maxHp);
        ProgressBar hpBar = new ProgressBar(false);
        hpBar.setFill(Color.LIGHTGREEN);
        hpBar.setWidth(48);
        hpBar.setHeight(7);
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

    @Spawns("laserTowerBullet")
    public Entity spawnLaserBullet(SpawnData data) {
        return createBullet(data, "tower/laser/bullet.png",30,10);
    }


    @Spawns("flameTowerBullet")
    public Entity newFlameBullet(SpawnData data) {
        return createBullet(data, "tower/flame/bullet.png",30,10);
    }

    @Spawns("thunderTowerBullet")
    public Entity newThunderBullet(SpawnData data) {
        return createBullet(data, "tower/thunder/bullet.png",30,10);
    }

    @Spawns("arrowTowerBullet")
    public Entity newArrowBullet(SpawnData data) {
        return createBullet(data, "tower/arrow/bullet.png", 50, 10);
    }

    private Entity createBullet(SpawnData data, String s, int w, int h) {
        return entityBuilder(data)
                .type(GameType.BULLET)
                .viewWithBBox(FXGL.texture(s, w, h))
                .with(new CollidableComponent(true))
                .with(new OffscreenCleanComponent())
                .with(new BulletComponent(data.get("radius"), data.get("damage")))
                .build();
    }

    @Spawns("buildIndicator")
    public Entity newBuildIndicator(SpawnData data) {
        return entityBuilder(data)
                .with(new BuildIndicatorComponent())
                .zIndex(100)
                .build();
    }

    @Spawns("placedButton")
    public Entity newPlacedButton(SpawnData data) {
        Texture texture = FXGL.texture(data.get("imgName"), data.get("width"), data.get("height"));
        texture.setTranslateX((80 - texture.getWidth()) / 2.0);
        texture.setTranslateY((80 - texture.getHeight()) / 2.0);

        Texture bgTexture = FXGL.texture("btnBg.png", 105, 105);
        bgTexture.setTranslateX((80 - bgTexture.getWidth()) / 2);
        bgTexture.setTranslateY((80 - bgTexture.getHeight()) / 2);
        return entityBuilder(data)
                .view(new Rectangle(80, 80, Color.web("#D5D5D511")))
                .view(bgTexture)
                .view(texture)
                .with(new PlacedButtonComponent(data.get("towerType")))
                .build();
    }

    @Spawns("placeBox")
    public Entity newPlaceBox(SpawnData data) {
        return entityBuilder(data)
                .at(1000, 0)
                .view("chooseBg.png")
                .build();
    }
}
