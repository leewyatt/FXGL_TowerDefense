package com.itcodebox.td.data;

import javafx.scene.image.Image;
import javafx.util.Duration;

/**
 * @author LeeWyatt
 */
public class TowerData {
    private int width;
    private int height;
    private int damage;
    private int attackRadius;
    private Duration attackDelay;
    private Image towerIcon;
    private String name;
    private int bulletSpeed;

    public TowerData() {
    }

    public TowerData(String name,int width, int height, int damage, int attackRadius, int bulletSpeed,Duration attackDelay,Image towerIcon) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.damage = damage;
        this.attackRadius = attackRadius;
        this.bulletSpeed= bulletSpeed;
        this.attackDelay = attackDelay;
        this.towerIcon =towerIcon;
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getTowerIcon() {
        return towerIcon;
    }

    public void setTowerIcon(Image towerIcon) {
        this.towerIcon = towerIcon;
    }

    public int getDamage() {
        return damage;
    }

    public Duration getAttackDelay() {
        return attackDelay;
    }

    public int getAttackRadius() {
        return attackRadius;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setAttackRadius(int attackRadius) {
        this.attackRadius = attackRadius;
    }

    public void setAttackDelay(Duration attackDelay) {
        this.attackDelay = attackDelay;
    }
}
