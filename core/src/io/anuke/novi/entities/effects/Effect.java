package io.anuke.novi.entities.effects;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.server.NoviServer;

public abstract class Effect extends Entity{
	float life;
	float lifetime = 100;
	
	@Override
	public final void update(){
		life += delta();
		if(life > lifetime){
			onRemove();
			remove();
		}
	}
	
	@Override
	public float getLayer(){
		return 1f;
	}
	
	public void onRemove(){
		
	}
	
	public final Entity add(){
		if(NoviServer.active()) throw new RuntimeException("Effects should not be added serverside!");
		return super.add();
	}
}
