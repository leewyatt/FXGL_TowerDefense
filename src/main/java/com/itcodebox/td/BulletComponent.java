package com.itcodebox.td;

import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

/**
 * @author LeeWyatt
 */
public class BulletComponent extends Component {

    private Point2D initPosition;

    @Override
    public void onAdded() {
        initPosition = entity.getPosition();
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D position = entity.getPosition();
        if (position.distance(initPosition)>Config.TOWER_DATA.getTowerAttackRadius()) {
            if (entity.isActive()) {
                entity.removeFromWorld();
            }
        }
    }
}
