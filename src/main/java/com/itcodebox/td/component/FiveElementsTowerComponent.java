package com.itcodebox.td.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import com.itcodebox.td.constant.GameType;
import com.itcodebox.td.data.TowerData;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class FiveElementsTowerComponent extends Component {

    private final AnimatedTexture texture;
    private LocalTimer shootTimer;
    private TowerData towerData;

    public FiveElementsTowerComponent(TowerData towerData) {
        this.towerData = towerData;
        AnimationChannel ac = new AnimationChannel(FXGL.image("tower/" + towerData.getName().replace("Tower", "") + "/tower.png"), Duration.seconds(.5), 15);
        texture = new AnimatedTexture(ac);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture.loop());
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (shootTimer.elapsed(towerData.getAttackDelay())) {
            FXGL.getGameWorld()
                    .getClosestEntity(entity, e -> e.isType(GameType.ENEMY) && !e.getComponent(EnemyComponent.class).isDead())
                    .ifPresent(nearestEnemy -> {
                        Point2D ep = nearestEnemy.getPosition();
                        Point2D tp = entity.getPosition();
                        if (ep.distance(tp) <= towerData.getAttackRadius()) {
                            shoot(nearestEnemy);
                            shootTimer.capture();
                        }
                    });
        }
    }

    private void shoot(Entity enemy) {
        Point2D position = getEntity().getPosition();
        Point2D direction = enemy.getPosition().subtract(position);

        //FXGL.play("fire_laser.wav");
        Entity bullet = FXGL.spawn(towerData.getName() + "Bullet", new SpawnData(entity.getCenter().subtract(30/ 2.0, 10 / 2.0))
                .put("radius", towerData.getAttackRadius())
                .put("damage", towerData.getDamage()));

        bullet.addComponent(new ProjectileComponent(direction, towerData.getBulletSpeed()));

    }
}
