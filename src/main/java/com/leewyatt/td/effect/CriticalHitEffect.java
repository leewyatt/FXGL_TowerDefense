package com.leewyatt.td.effect;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 *
 * 敌人被暴击的效果
 * 1. 暴击图片
 */
public class CriticalHitEffect extends Effect {
    private static Duration duration = Duration.seconds(0.18);
    private final AnimationChannel ac;
    private AnimatedTexture at;

    public CriticalHitEffect() {
        super(duration);
        ac = new AnimationChannel(FXGL.image("buffer/critical2.png"),duration,4);
        at = new AnimatedTexture(ac);
    }

    @Override
    public void onStart(Entity entity) {
        at.setTranslateX((entity.getWidth()-ac.getFrameWidth(0))/2.0);
        at.setTranslateY(-ac.getFrameHeight(0)-7);
        entity.getViewComponent().addChild(at);
        at.play();
    }

    @Override
    public void onEnd( Entity entity) {
        entity.getViewComponent().removeChild(at);
    }
}
