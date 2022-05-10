package com.leewyatt.td.data;

/**
 * 特殊效果元数据,与json数据对应
 */
public record EffectData(
	String imgName,
	String name,
	String desc,
	String descEn
) {
}
