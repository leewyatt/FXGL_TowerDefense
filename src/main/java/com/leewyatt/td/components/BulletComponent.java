package com.leewyatt.td.components;

import com.almasb.fxgl.entity.component.Component;
import com.leewyatt.td.data.BulletData;
import com.leewyatt.td.data.EffectData;
import javafx.geometry.Point2D;

/**
 * @author LeeWyatt
 *
 * 子弹组件,如果超过射程,那么移除
 */
public class BulletComponent extends Component {

    /**
     * 初始位置
     */
    private Point2D initPosition;
    /**
     * 伤害
     */
    private int damage;
    /**
     * 攻击半径
     */
    private int range;
    /**
     * 攻击效果
     */
    private String effectName;


    @Override
    public void onAdded() {
        initPosition = entity.getPosition();
        BulletData bulletData = entity.getObject("bulletData");
        EffectData effectData = bulletData.effectData();
        effectName = effectData.name();
        damage = bulletData.attackDamage();
        range = bulletData.range();
    }

    @Override
    public void onUpdate(double tpf) {
        Point2D newPosition = entity.getPosition();
        //如果超过射程(攻击范围).那么移除
        if (newPosition.distance(initPosition) > range) {
            if (entity.isActive()) {
                entity.removeFromWorld();
            }
        }

    }

    public int getDamage() {
        return damage;
    }

    public String getEffectName() {
        return effectName;
    }
}
