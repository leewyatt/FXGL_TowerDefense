package com.itcodebox.td;

import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class TowerData {

    private int damage = 1;
    private int towerAttackRadius = 215;
    private Duration attackDelay = Duration.seconds(0.5);

    public int getDamage() {
        return damage;
    }

    public Duration getAttackDelay() {
        return attackDelay;
    }

    public int getTowerAttackRadius() {
        return towerAttackRadius;
    }
}
