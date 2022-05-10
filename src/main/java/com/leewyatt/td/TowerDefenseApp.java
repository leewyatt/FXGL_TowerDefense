package com.leewyatt.td;

import com.almasb.fxgl.app.CursorInfo;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.GameView;
import com.almasb.fxgl.app.scene.LoadingScene;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.core.util.LazyValue;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.physics.BoundingShape;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.HitBox;
import com.leewyatt.td.components.BuildIndicatorComponent;
import com.leewyatt.td.components.EnemyComponent;
import com.leewyatt.td.data.ConfigData;
import com.leewyatt.td.data.EnemyData;
import com.leewyatt.td.data.LevelData;
import com.leewyatt.td.data.TowerData;
import com.leewyatt.td.ui.*;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

/**
 * 图片,音乐等来自网络,本项目仅仅用于javafx和FXGL技术的学习.请勿用于商业用途;如果侵权,请联系我进行删除
 *
 * Pictures, music, etc. download from the Internet.
 * This project is only used for learning JavaFX and fxgl technology.
 * If it infringes, please contact me to delete it
 *
 */


/**
 * @author LeeWyatt
 */
public class TowerDefenseApp extends GameApplication {
    /**
     * 总关卡数
     */
    private static final int MAX_LEVEL = 5;
    /**
     * 开始的关卡
     */
    public static int START_LEVEL = 1;
    /**
     * 判断当前位置是否可以创建炮塔
     */
    private boolean canBuilder;
    /**
     * 保存敌人的移动路径,由多个关键的点构成
     * LinkedHashMap<Integer, Pair<Point2D, String>> p
     * Integer 存储点的索引, 第几个点
     * Pari<Point2D,String> point2D表示点的位置
     *                      String 表示方向,比如此刻是up,就表示敌人应该向上转;显示向上移动的图片
     */
    private final LinkedHashMap<Integer, Pair<Point2D, String>> pointInfos = new LinkedHashMap<>();
    /**
     * 存储可以建造炮塔的区域,只有炮塔在此区域内才可以建造
     */
    private ArrayList<Rectangle> spaceInfos = new ArrayList<>();
    /**
     * Toggle切换组
     * 多个炮塔(
     */
    private final ToggleGroup towerBtnGroup = new ToggleGroup();
    private TowerDetailPane detailPane;
    private Entity buildIndicator;
    private BuildIndicatorComponent buildIndicatorComponent;
    private HashMap<String, TowerData> towerMap = new HashMap<>();
    private Entity emptyEntity;
    private LevelData levelData;

    private LazyValue<LevelStartScene> levelStartSceneValue =
            new LazyValue<>(LevelStartScene::new);

    private LazyValue<LevelEndScene> levelEndSceneLazyValue =
            new LazyValue<>(LevelEndScene::new);

