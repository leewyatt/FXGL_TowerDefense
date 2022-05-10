package com.leewyatt.td.data;

/**
 * 动画元数据 json里的数据对应
 */
public record AnimationData(
	int frameHeight,
	int startFrame,
	int framesPerRow,
	String imageName,
	int frameWidth,
	int width,
	int endFrame,
	double channelDuration,
	String status,
	int height
) {
}
