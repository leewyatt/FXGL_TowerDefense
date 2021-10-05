package com.itcodebox.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.geometry.Point2D;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class TowerComponent extends Component {

    private LocalTimer shootTimer;

    private AnimationChannel acBuild,acAttack;
    private AnimatedTexture texture;
    private boolean isBuilding;

    public TowerComponent() {
        acBuild = new AnimationChannel(FXGL.image("tower/build.png", 29 * 5, 51 * 2), 5, 29, 51, Duration.seconds(1), 0, 9);
        acAttack = new AnimationChannel(FXGL.image("tower/attack.png", 29 * 4, 51 * 2), 4, 29, 51, Duration.seconds(.5), 0, 7);
        texture = new AnimatedTexture(acBuild);
    }

    @Override
    public void onAdded() {
        isBuilding = true;
        entity.getViewComponent().addChild(texture);
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();
        texture.play();
        FXGL.runOnce(() -> {
            isBuilding = false;
        }, Duration.seconds(.5));
    }

    @Override
    public void onUpdate(double tpf) {

        if (shootTimer.elapsed(Config.TOWER_DATA.getAttackDelay()) && !isBuilding) {
            FXGL.getGameWorld()
                    .getClosestEntity(entity, e -> e.isType(GameType.ENEMY) && !e.getComponent(EnemyComponent.class).isDead() )
                    .ifPresent(nearestEnemy -> {
                        Point2D ep = nearestEnemy.getPosition();
                        Point2D tp = entity.getPosition();
                        if (ep.distance(tp) <= Config.TOWER_DATA.getTowerAttackRadius()) {
                            texture.playAnimationChannel(acAttack);
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
        Entity bullet = FXGL.spawn("bullet", getEntity().getCenter().subtract(30/2.0, 10/2.0));
        bullet.addComponent(new ProjectileComponent(direction, 8 * 60));

    }
}
