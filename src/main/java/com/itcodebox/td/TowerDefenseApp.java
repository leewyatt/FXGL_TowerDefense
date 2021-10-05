package com.itcodebox.td;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LeeWyatt
 */
public class TowerDefenseApp extends GameApplication {

    //TODO 1. 需要完善的地方: 把炮塔抽象出来, 方便扩展炮塔 ; Add more turret types
    //        比如箭塔(可以群体攻击) 石头塔(可以范围攻击)
    //     2. 完善更多的地图和关卡

    private LinkedHashMap<Integer, Pair<Point2D, String>> pointInfos = new LinkedHashMap<>();
    private Entity buildIndicator;
    private BuildIndicatorComponent buildIndicatorComponent;
    private Entity emptyEntity;

    public LinkedHashMap<Integer, Pair<Point2D, String>> getPointInfos() {
        return pointInfos;
    }

    ArrayList<Rectangle> spaceInfos = new ArrayList<>();

    public ArrayList<Rectangle> getSpaceInfos() {
        return spaceInfos;
    }



    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Tower Defense");
        settings.setVersion("0.1");

        settings.setWidth(20 * 50);
        settings.setHeight(15 * 50);
        settings.setAppIcon("logo.jpg");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("placed",false);
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (!FXGL.getb("placed")) {
                return;
            }
            moveMouse();
        });

        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (!FXGL.getb("placed")){
                return;
            }
            buildTower();
        });

    }

    private void moveMouse() {
        int w = 30;
        int h = 30;

        Point2D p = FXGL.getInput().getMousePositionWorld();
        //鼠标点击的位置是炮塔的中心
        double x = p.getX() - w / 2.0;
        double y = p.getY() - h / 2.0;
        buildIndicator.setX(x);
        buildIndicator.setY(y);
        boolean flag = false;
        for (Rectangle r : spaceInfos) {
            //判断是否可以建造
            if (r.getX() <= x && r.getWidth() + r.getX() >= x + w && r.getY() <= y && r.getHeight() + r.getY() >= y + h) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            buildIndicatorComponent.canBuild(false);
            return;
        }

        emptyEntity.setX(x);
        emptyEntity.setY(y);
        List<Entity> towers = FXGL.getGameWorld().getEntitiesByType(GameType.TOWER);
        BoundingBoxComponent emptyBox = emptyEntity.getBoundingBoxComponent();
        boolean canGenerate = true;
        for (Entity tower : towers) {
            if (emptyBox.isCollidingWith(tower.getBoundingBoxComponent())) {
                canGenerate = false;
                break;
            }
        }

        buildIndicatorComponent.canBuild(canGenerate);

    }

    private void buildTower() {
        int w = 30;
        int h = 30;

        Point2D p = FXGL.getInput().getMousePositionWorld();
        //鼠标点击的位置是炮塔的中心
        double x = p.getX() - w / 2.0;
        double y = p.getY() - h / 2.0;

        boolean flag = false;
        for (Rectangle r : spaceInfos) {
            //判断是否可以建造
            if (r.getX() <= x && r.getWidth() + r.getX() >= x + w && r.getY() <= y && r.getHeight() + r.getY() >= y + h) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            return;
        }
        emptyEntity.setX(x);
        emptyEntity.setY(y);
        buildIndicator.setX(x);
        buildIndicator.setY(y);
        List<Entity> towers = FXGL.getGameWorld().getEntitiesByType(GameType.TOWER);
        BoundingBoxComponent emptyBox = emptyEntity.getBoundingBoxComponent();
        boolean canGenerate = true;
        for (Entity tower : towers) {
            if (emptyBox.isCollidingWith(tower.getBoundingBoxComponent())) {
                canGenerate = false;
                break;
            }
        }

        buildIndicatorComponent.canBuild(canGenerate);
        if (canGenerate) {
            FXGL.play("placed.wav");
            FXGL.spawn("tower", x, y);
            FXGL.set("placed", false);
            buildIndicator.setX(-1000);
            buildIndicator.setY(-1000);
        }

    }

    @Override
    protected void initGame() {
        //缓存一下比较大的图片;避免调用的时候卡顿
        FXGL.image("enemy/enemy_move_right.png");
        FXGL.image("enemy/enemy_move_left.png");
        FXGL.image("enemy/enemy_move_up.png");
        FXGL.image("enemy/enemy_move_down.png");
        FXGL.image("enemy/enemy_die.png");
        FXGL.image("tower/build.png");
        FXGL.image("tower/attack.png");

        FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());

        FXGL.setLevelFromMap("level1.tmx");
        //清理无用的实体;
        List<Entity> tempEntities = FXGL.getGameWorld().getEntitiesByType(GameType.SPACE,GameType.POINT);
        FXGL.getGameWorld().removeEntities(tempEntities);
        //建造指示器的创建
        buildIndicator = FXGL.spawn("buildIndicator");
        buildIndicator.setX(-1000);
        buildIndicator.setY(-1000);
        buildIndicatorComponent = buildIndicator.getComponent(BuildIndicatorComponent.class);
        //检测建造碰撞用的实体
        emptyEntity = FXGL.spawn("empty");
        emptyEntity.setX(-100);
        emptyEntity.setY(-100);
        //刷怪
        FXGL.runOnce(()->{
            FXGL.run(() -> {
                FXGL.spawn("enemy", pointInfos.get(0).getKey());
            }, Duration.seconds(1), 20);
        }, Duration.seconds(5));

        FXGL.run(() -> {
            FXGL.run(() -> {
                FXGL.spawn("enemy", pointInfos.get(0).getKey());
            }, Duration.seconds(1), 20);
        }, Duration.seconds(40), 2);

        FXGL.spawn("bottom");
        PlacedButtonComponent placedButton = FXGL.spawn("placedButton").getComponent(PlacedButtonComponent.class);

    }

    @Override
    protected void onPreInit() {
        FXGL.getSettings().setGlobalSoundVolume(0.05);
        FXGL.getSettings().setGlobalMusicVolume(0.1);
        FXGL.loopBGM("bgm.mp3");
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(GameType.BULLET, GameType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                EnemyComponent enemyComponent = enemy.getComponent(EnemyComponent.class);
                if (enemyComponent.isDead()) {
                    return;
                }
                bullet.removeFromWorld();

                enemyComponent.attacked();
            }

        });
    }


    @Override
    protected void initUI() {

    }

    public static void main(String[] args) {
        launch(args);
    }
}
