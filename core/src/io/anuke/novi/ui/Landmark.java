package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.Color;

public enum Landmark{
	base("base", Color.ROYAL), enemybase("base", Color.FIREBRICK);
	private Color color;
	private String texture;
	
	
	private Landmark(String tex, Color color){
		this.texture = tex;
		this.color = color;
	}
	
	public String texture(){
		return texture;
	}
	
	public Color color(){
		return color;
	}
}
