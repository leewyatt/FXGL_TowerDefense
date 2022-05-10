package com.leewyatt.td.data;
/**
 * 炮塔元数据,与json数据对应
 */
public record TowerData(
	int cost,
	AnimationData animationData,
	String name,
	String icon,
	int width,
	BulletData bulletData,
	double attackRate,
	int height
) {
}
