package io.anuke.novi.entities.base;

import io.anuke.novi.Novi;
import io.anuke.novi.utils.Draw;

public class BaseFactory extends BaseBlock{
	static final float buildtime = 60;
	static final int maxspawn = 20;
	
	@Override
	public void draw(){
		Draw.rect("dronemaker", x, y, base.rotation);
	}

	@Override
	public void behaviorUpdate(){
		if(base.target == null || base.spawned > maxspawn) return;
		
		reload += Novi.delta();
		
		if(reload >= buildtime){
			//Drone drone = (Drone)new Drone().set(x, y);
			//drone.velocity.y = -3;
			//drone.add().send();
			//drone.base = base;
			reload = 0;
			base.spawned ++;
		}
	}
	
	@Override
	public boolean targetPlayer(){
		return false;
	}
}
