package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.Color;

public enum MarkerType{
	base("base", "friendly base", Color.ROYAL), enemybase("base", "enemy base", Color.FIREBRICK), player("player", "player", Color.GREEN);
	private Color color;
	private String texture;
	private String desc;
	
	
	private MarkerType(String tex, String desc, Color color){
		this.texture = tex;
		this.color = color;
		this.desc = desc;
	}
	
	public String description(){
		return desc;
	}
	
	public String texture(){
		return texture;
	}
	
	public Color color(){
		return color;
	}
}
