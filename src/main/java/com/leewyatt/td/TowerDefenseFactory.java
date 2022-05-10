package com.leewyatt.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.ExpireCleanComponent;
import com.almasb.fxgl.dsl.components.OffscreenCleanComponent;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.IrremovableComponent;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.leewyatt.td.components.*;
import com.leewyatt.td.data.BulletData;
import com.leewyatt.td.data.EnemyData;
import com.leewyatt.td.data.TowerData;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import static com.almasb.fxgl.dsl.FXGL.entityBuilder;

/**
 * @author LeeWyatt
 */
public class TowerDefenseFactory implements EntityFactory {

    /**
     * 炮塔
     * @param data 数据 从json里获取到的
     *             json文件在 data/___Tower.json
     * @return
     */
    @Spawns("tower")
    public Entity newTower(SpawnData data) {
        TowerData towerData = data.get("towerData");
        return FXGL.entityBuilder(data)
                .type(EntityType.TOWER)
                .bbox(BoundingShape.box(towerData.width(), towerData.height()))
                .collidable()
                .with(new TowerComponent())
                .build();
    }

    /**
     * 子弹
     * @param data 子弹的数据,也在炮塔的json文件里,因为每个炮塔对应的子弹和特殊效果不同
     * @return
     */
    @Spawns("bullet")
    public Entity newBullet(SpawnData data) {
        BulletData bulletData = data.get("bulletData");
        return FXGL.entityBuilder(data)
                .type(EntityType.BULLET)
                .collidable()
                .viewWithBBox(bulletData.imageName())
                .with(new ProjectileComponent(
                        data.get("dir"),
                        bulletData.speed()))
                .with(new OffscreenCleanComponent())
                .with(new BulletComponent())
                .build();
    }

    /**
     * 敌人(敌人) 数据 存储在 data/enemy_.json 文件里
     * 敌人不同运动方向的图片
     * @param data
     * @return
     */
    @Spawns("enemy")
    public Entity newEnemy(SpawnData data) {
        EnemyData enemyData = data.get("enemyData");
        return FXGL.entityBuilder(data)
                .type(EntityType.ENEMY)
                //减速特效,需要时间组件
                .with(new TimeComponent())
                .with(new EffectComponent())
                .collidable()
                .bbox(BoundingShape.box(enemyData.getWidth(),enemyData.getHeight()))
                .with(new EnemyComponent())
                .build();
    }

    /**
     * 敌人移动路径上的关键点
     * @param data
     * @return
     */
    @Spawns("point")
    public Entity newPoint(SpawnData data) {
        //点的索引,第几个点
        int index = data.get("index");
        //点的运动方向
        String dir = data.get("dir");
        TowerDefenseApp app = (TowerDefenseApp) (FXGL.getApp());
        app.getPointInfos().put(index, new Pair<>(new Point2D(data.getX(), data.getY()), dir));
        return FXGL.entityBuilder(data)
                .type(EntityType.POINT)
                .build();
    }

    /**
     * 炮塔列表框组件,主要就是显示一张背景图片
     * @param data
     * @return
     */
    @Spawns("towersBox")
    public Entity newTowersBox(SpawnData data) {
        return FXGL.entityBuilder(data)
                .at(1000, 0)
                .with(new IrremovableComponent())
                .view("ui/right/chooseBg.png")
                .neverUpdated()
                .build();
    }

    /**
     * 炮塔列表框里的炮塔按钮
     * @param data 读取炮塔json文件的数据;
     * @return
     */
    @Spawns("towerButton")
    public Entity newPlacedButton(SpawnData data) {
        TowerData towerData = data.get("towerData");
        Texture texture = FXGL.texture(towerData.icon());
        texture.setTranslateX((80 - towerData.width()) / 2.0);
        texture.setTranslateY((80 - towerData.height()) / 2.0);
        Texture bgTexture = FXGL.texture("ui/right/btnBg.png", 105, 105);
        bgTexture.setTranslateX((80 - bgTexture.getWidth()) / 2);
        bgTexture.setTranslateY((80 - bgTexture.getHeight()) / 2);
        return entityBuilder(data)
                .with(new IrremovableComponent())
                .view(new Rectangle(80, 80, Color.web("#D5D5D511")))
                .view(bgTexture)
                .view(texture)
                .with(new TowerButtonComponent())
                .build();
    }

    /**
     * 建造指示器; 一个炮塔图标在中间,一个圆圈表示范围,红色表示不能建造,绿色表述可以建造
     * @param data
     * @return
     */
    @Spawns("buildIndicator")
    public Entity newBuildIndicator(SpawnData data) {
        return entityBuilder(data)
                .with(new BuildIndicatorComponent())
                .zIndex(Integer.MAX_VALUE)
                .build();
    }

    /**
     * 空白的实体, 仅仅用于检测碰撞; 比如炮塔移动,就把空白实体移动到对应的位置,进行碰撞检测
     * 如果在可以建造的区域,并且不和周围的炮塔碰撞,那么此刻就能建造炮塔
     */
    @Spawns("empty")
    public Entity newEmpty(SpawnData data) {
        return FXGL.entityBuilder(data)
                .type(EntityType.EMPTY)
                .collidable()
                .neverUpdated()
                .build();
    }

    /**
     * 能够建造炮塔的区域,从地图文件里获取
     */
    @Spawns("space")
    public Entity newSpace(SpawnData data) {
        TowerDefenseApp app = (TowerDefenseApp) (FXGL.getApp());
        app.getSpaceInfos().add(new Rectangle(data.getX(), data.getY(), data.<Integer>get("width"), data.<Integer>get("height")));
        return FXGL.entityBuilder(data)
                .type(EntityType.SPACE)
                .build();
    }

    /**
     * 开始点的位置.在游戏开始前,动画显示开始点,提示玩家,敌人从这里开始移动
     */
    @Spawns("startPoint")
    public Entity newStartPoint(SpawnData data) {
        AnimationChannel ac = new AnimationChannel(FXGL.image("ui/startPoint.png",440,70), 4, 110, 35, Duration.seconds(0.5), 0, 7);
        AnimatedTexture at = new AnimatedTexture(ac);
        at.loop();
        return entityBuilder(data)
                .view(at)
                .with(new ExpireCleanComponent(Duration.seconds(5)))
                .build();
    }

    /**
     * 怪兽移动的终点:
     *      会在游戏时提动画提示玩家
     */
    @Spawns("finishPoint")
    public Entity newFinishPoint(SpawnData data) {
        AnimationChannel ac = new AnimationChannel(FXGL.image("ui/finishPoint.png"), 12, 46, 66, Duration.seconds(1), 0, 11);
        AnimatedTexture at = new AnimatedTexture(ac);
        at.loop();
        return entityBuilder(data)
                .type(EntityType.FINISH_POINT)
                .viewWithBBox(at)
                .collidable()
                .build();
    }

    /**
     * 暂停按钮. 点击后暂停或者继续
     */
    @Spawns("pauseButton")
    public Entity newPauseButton(SpawnData data) {
        return entityBuilder(data)
                .with(new IrremovableComponent())
                .with(new PauseButtonComponent())
                .build();
    }





}
