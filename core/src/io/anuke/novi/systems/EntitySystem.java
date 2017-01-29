package io.anuke.novi.systems;

import io.anuke.novi.entities.Entity;

public abstract class EntitySystem{
	abstract public void update(Entity entity);
	public boolean accept(Entity entity){
		return true;
	}
	
}
