package com.itcodebox.td.component;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.ProjectileComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import com.itcodebox.td.constant.Config;
import com.itcodebox.td.constant.GameType;
import com.itcodebox.td.data.TowerData;
import javafx.geometry.Point2D;

import java.util.List;

/**
 * @author LeeWyatt
 */
public class ArrowTowerComponent extends Component {
    private LocalTimer shootTimer;
    private TowerData towerData = Config.ARROW_TOWER_DATA;
    private  int maxBullet = 10;
    @Override
    public void onAdded() {
        shootTimer = FXGL.newLocalTimer();
        shootTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        if (shootTimer.elapsed(towerData.getAttackDelay())) {
            List<Entity> entitiesByType = FXGL.getGameWorld().getEntitiesByType(GameType.ENEMY);
            int bulletNum = 0;
            for (Entity value : entitiesByType) {
                if (bulletNum > maxBullet) {
                    break;
                }
                Point2D ep = value.getPosition();
                Point2D tp = entity.getPosition();
                if (ep.distance(tp) <= towerData.getAttackRadius()) {
                    shoot(value);
                    shootTimer.capture();
                    bulletNum++;
                }
            }

        }
    }

    private void shoot(Entity enemy) {
        Point2D position = getEntity().getPosition();
        Point2D direction = enemy.getPosition().subtract(position);

        //FXGL.play("fire_laser.wav");
        Entity bullet = FXGL.spawn(towerData.getName() + "Bullet", new SpawnData(entity.getCenter().subtract(50 / 2.0, 10 / 2.0))
                .put("radius", towerData.getAttackRadius())
                .put("damage", towerData.getDamage()));

        bullet.addComponent(new ProjectileComponent(direction, towerData.getBulletSpeed()));

    }
}
