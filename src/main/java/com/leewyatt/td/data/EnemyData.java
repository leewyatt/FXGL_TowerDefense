package com.leewyatt.td.data;

import java.util.List;
/**
 * 敌人元数据,与json数据对应
 */
public class EnemyData{
	private int reward;
	private String preview;
	private int moveSpeed;
	private List<AnimationData> animationData;
	private int hp;
	private int width;
	private BboxData bboxData;
	private int height;

	public int getReward(){
		return reward;
	}

	public String getPreview(){
		return preview;
	}

	public int getMoveSpeed(){
		return moveSpeed;
	}

	public List<AnimationData> getAnimationData(){
		return animationData;
	}

	public int getHp(){
		return hp;
	}

	public void setHp(int hp) {
		this.hp = hp;
	}

	public int getWidth(){
		return width;
	}

	public BboxData getBboxData(){
		return bboxData;
	}

	public int getHeight(){
		return height;
	}
}