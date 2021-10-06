package com.itcodebox.td;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.components.BoundingBoxComponent;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.itcodebox.td.component.BuildIndicatorComponent;
import com.itcodebox.td.component.BulletComponent;
import com.itcodebox.td.component.EnemyComponent;
import com.itcodebox.td.component.PlacedButtonComponent;
import com.itcodebox.td.constant.Config;
import com.itcodebox.td.constant.GameType;
import com.itcodebox.td.data.TowerData;
import com.itcodebox.td.constant.TowerType;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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

    //TODO  完善更多的地图和关卡

    private LinkedHashMap<Integer, Pair<Point2D, String>> pointInfos = new LinkedHashMap<>();
    private Entity buildIndicator;
    private BuildIndicatorComponent buildIndicatorComponent;
    private Entity emptyEntity;
    private PlacedButtonComponent laserBtn;
    private PlacedButtonComponent thunderBtn;
    private PlacedButtonComponent flameBtn;
    private PlacedButtonComponent arrowBtn;

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

        settings.setWidth(20 * 50 + 115);
        settings.setHeight(15 * 50);
        settings.setAppIcon("logo.jpg");
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("towerType", TowerType.NONE);

    }

    private void hideIndicator() {
        buildIndicator.setX(-1000);
        buildIndicator.setY(-1000);
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            TowerType towerType = FXGL.geto("towerType");
            if (towerType == TowerType.NONE) {
                return;
            }
            moveMouse(towerType);
        });

        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                FXGL.set("towerType", TowerType.NONE);
                hideIndicator();
                return;
            }
            TowerType towerType = FXGL.geto("towerType");
            if (towerType == TowerType.NONE) {
                return;
            }
            buildTower(towerType);
        });

    }

    private void moveMouse(TowerType towerType) {
        TowerData data = getTowerData(towerType);
        if (data == null) {
            return;
        }

        int w = data.getWidth();
        int h = data.getHeight();

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

    private void buildTower(TowerType towerType) {
        TowerData towerData = getTowerData(towerType);
        if (towerData == null) {
            return;
        }

        int w = towerData.getWidth();
        int h = towerData.getHeight();

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
            FXGL.spawn(towerData.getName(), x, y);
            FXGL.set("towerType", TowerType.NONE);
            hideIndicator();
        }

    }

    private TowerData getTowerData(TowerType towerType) {
        TowerData data;
        if (towerType == TowerType.LASER) {
            data = Config.LASER_TOWER_DATA;
        } else if (towerType == TowerType.THUNDER) {
            data = Config.THUNDER_TOWER_DATA;
        } else if (towerType == TowerType.FLAME) {
            data = Config.FLAME_TOWER_DATA;
        } else if (towerType == TowerType.ARROW) {
            data = Config.ARROW_TOWER_DATA;
        } else {
            return null;
        }
        buildIndicatorComponent.resetIndicator(data.getTowerIcon(), data.getAttackRadius());
        emptyEntity.getBoundingBoxComponent().clearHitBoxes();
        emptyEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(data.getWidth(), data.getHeight())));
        return data;
    }

    @Override
    protected void initGame() {
        FXGL.getGameScene().setBackgroundColor(Color.web("#16232B"));
        FXGL.image("enemy/enemy_move_right.png");
        FXGL.image("enemy/enemy_move_left.png");
        FXGL.image("enemy/enemy_move_up.png");
        FXGL.image("enemy/enemy_move_down.png");
        FXGL.image("enemy/enemy_die.png");
        FXGL.image("tower/laser/build.png");
        FXGL.image("tower/laser/attack.png");

        FXGL.getGameWorld().addEntityFactory(new GameEntityFactory());

        FXGL.setLevelFromMap("level1.tmx");
        //清理无用的实体;
        List<Entity> tempEntities = FXGL.getGameWorld().getEntitiesByType(GameType.SPACE, GameType.POINT);
        FXGL.getGameWorld().removeEntities(tempEntities);
        //建造指示器的创建
        buildIndicator = FXGL.spawn("buildIndicator");
        hideIndicator();
        buildIndicatorComponent = buildIndicator.getComponent(BuildIndicatorComponent.class);
        //buildIndicatorComponent.resetIndicator(FXGL.image("tower/thunder/tower_icon.png"), 150);

        //检测建造碰撞用的实体
        emptyEntity = FXGL.spawn("empty");
        emptyEntity.getBoundingBoxComponent().clearHitBoxes();
        emptyEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(30, 30)));
        emptyEntity.setX(-100);
        emptyEntity.setY(-100);
        //刷怪
        FXGL.runOnce(() -> {
            FXGL.run(() -> {
                FXGL.spawn("enemy", pointInfos.get(0).getKey());
            }, Duration.seconds(1), 20);
        }, Duration.seconds(5));

        FXGL.run(() -> {
            FXGL.run(() -> {
                FXGL.spawn("enemy", pointInfos.get(0).getKey());
            }, Duration.seconds(1), 20);
        }, Duration.seconds(30), 20);

        FXGL.spawn("placeBox");

        laserBtn = FXGL.spawn("placedButton", new SpawnData(1016, 60)
                .put("imgName", "tower/laser/tower_icon.png")
                .put("width", 58.0)
                .put("height", 102.0)
                .put("towerType", TowerType.LASER)).getComponent(PlacedButtonComponent.class);

        thunderBtn = FXGL.spawn("placedButton", new SpawnData(1016, 160)
                .put("imgName", "tower/thunder/tower_icon.png")
                .put("width", 42.0)
                .put("height", 72.0)
                .put("towerType", TowerType.THUNDER)).getComponent(PlacedButtonComponent.class);

        flameBtn = FXGL.spawn("placedButton", new SpawnData(1016, 260)
                .put("imgName", "tower/flame/tower_icon.png")
                .put("width", 42.0)
                .put("height", 89.0)
                .put("towerType", TowerType.FLAME)).getComponent(PlacedButtonComponent.class);

        arrowBtn = FXGL.spawn("placedButton", new SpawnData(1016, 360)
                .put("imgName", "tower/arrow/tower_icon.png")
                .put("width", 43.0)
                .put("height", 68.0)
                .put("towerType", TowerType.ARROW)).getComponent(PlacedButtonComponent.class);

        FXGL.getop("towerType").addListener((ob, ov, nv) -> {
            if (TowerType.LASER == nv) {
                selectedPlaceBtn(true, false, false, false);
            }
            if (TowerType.THUNDER == nv) {
                selectedPlaceBtn(false, true, false, false);
            }
            if (TowerType.FLAME == nv) {
                selectedPlaceBtn( false, false, true, false);
            }
            if (TowerType.ARROW == nv) {
                selectedPlaceBtn(false, false, false, true);
            }
            if (TowerType.NONE == nv) {
                selectedPlaceBtn(false, false, false, false);
            }
        });

    }

    private void selectedPlaceBtn(boolean laser, boolean thunder, boolean flame, boolean arrow) {
        laserBtn.setSelected(laser);
        thunderBtn.setSelected(thunder);
        flameBtn.setSelected(flame);
        arrowBtn.setSelected(arrow);
    }

    @Override
    protected void onPreInit() {
        FXGL.getSettings().setGlobalSoundVolume(0.5);
        FXGL.getSettings().setGlobalMusicVolume(0.5);
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
                BulletComponent component = bullet.getComponent(BulletComponent.class);
                bullet.removeFromWorld();
                enemyComponent.attacked(component.getDamage());
            }

        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
