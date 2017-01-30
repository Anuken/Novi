package io.anuke.novi.systems;

import java.util.Collection;

import io.anuke.novi.entities.Entity;

public abstract class IteratingSystem extends EntitySystem{
	
	@Override
	public void update(Collection<Entity> entities){
		for(Entity entity : entities){
			if(accept(entity)) update(entity);
		}
	}
	
	abstract public void update(Entity entity);
	
	public boolean accept(Entity entity){
		return true;
	}
	
}
