package io.anuke.novi.entities.effects;

import io.anuke.novi.utils.Draw;

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
		float scl = 2f;
		
		if(life > lifetime / scl) Draw.color(1f - (life - lifetime / 2f) / (lifetime / scl));
		Draw.rect("shockwave", x, y, scale, scale, 0);
		Draw.color();
		
		scale += grow * delta();
	}

}
