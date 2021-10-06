package com.itcodebox.td.component;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/**
 * @author LeeWyatt
 */
public class BulletComponent extends Component {

    private Point2D initPosition;

    private final int radius;
    private int damage;

    public BulletComponent(int radius, int damage) {
        this.radius = radius;
        this.damage = damage;
    }

    @Override
    public void onAdded() {
        initPosition = entity.getPosition();
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D position = entity.getPosition();
        if (position.distance(initPosition) > radius) {
            if (entity.isActive()) {
                entity.removeFromWorld();
            }
        }
    }

    public int getDamage() {
        return damage;
    }

}
