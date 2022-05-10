package com.leewyatt.td.data;

/**
 * 关卡元数据,与json数据对应
 */
public record LevelData(
	int amount,
	int startingMoney,
	String name,
	String enemy,
	double interval,
	String map
) {
}
