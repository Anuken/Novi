package io.anuke.novi.entities.effects;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novi.utils.Draw;
import io.anuke.ucore.UCore;

public enum EffectType{
	explosion{
		public int lifetime(){
			return 10;
		}
		
		public void draw(Effect e){
			Draw.color(e.life >= lifetime() /2 ? Color.BLACK : Color.WHITE);
			Draw.rect("explosion", e.x, e.y);
			Draw.color();
		}
	}, 
	shockwave;
	protected String[] frames;
	
	private EffectType(String... frames){
		this.frames = frames;
	}
	
	private EffectType(int frameamount){
		frames = new String[frameamount];
		
		for(int i = 0; i < frames.length; i ++)
			frames[i] = drawName() + (i+1);
	}
	
	public String drawName(){
		return name();
	}
	
	public int lifetime(){
		return 100;
	}
	
	public void draw(Effect e){
		if(frames != null && frames.length > 0){
			Draw.rect(frames[(int)UCore.clamp(e.life/lifetime())*frames.length], e.x, e.y);
		}else{
			Draw.rect("error", e.x, e.y);
		}
	}
}
