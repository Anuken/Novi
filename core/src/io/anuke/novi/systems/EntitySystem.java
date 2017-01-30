package io.anuke.novi.systems;

import java.util.Collection;

import io.anuke.novi.entities.Entity;

public abstract class EntitySystem{
	
	public abstract void update(Collection<Entity> entities);
}
