package com.itcodebox.td;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.LinkedHashMap;

/**
 * @author LeeWyatt
 */
public class EnemyComponent extends Component {

    private HealthIntComponent hp;
    private LinkedHashMap<Integer, Pair<Point2D, String>> pointInfos;
    private Point2D nextWaypoint;
    private double speed;
    int index = 0;
    private ProgressBar hpBar;
    private AnimatedTexture texture;
    private AnimationChannel animWalkRight, animWalkLeft, animWalkUp, animWalkDown, animDie;
    private boolean dead;

    public EnemyComponent(ProgressBar hpBar) {
        this.hpBar = hpBar;
        animWalkRight = new AnimationChannel(FXGL.image("enemy/enemy_move_right.png", 5 * 48, 48 * 3), 5, 48, 48, Duration.seconds(.5), 0, 14);
        animWalkLeft = new AnimationChannel(FXGL.image("enemy/enemy_move_left.png", 5 * 48, 48 * 3), 5, 48, 48, Duration.seconds(.5), 0, 14);
        animWalkUp = new AnimationChannel(FXGL.image("enemy/enemy_move_up.png", 5 * 48, 48 * 3), 5, 48, 48, Duration.seconds(.5), 0, 14);
        animWalkDown = new AnimationChannel(FXGL.image("enemy/enemy_move_down.png", 5 * 48, 48 * 3), 5, 48, 48, Duration.seconds(.5), 0, 14);
        animDie = new AnimationChannel(FXGL.image("enemy/enemy_die.png", 5 * 48, 2*48 ), 5, 48, 48, Duration.seconds(.25), 0, 8);

        texture = new AnimatedTexture(animWalkRight);

    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public void attacked() {
        hp.damage(Config.TOWER_DATA.getDamage());
        if (hp.isZero()) {
            dead = true;
            entity.getViewComponent().removeChild(hpBar);
            texture.loopAnimationChannel(animDie);
            texture.setOnCycleFinished(()->entity.removeFromWorld());
        }

    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        TowerDefenseApp app = (TowerDefenseApp) (FXGL.getApp());
        pointInfos = app.getPointInfos();
        nextWaypoint = pointInfos.get(index).getKey();
        walkAnim();
    }

    private void walkAnim() {
        String dir = pointInfos.get(index).getValue();
        if ("left".equals(dir)) {
            texture.loopAnimationChannel(animWalkLeft);
        } else if ("right".equals(dir)) {
            texture.loopAnimationChannel(animWalkRight);
        } else if ("up".equals(dir)) {
            texture.loopAnimationChannel(animWalkUp);
        } else if ("down".equals(dir)) {
            texture.loopAnimationChannel(animWalkDown);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        if (index >= pointInfos.size() || dead) {
            return;
        }
        speed = tpf * 30 * 2;

        Point2D velocity = nextWaypoint.subtract(entity.getPosition())
                .normalize()
                .multiply(speed);

        entity.translate(velocity);

        if (nextWaypoint.distance(entity.getPosition()) < speed) {
            entity.setPosition(nextWaypoint);
            walkAnim();
            index++;
            if (index < pointInfos.size()) {
                nextWaypoint = pointInfos.get(index).getKey();
            }

            //else {
            //    FXGL.getEventBus().fireEvent(new EnemyReachedGoalEvent());
            //}
        }
    }
}
