package io.anuke.novi.entities.effects;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novi.utils.Draw;

public class ExplosionEffect extends Effect{
	{
		lifetime = 10;
	}
	
	@Override
	public void draw(){
		Draw.color(life >= lifetime /2 ? Color.BLACK : Color.WHITE);
		Draw.rect("explosion", x, y);
		Draw.color();
	}
}
