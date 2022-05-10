package com.leewyatt.td.effect;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.dsl.components.HealthIntComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.components.TimeComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 *
 * 敌人被施毒
 * 1. 中毒效果
 * 2. 减速且持续伤害
 */
public class PoisonEffect extends Effect {

    private LocalTimer timer;
    private Duration damageDuration;
    private int damage;
    private double ratio;

    AnimationChannel acPoison = new AnimationChannel(FXGL.image("buffer/poison.png"), Duration.seconds(0.2), 4);
    AnimatedTexture atPoison = new AnimatedTexture(acPoison);

    /**
     *
     * @param ratio 减速的比率
     * @param duration 中毒的总时间
     * @param damage 每次毒伤的伤害
     * @param damageDuration 每次毒伤的间隔时间
     */
    public PoisonEffect(double ratio,Duration duration, int damage, Duration damageDuration) {
        super(duration);
        this.ratio = ratio;
        this.damage = damage;
        this.damageDuration = damageDuration;
        timer = FXGL.newLocalTimer();
    }



    @Override
    public void onStart(Entity entity) {
        timer.capture();
        atPoison.setTranslateX((entity.getWidth()-acPoison.getFrameWidth(0))/2.0);
        entity.getViewComponent().addChild(atPoison);
        atPoison.loop();
        //通过TimeComponent组件, 来减速;ratio是减速的比率
        entity.getComponent(TimeComponent.class).setValue(ratio);
    }

    @Override
    public void onEnd(Entity entity) {
        entity.getComponent(TimeComponent.class).setValue(1.0);
        entity.getViewComponent().removeChild(atPoison);
        atPoison.stop();
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
