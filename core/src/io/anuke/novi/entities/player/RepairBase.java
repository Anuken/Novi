package io.anuke.novi.entities.player;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.DestructibleEntity;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.utils.Draw;

public class RepairBase extends DestructibleEntity{
	
	{
		this.material.set(100);
	}
	
	@Override
	public void draw(){
		Novi.log("well " + x + " " + y);
		Draw.rect("playerbase", x, y);
	}
	
	//never collide with anything
	@Override
	public boolean collides(SolidEntity other){
		return false;
	}
}
