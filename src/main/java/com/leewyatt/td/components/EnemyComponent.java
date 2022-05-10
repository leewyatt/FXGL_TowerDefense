package com.leewyatt.td.components;

import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.EffectComponent;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.dsl.effects.SlowTimeEffect;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.ui.ProgressBar;
import com.leewyatt.td.TowerDefenseApp;
import com.leewyatt.td.data.*;
import com.leewyatt.td.effect.BurnEffect;
import com.leewyatt.td.effect.CriticalHitEffect;
import com.leewyatt.td.effect.DizzinessEffect;
import com.leewyatt.td.effect.PoisonEffect;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author LeeWyatt
 * 
 * 敌人组件,不同移动方向, 有不同的图片
 *          不同的特殊伤害效果,有不同的表现
 */
public class EnemyComponent extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animWalkRight, animWalkLeft, animWalkUp, animWalkDown, animDie;
    private boolean dead;
    private ProgressBar hpBar;
    private Texture slowDownTexture;
    private LinkedHashMap<Integer, Pair<Point2D, String>> pointInfos;
    private Point2D nextWaypoint;
    private int index = 0;
    private int moveSpeed;
    private HealthIntComponent hp;
    private EnemyData enemyData;
    private int moveSpeedTemp;

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    @Override
    public void onAdded() {
        enemyData = entity.getObject("enemyData");
        moveSpeed = enemyData.getMoveSpeed();
        addHpComponentView(enemyData);
        List<AnimationData> animationData = enemyData.getAnimationData();
        for (AnimationData at : animationData) {
            if (at.status().equalsIgnoreCase("right")) {
                animWalkRight = initAc(at);
            } else if (at.status().equalsIgnoreCase("left")) {
                animWalkLeft = initAc(at);
            } else if (at.status().equalsIgnoreCase("up")) {
                animWalkUp = initAc(at);
            } else if (at.status().equalsIgnoreCase("down")) {
                animWalkDown = initAc(at);
            } else if (at.status().equalsIgnoreCase("die")) {
                animDie = initAc(at);
            }
        }
        texture = new AnimatedTexture(animWalkRight);
        entity.getViewComponent().addChild(texture);
        slowDownTexture = FXGL.texture("buffer/slow.png", entity.getWidth(), entity.getHeight());
        slowDownTexture.setVisible(false);
        entity.getViewComponent().addChild(slowDownTexture);
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

    public void stopMove() {
        moveSpeedTemp = moveSpeed;
        moveSpeed = 0;
        texture.stop();
    }

    public void restartMove() {
        moveSpeed = moveSpeedTemp;
        texture.loop();
    }

    @Override
    public void onUpdate(double tpf) {
        if (index >= pointInfos.size() || dead) {
            return;
        }
        boolean b = entity.getComponent(EffectComponent.class).hasEffect(SlowTimeEffect.class);
        slowDownTexture.setVisible(b);
        double speed = tpf * moveSpeed;
        Point2D velocity = nextWaypoint
                .subtract(entity.getPosition())
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
        }
    }

    public void attacked(BulletData bulletData) {
        int damage = bulletData.attackDamage();
        String effectName = bulletData.effectData().name();
        //减速
        if (effectName.equalsIgnoreCase(ConfigData.EFFECT_SLOW_DOWN)) {
            entity.getComponent(EffectComponent.class).startEffect(new SlowTimeEffect(0.4, Duration.seconds(5)));
        }
        //倍击
        if (effectName.equalsIgnoreCase(ConfigData.EFFECT_CRITICAL_HIT) && FXGLMath.randomBoolean(ConfigData.EFFECT_CRITICAL_HIT_CHANCE)) {
            boolean b = FXGLMath.randomBoolean(ConfigData.EFFECT_CRITICAL_HIT_CHANCE);
            if (b) {
                damage = damage * ConfigData.CRITICAL_HIT;
                entity.getComponent(EffectComponent.class).startEffect(new CriticalHitEffect());
            }
        }
        //眩晕
        if (effectName.equalsIgnoreCase(ConfigData.EFFECT_DIZZINESS) && FXGLMath.randomBoolean(ConfigData.EFFECT_DIZZINESS_CHANCE)) {
            entity.getComponent(EffectComponent.class).startEffect(new DizzinessEffect(Duration.seconds(2)));
        }
        //持续灼烧
        if (effectName.equalsIgnoreCase(ConfigData.EFFECT_BURN)) {
            entity.getComponent(EffectComponent.class).startEffect(new BurnEffect(Duration.seconds(5), 80, Duration.seconds(1)));
        }
        //中毒 (减速加持续伤害)
        if (effectName.equalsIgnoreCase(ConfigData.EFFECT_POISON)) {
            entity.getComponent(EffectComponent.class).startEffect(new PoisonEffect(0.65, Duration.seconds(5), 65, Duration.seconds(1)));
        }

        hp.damage(damage);

        if (hp.isZero()) {
            dead = true;
            FXGL.inc("kill", 1);
            FXGL.inc("gold", enemyData.getReward());
            entity.getViewComponent().removeChild(hpBar);
            texture.playAnimationChannel(animDie);
            texture.setOnCycleFinished(() -> entity.removeFromWorld());
        }
    }

    private AnimationChannel initAc(AnimationData at) {
        return new AnimationChannel(FXGL.image(at.imageName()), at.framesPerRow(), at.frameWidth(), at.frameHeight(),
                Duration.seconds(at.channelDuration()), at.startFrame(), at.endFrame());
    }

    private void addHpComponentView(EnemyData enemyData) {
        int maxHp = enemyData.getHp();
        hp = new HealthIntComponent(maxHp);
        hpBar = new ProgressBar(false);
        hpBar.setFill(Color.LIGHTGREEN);
        hpBar.setWidth(40);
        hpBar.setTranslateX((enemyData.getWidth() - 40) / 2.0);
        hpBar.setHeight(7);
        hpBar.setTranslateY(-5);
        hpBar.setMaxValue(maxHp);
        hpBar.setCurrentValue(maxHp);
        hpBar.currentValueProperty().bind(hp.valueProperty());
        hp.valueProperty().addListener((ob, ov, nv) -> {
            int value = nv.intValue();
            if (value > maxHp * 0.65) {
                hpBar.setFill(Color.LIGHTGREEN);
            } else if (value > maxHp * 0.25) {
                hpBar.setFill(Color.GOLD);
            } else {
                hpBar.setFill(Color.RED);
            }
        });
        entity.getViewComponent().addChild(hpBar);
        entity.addComponent(hp);
    }
}
