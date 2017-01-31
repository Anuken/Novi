package io.anuke.novi.entities;

import io.anuke.novi.entities.combat.Damager;

//an entity that can be hit and/or destroyed by bullets
public abstract class DestructibleEntity extends FlyingEntity{
	public int health = 100;
	
	public void collisionEvent(SolidEntity other){
		if(!(other instanceof Damager)) return;
		Damager damager = (Damager)other;
		health -= damager.damage();
		onHit(other);
		
		if(health <= 0){
			onDeath();
			if(this.removeOnDeath()) removeServer();
		}
	}
	
	//when the entity dies
	public void onDeath(){
		
	}
	
	//when the entity is hit
	public void onHit(SolidEntity entity){
		
	}
	
	//whether to remove the entity when it dies
	public boolean removeOnDeath(){
		return true;
	}
}
