package io.anuke.novi.entities;

import com.badlogic.gdx.math.Vector2;

public abstract class FlyingEntity extends SolidEntity{
	public Vector2 velocity = new Vector2();
	
	//updates velocity and position
	protected void updateVelocity(){
		x += velocity.x*delta();
		y += velocity.y*delta();
		velocity.scl((float)Math.pow(1f - material.drag, delta()));
		if(material.maxvelocity > 0) velocity.limit(material.maxvelocity);
	}
	
	public FlyingEntity translate(float x, float y){
		Vector2 v = new Vector2(x,y).rotate(velocity.angle() - 90);
		this.x += v.x;
		this.y += v.y;
		return this;
	}
	
	 public FlyingEntity setPosition(float x, float y){
		 super.setPosition(x, y);
		 return this;
	 }
}
