package io.anuke.novi.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import io.anuke.novi.Novi;
import io.anuke.novi.server.NoviServer;

public class Effect{
	public transient long seed;
	public float x, y;
	public transient float life;
	public float delay = 0;
	public EffectType type;
	public int color;
	
	public Effect(){
		
	}
	
	public Effect(EffectType type){
		this.type = type;
	}
	
	public Effect delay(float time){
		this.delay = time;
		return this;
	}
	
	public Effect color(Color color){
		this.color = Color.rgba8888(color);
		return this;
	}
	
	
	/**Updates this effect -- returns true if it needs to be removed*/
	public boolean update(){
		if(delay > 0){
			delay -= Novi.delta();
		}else{
			life += Novi.delta();
			if(life > type.lifetime()){
				return true;
			}
		}
		
		return false;
	}

	public void draw(){
		if(delay <= 0)
		type.draw(this);
	}
	
	public Effect set(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}
	
	public void send(){
		NoviServer.instance().sendNear(this, x, y);
	}

	public float getLayer(){
		return 1f;
	}
	
	public float fract(){
		return life/type.lifetime();
	}
	
	public void onRecieve(){
		if(color == 0 && type != null) color = Color.rgba8888(type.defaultColor());
		seed = MathUtils.random(0, Integer.MAX_VALUE-100);
	}
}
