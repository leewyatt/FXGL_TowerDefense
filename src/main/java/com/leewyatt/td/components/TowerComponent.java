package com.leewyatt.td.components;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import com.leewyatt.td.EntityType;
import com.leewyatt.td.TowerDefenseApp;
import com.leewyatt.td.data.AnimationData;
import com.leewyatt.td.data.BulletData;
import com.leewyatt.td.data.ConfigData;
import com.leewyatt.td.data.TowerData;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

import java.util.List;

/**
 * @author LeeWyatt
 * 
 * 炮塔组件, 攻击进入射程范围的敌人
 */
public class TowerComponent extends Component {
    private LocalTimer shootTimer;
    private AnimationChannel ac;
    private AnimatedTexture at;
    private TowerData towerData;
    private String towerName;
    private Duration attackRate;
    private Point2D towerPosition;
    private BulletData bulletData;

    @Override
    public void onAdded() {
        towerData = entity.getObject("towerData");
        towerName = towerData.name();
        attackRate = Duration.seconds(towerData.attackRate());
        AnimationData ad = towerData.animationData();
        ac = new AnimationChannel(
                FXGL.image(ad.imageName()),
                ad.framesPerRow(),
                ad.frameWidth(),
                ad.frameHeight(),
                Duration.seconds(ad.channelDuration()),
                ad.startFrame(),
                ad.endFrame());
        at = new AnimatedTexture(ac);

        towerPosition = entity.getPosition();
        bulletData = towerData.bulletData();
        entity.getViewComponent().addChild(at);
        at.loop();
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();

        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            if (event.getButton() != MouseButton.PRIMARY) {
                return;
            }
            FXGL.<TowerDefenseApp>getAppCast().onTowerPressed(entity, bulletData.range());
        });
        entity.getViewComponent().addEventHandler(MouseEvent.MOUSE_RELEASED, event -> {
            FXGL.<TowerDefenseApp>getAppCast().onTowerReleased();
        });
    }

    public void showRangeIndicator() {
        FXGL.<TowerDefenseApp>getAppCast().onTowerPressed(entity, bulletData.range());

    }
    public void hideRangeIndicator() {
        FXGL.<TowerDefenseApp>getAppCast().onTowerReleased();
    }

    @Override
    public void onUpdate(double tpf) {
        if (!shootTimer.elapsed(attackRate)) {
            return;
        }
        if ("Arrow Tower".equalsIgnoreCase(towerName)) {
            arrowTowerAttack();
        } else {
            attack();
        }
    }


    private void attack() {
        FXGL.getGameWorld().getClosestEntity(entity,
                        e -> e.isType(EntityType.ENEMY)
                                && e.getPosition().distance(towerPosition) < bulletData.range()
                )
                .ifPresent(enemy -> {
                    shootBullet(enemy);
                    shootTimer.capture();
                });
    }

    private void arrowTowerAttack() {
        List<Entity> es = FXGL.getGameWorld().getEntitiesByType(EntityType.ENEMY);
        int bulletNum = 0;
        boolean flag = false;
        for (Entity enemy : es) {
            if (bulletNum > ConfigData.MAX_BULLET_AMOUNT) {
                break;
            }
            Point2D ep = enemy.getPosition();
            //判断是否在射程之内 ; TODO 用 enemy.distanceBBox(tower) 这样判断其实更精确
            if (ep.distance(towerPosition) <= bulletData.range()) {
                flag = true;
                bulletNum++;
                shootBullet(enemy);
            }
        }
        if (flag) {
            shootTimer.capture();
        }
    }

    private void shootBullet(Entity enemy) {
        Point2D dir = enemy.getPosition().subtract(towerPosition);
        FXGL.spawn("bullet", new SpawnData(
                entity.getCenter().subtract(bulletData.width() / 2.0, bulletData.height() / 2.0))
                .put("bulletData", bulletData)
                .put("dir", dir)
        );
    }

}
