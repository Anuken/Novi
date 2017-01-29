package io.anuke.novi.entities;

//an entity that can be hit and/or destroyed by bullets
public abstract class DestructibleEntity extends FlyingEntity{
	public int health = 100;
	
	public void collisionEvent(SolidEntity other){
		if(!(other instanceof Damager)) return;
		Damager damager = (Damager)other;
		health -= damager.damage();
		hitEvent(other);
		if(health <= 0){
			deathEvent();
			if(this.removeOnDeath())server.removeEntity(this);
		}
	}
	
	//when the entity dies
	public void deathEvent(){
		
	}
	
	//when the entity is hit
	public void hitEvent(SolidEntity entity){
		
	}
	
	//whether to remove the entity when it dies
	public boolean removeOnDeath(){
		return true;
	}
}
