package com.leewyatt.td.components;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.leewyatt.td.data.BulletData;
import com.leewyatt.td.data.EffectData;
import javafx.geometry.Point2D;

import java.util.HashSet;
import java.util.Set;

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

    /**
     * 攻击批次 ID
     * -1 表示普通子弹，不参与批次逻辑
     * >= 0 表示箭塔的群攻箭
     */
    private int batchId;

    /**
     * 静态集合：记录已经命中的 (批次ID + 敌人ID) 组合
     * 格式："batchId_enemyId"
     * 用于确保同一批次的箭对同一敌人只造成一次伤害
     */
    private static final Set<String> hitRecords = new HashSet<>();

    /**
     * 清理命中记录（在关卡重置时调用）
     */
    public static void clearHitRecords() {
        hitRecords.clear();
    }

    @Override
    public void onAdded() {
        initPosition = entity.getPosition();
        BulletData bulletData = entity.getObject("bulletData");
        EffectData effectData = bulletData.effectData();
        effectName = effectData.name();
        damage = bulletData.attackDamage();
        range = bulletData.range();
        batchId = entity.getInt("batchId");
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

    public int getBatchId() {
        return batchId;
    }

    /**
     * 生成命中记录的 key
     */
    private String getHitKey(Entity enemy) {
        return batchId + "_" + enemy.hashCode();
    }

    /**
     * 检查此批次的箭是否可以对指定敌人造成伤害
     * @param enemy 被碰撞的敌人
     * @return true 表示可以造成伤害
     */
    public boolean canDealDamage(Entity enemy) {
        // 非批次子弹（普通炮塔），始终可以造成伤害
        if (batchId < 0) {
            return true;
        }
        // 批次子弹，检查这个敌人是否已被此批次命中过
        return !hitRecords.contains(getHitKey(enemy));
    }

    /**
     * 标记此批次已经命中指定敌人
     * @param enemy 被命中的敌人
     */
    public void markHit(Entity enemy) {
        if (batchId >= 0) {
            hitRecords.add(getHitKey(enemy));
        }
    }
}