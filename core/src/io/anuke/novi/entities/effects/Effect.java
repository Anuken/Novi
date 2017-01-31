package io.anuke.novi.entities.effects;

import io.anuke.novi.entities.Entity;



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
	
	public void onRemove(){
		
	}
	
	public final Entity add(){
		if(server != null) throw new RuntimeException("Effects should not be added serverside!");
		entities.put(this.getID(), this);
		return this;
	}
}
