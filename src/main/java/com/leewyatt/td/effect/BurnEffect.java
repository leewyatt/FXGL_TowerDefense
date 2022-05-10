package com.leewyatt.td.effect;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 *
 * 敌人被燃烧攻击的效果
 * 1. 持续伤害
 * 2. 火焰效果
 *
 */
public class BurnEffect extends Effect {

    private LocalTimer timer;
    private Duration damageDuration;
    private int damage;

    AnimationChannel acBurn = new AnimationChannel(FXGL.image("buffer/burn.png"),4,15,20,Duration.seconds(0.5),0,7);
    AnimatedTexture atBurn = new AnimatedTexture(acBurn);

    /**
     *
     * @param duration 效果的持续时间
     * @param damage 每次伤害的效果
     * @param damageDuration 每次伤害与上次伤害的间隔时间
     */
    public BurnEffect(Duration duration,int damage,Duration damageDuration) {
        super(duration);
        this.damage = damage;
        this.damageDuration = damageDuration;
        timer = FXGL.newLocalTimer();
    }


    @Override
    public void onStart(Entity entity) {
        atBurn.setTranslateX((entity.getWidth()-acBurn.getFrameWidth(0))/2.0);
        atBurn.setTranslateY(-acBurn.getFrameHeight(0)-7);
        entity.getViewComponent().addChild(atBurn);
        atBurn.loop();
        timer.capture();
    }

    @Override
    public void onEnd(Entity entity) {
        atBurn.stop();
        entity.getViewComponent().removeChild(atBurn);
    }

    @Override
    public void onUpdate(Entity entity, double tpf) {
        if (timer.elapsed(damageDuration)) {
            HealthIntComponent hp = entity.getComponent(HealthIntComponent.class);
            hp.damage(damage);
            timer.capture();
        }
    }
}
