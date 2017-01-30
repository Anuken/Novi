package io.anuke.novi.entities.effects;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novi.sprites.Layer;

public class Shockwave extends Effect{
	float scale = 0.001f, grow = 0.09f;
	
	public Shockwave(){
		lifetime = 16;
	}
	
	public Shockwave(float lifetime, float scale, float grow){
		this.scale = scale;
		this.grow = grow;
		this.lifetime = lifetime;
	}
	
	@Override
	public void draw(){
		Layer layer = renderer.layer("shockwave", x, y).setScale(scale);
		float scl = 2f;
		if(life > lifetime / scl) layer.setColor(new Color(1, 1f, 1f, 1f - (life - lifetime / 2f) / (lifetime / scl)));
		scale += grow * delta();
	}

}