    private RangeIndicator rangeIndicator;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setTitle("Tower Defense");
        settings.setVersion("0.2");
        settings.setWidth(20 * 50 + 115);
        settings.setHeight(15 * 50);
        settings.setAppIcon("logo.jpg");
        settings.setDefaultCursor(new CursorInfo("cursor.png", 0, 0));
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new SceneFactory() {
            @Override
            public FXGLMenu newMainMenu() {
                return new MainMenu();
            }

            @Override
            public LoadingScene newLoadingScene() {
                return new GameLoadingScene();
            }
        });
    }

    @Override
    protected void initInput() {
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            String towerName = FXGL.gets("selectedTowerName");
            //如果没有选择炮塔.那么返回
            if (towerName.isEmpty()) {
                return;
            }
            //如果选择了炮塔,那么移动的时候,动态显示位置
            mouseMove(towerName);
        });
        FXGL.getInput().addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            //右键取消选择
            if (e.getButton() == MouseButton.SECONDARY) {
                FXGL.set("selectedTowerName", "");
                Toggle selectedToggle = towerBtnGroup.getSelectedToggle();
                if (selectedToggle != null) {
                    selectedToggle.setSelected(false);
                }
                hideIndicator();
                return;
            }
            String towerName = FXGL.gets("selectedTowerName");
            if (towerName.isEmpty() || !canBuilder) {
                return;
            }
            //如果选择了炮塔, 那么建造炮塔
            buildTower(towerName);
        });

    }

    private void mouseMove(String towerName) {
        canBuilder = false;
        TowerData towerData = towerMap.get(towerName);
        if (towerData == null) {
            return;
        }
        if (FXGL.geti("gold") < towerData.cost()) {
            return;
        }
        buildIndicatorComponent.updateIndicator(towerData);
        int w = towerData.width();
        int h = towerData.height();
        Point2D p = FXGL.getInput().getMousePositionWorld();
        //鼠标点击的位置是炮塔的中心
        double x = p.getX() - w / 2.0;
        double y = p.getY() - h / 2.0;
        buildIndicator.setX(x);
        buildIndicator.setY(y);
        boolean flag = false;
        for (Rectangle r : spaceInfos) {
            //判断是否可以建造;(如果spaceInfos里存入的是Rectangle2D,
            // 并且每次updateIndicator 每次修改图片时就改了宽高,那么可以用  buildIndicator.isWithin(r2d)来判断
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
        List<Entity> towers = FXGL.getGameWorld().getEntitiesByType(EntityType.TOWER);
        emptyEntity.getBoundingBoxComponent().clearHitBoxes();
        emptyEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingShape.box(towerData.width(), towerData.height())));
        boolean temp = true;
        for (Entity tower : towers) {
            //判断可以建造的地方是否有其他炮塔
            if (emptyEntity.isColliding(tower)) {
                temp = false;
                break;
            }
        }
        canBuilder = temp;
        buildIndicatorComponent.canBuild(canBuilder);
    }

    private void buildTower(String towerName) {
        TowerData towerData = towerMap.get(towerName);
        if (towerData == null) {
            return;
        }
        if (FXGL.geti("gold") < towerData.cost()) {
            return;
        }
        Point2D p = FXGL.getInput().getMousePositionWorld();
        double x = p.getX() - towerData.width() / 2.0;
        double y = p.getY() - towerData.height() / 2.0;
        FXGL.play("placed.wav");
        FXGL.spawn("tower", new SpawnData(x, y).put("towerData", towerData));
        FXGL.inc("gold", -towerData.cost());
        FXGL.set("selectedTowerName", "");
        Toggle selectedToggle = towerBtnGroup.getSelectedToggle();
        if (selectedToggle != null) {
            selectedToggle.setSelected(false);
        }
        hideIndicator();
    }

    @Override
    protected void initPhysics() {
        FXGL.getPhysicsWorld().addCollisionHandler(new CollisionHandler(EntityType.BULLET, EntityType.ENEMY) {
            @Override
            protected void onCollisionBegin(Entity bullet, Entity enemy) {
                EnemyComponent enemyComponent = enemy.getComponent(EnemyComponent.class);
                if (enemyComponent.isDead()) {
                    return;
                }
                enemyComponent.attacked(bullet.getObject("bulletData"));
                bullet.removeFromWorld();
            }
        });

        FXGL.onCollision(EntityType.FINISH_POINT, EntityType.ENEMY, (point, enemy) -> {
            FXGL.inc("health", -1);
            if (levelData != null && FXGL.geti("health") <= 0) {
                levelData = null;
                FXGL.set("complete", false);
                FXGL.runOnce(() -> FXGL.getSceneService().pushSubScene(levelEndSceneLazyValue.get()), Duration.seconds(1.0));
            }
            enemy.removeFromWorld();
        });
    }

    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("level", START_LEVEL);
        vars.put("gold", 0);
        vars.put("kill", 0);
        vars.put("complete", false);
        vars.put("health", ConfigData.INIT_HP);
        vars.put("selectedTowerName", "");
        vars.put("enemyPreview", "");
        vars.put("enemyHp", 0);
    }

    @Override
    protected void initGame() {
        rangeIndicator = new RangeIndicator();
        GameView gameView = new GameView(rangeIndicator, Integer.MAX_VALUE);
        FXGL.getGameScene().addGameView(gameView);

        FXGL.getGameScene().setBackgroundColor(Color.web("#16232B"));
        FXGL.getGameWorld().addEntityFactory(new TowerDefenseFactory());
        //tower box 存放炮塔按钮
        createTowersBox();
        //加载关卡
        loadLevel();
        //判断关卡是否完成;如果完成,显示完成界面
        checkTheLevelIsEnd();
    }

    private void checkTheLevelIsEnd() {
        ChangeListener<Number> numberChangeListener = (ob, ov, nv) -> {
            if (levelData != null && (FXGL.geti("kill") + ConfigData.INIT_HP - FXGL.geti("health") == levelData.amount())) {
                FXGL.set("complete", true);
                FXGL.runOnce(() -> {
                    FXGL.getSceneService().pushSubScene(levelEndSceneLazyValue.get());
                }, Duration.seconds(2));
            }
        };
        FXGL.getip("kill").addListener(numberChangeListener);
        FXGL.getip("health").addListener(numberChangeListener);
    }

    private void createTowersBox() {
        FXGL.spawn("towersBox");
        FXGL.spawn("towerButton", new SpawnData(1016, 60)
                .put("towerData", loadTowerData("laserTower")));
        FXGL.spawn("towerButton", new SpawnData(1016, 160)
                .put("towerData", loadTowerData("flameTower")));
        FXGL.spawn("towerButton", new SpawnData(1016, 260)
                .put("towerData", loadTowerData("iceTower")));
        FXGL.spawn("towerButton", new SpawnData(1016, 360)
                .put("towerData", loadTowerData("poisonTower")));
        FXGL.spawn("towerButton", new SpawnData(1016, 460)
                .put("towerData", loadTowerData("thunderTower")));
        FXGL.spawn("towerButton", new SpawnData(1016, 560)
                .put("towerData", loadTowerData("arrowTower")));
        FXGL.spawn("pauseButton", new SpawnData(1016, 660));
    }

    /**
     * 挑战失败时,重玩此关
     */
    public void restartLevel() {
        loadLevel();
    }

    /**
     * 继续下一关
     */
    public void continueNextLevel() {
        if (FXGL.geti("level") < MAX_LEVEL) {
            FXGL.inc("level", 1);
        } else {
            FXGL.set("level", 1);
        }
        loadLevel();
    }

    private void loadLevel() {
        Toggle selectedToggle = towerBtnGroup.getSelectedToggle();
        if (selectedToggle != null) {
            selectedToggle.setSelected(false);
        }
        //清理集合
        spaceInfos.clear();
        pointInfos.clear();
        //初始化数据与设置
        FXGL.set("kill", 0);
        FXGL.set("health", ConfigData.INIT_HP);
        FXGL.set("selectedTowerName", "");

        //读取关卡数据
        levelData = FXGL.getAssetLoader().loadJSON("levels/level" + FXGL.geti("level") + ".json", LevelData.class).get();
        FXGL.set("levelData", levelData);
        //设置当前关卡
        String map = levelData.map();
        FXGL.setLevelFromMap(map);

        //设置初始金币
        FXGL.set("gold", levelData.startingMoney());
        //生成ui相关的实体
        spawnEntities();
        //读取敌军数据
        EnemyData enemyData = FXGL.getAssetLoader().loadJSON(levelData.enemy(), EnemyData.class).get();
        FXGL.set("enemyPreview", enemyData.getPreview());
        FXGL.set("enemyHp", enemyData.getHp());
        //4. 弹出信息框
        FXGL.runOnce(() -> {
            FXGL.getSceneService().pushSubScene(levelStartSceneValue.get());
        }, Duration.seconds(0.5));
        //生成敌军实体
        spawnEnemy(enemyData);
        //终点传送门
        FXGL.spawn("finishPoint", new SpawnData(pointInfos.get(pointInfos.size() - 1).getKey()));
    }

    private void spawnEnemy(EnemyData enemyData) {
        Point2D point2D = pointInfos.get(0).getKey();
        FXGL.spawn("startPoint", point2D);
        FXGL.runOnce(() -> {
            FXGL.run(() -> {
                FXGL.spawn("enemy", new SpawnData(point2D).put("enemyData", enemyData));
            }, Duration.seconds(levelData.interval()), levelData.amount());
        }, Duration.seconds(3));

    }

    private void spawnEntities() {
        //建造指示器的创建
        buildIndicator = FXGL.spawn("buildIndicator");
        hideIndicator();
        buildIndicatorComponent = buildIndicator.getComponent(BuildIndicatorComponent.class);

        //用于检测的碰撞的不可见实体
        emptyEntity = FXGL.spawn("empty");
        emptyEntity.setX(-100);
        emptyEntity.setY(-100);
    }

    @Override
    protected void initUI() {
        FXGL.addUINode(new TopInfoPane());
        detailPane = new TowerDetailPane();
        FXGL.addUINode(detailPane);
        hideDetailPane();
    }

    public void onTowerPressed(Entity entity, int radius) {
        rangeIndicator.show(entity, radius);
    }

    public void onTowerReleased() {
        rangeIndicator.hide();
    }

    private TowerData loadTowerData(String tName) {
        TowerData towerData = FXGL.getAssetLoader().loadJSON("data/" + tName + ".json", TowerData.class).get();
        towerMap.put(towerData.name(), towerData);
        return towerData;
    }

    public LinkedHashMap<Integer, Pair<Point2D, String>> getPointInfos() {
        return pointInfos;
    }

    public void showDetailPane(double tx, double ty, TowerData towerData) {
        if (detailPane == null) {
            return;
        }
        detailPane.updateTowerData(tx, ty, towerData);
        detailPane.setVisible(true);
    }

    public void hideDetailPane() {
        detailPane.setVisible(false);
        detailPane.setTranslateY(-500);
    }

    private void hideIndicator() {
        buildIndicator.setX(-1000);
        buildIndicator.setY(-1000);
    }

    public ToggleGroup getTowerBtnGroup() {
        return towerBtnGroup;
    }

    public ArrayList<Rectangle> getSpaceInfos() {
        return spaceInfos;
    }

    @Override
    protected void onPreInit() {
        FXGL.getSettings().setGlobalSoundVolume(0.4);
        FXGL.getSettings().setGlobalMusicVolume(0.07);
        FXGL.loopBGM("bgm.mp3");
    }

    public static void main(String[] args) {
        launch(args);
    }

}
