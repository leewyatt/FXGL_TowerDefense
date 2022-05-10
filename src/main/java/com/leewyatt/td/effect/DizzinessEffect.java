package com.leewyatt.td.effect;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.Effect;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.leewyatt.td.components.EnemyComponent;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 *
 * 敌人被击晕的效果
 * 1. 击晕的图片
 * 2. 停止移动
 */
public class DizzinessEffect extends Effect {
    AnimationChannel acDizziness = new AnimationChannel(FXGL.image("buffer/vertigo.png"), Duration.seconds(0.5), 6);
    AnimatedTexture atDizziness = new AnimatedTexture(acDizziness);
    private EnemyComponent enemyComponent;

    /**
     *
     * @param duration 停止移动的时间
     */
    public DizzinessEffect(Duration duration) {
        super(duration);
    }


    @Override
    public void onStart(Entity entity) {
        enemyComponent = entity.getComponent(EnemyComponent.class);
        enemyComponent.stopMove();
        atDizziness.setTranslateX((entity.getWidth()-acDizziness.getFrameWidth(0))/2.0);
        atDizziness.setTranslateY(-acDizziness.getFrameHeight(0)+10);
        entity.getViewComponent().addChild(atDizziness);
        atDizziness.loop();
    }

    @Override
    public void onEnd( Entity entity) {
        entity.getViewComponent().removeChild(atDizziness);
        atDizziness.stop();
        enemyComponent.restartMove();
    }
}
