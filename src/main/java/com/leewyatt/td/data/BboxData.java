package com.leewyatt.td.data;

/**
 * 元数据,bbox信息与json文件里的数据对应
 */
public record BboxData(
	int w,
	int x,
	int h,
	int y
) {
}
