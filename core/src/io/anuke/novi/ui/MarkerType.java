package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novi.Novi;
import io.anuke.novi.effects.EffectType;
import io.anuke.novi.effects.Effects;
import io.anuke.novi.modules.ClientData;
import io.anuke.novi.modules.UI;

public enum MarkerType{
	base("base", "friendly base", Color.ROYAL){
		{action = "[click to teleport]";}
		
		public void clicked(Marker marker){
			Novi.module(ClientData.class).player.set(marker.x, marker.y);
			Effects.effect(EffectType.shockwave, marker.x, marker.y);
			Effects.effect(EffectType.smoke, marker.x, marker.y);
			
			Novi.module(UI.class).toggleMap();
		}
	}, 
	enemybase("base", "enemy base", Color.FIREBRICK), 
	player("player", "player", Color.GREEN);
	private Color color;
	private String texture;
	private String desc;
	protected String action;
	
	
	private MarkerType(String tex, String desc, Color color){
		this.texture = tex;
		this.color = color;
		this.desc = desc;
	}
	
	public void clicked(Marker marker){
		
	}
	
	public String description(){
		return desc;
	}
	
	public String action(){
		return action;
	}
	
	public String texture(){
		return texture;
	}
	
	public Color color(){
		return color;
	}
}
