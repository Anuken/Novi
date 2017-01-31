package io.anuke.novi.systems;

import java.util.HashSet;

import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.SolidEntity;

public class CollisionSystem extends IteratingSystem{
	HashSet<Long> collided = new HashSet<Long>(); //used for storing collisions each frame so entities don't collide twice
	SpatialSystem spatial;
	
	@Override
	public void update(Entity aentity){
		if(spatial == null) spatial = Entities.getSystem(SpatialSystem.class);
		
		SolidEntity entity = (SolidEntity)aentity;
		spatial.getNearby(entity.x, entity.y, entity.material.getRectangle().width*1.2f, (other)->{
			
			if(other.equals(entity) || !(other instanceof SolidEntity) 
					|| !entity.inRange((SolidEntity)other, 10 + entity.material.getRectangle().width)) return;
			if( !collided.contains(other.getID())){
				SolidEntity othersolid = (SolidEntity)other;
				if(othersolid.collides(entity) && entity.collides(othersolid)){
					collisionEvent(entity, othersolid);
					collided.add(entity.getID());
				}
			}
		});
	}
	
	private void collisionEvent(SolidEntity entitya, SolidEntity entityb){
		entitya.collisionEvent(entityb);
		entityb.collisionEvent(entitya);
	}

	public boolean accept(Entity entity){
		return entity instanceof SolidEntity;
	}
}
