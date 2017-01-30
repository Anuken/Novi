package io.anuke.novi.entities.effects;

import com.badlogic.gdx.graphics.Color;

public class ExplosionEffect extends Effect{
	{
		lifetime = 10;
	}
	
	@Override
	public void draw(){
		renderer.layer("explosion", x, y).setLayer(3f).setColor(life >= lifetime /2 ? Color.BLACK : Color.WHITE);
	}
}
