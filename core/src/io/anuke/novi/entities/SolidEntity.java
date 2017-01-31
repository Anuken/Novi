package io.anuke.novi.entities;

import com.badlogic.gdx.math.Rectangle;

import io.anuke.novi.entities.combat.Bullet;
import io.anuke.novi.utils.*;

//a solid entity is an entity that collides with things
//this class does not have velocity; see FlyingEntity
public abstract class SolidEntity extends Entity{
	transient public MaterialData material = new MaterialData(this, 6,6);
	
	//returns whether this entity collides with the other solid entity
	//yes I know it's a mess
	public boolean collides(SolidEntity other){
		if(other instanceof Bullet && this.equals(((Bullet)other).shooter)) return false;
		material.updateHitbox();
		if(material.doubleCheckCollision(other.material)) return true;
		material.updateHitboxWrap();
		if(material.doubleCheckCollision(other.material)) return true;
		return false;
	}
	
	public boolean collides(Rectangle rekt){
		material.updateHitbox();
		return material.getRectangle().overlaps(rekt);
	}
	
	//returns the predicted X position. it's the normal X by default, because prediction is disabled
	public float predictedX(){
		return x;
	}
	
	//^^^^
	public float predictedY(){
		return y;
	}
	
	//called when this entity hits another one - overriding is optional
	public void collisionEvent(SolidEntity other){
		
	}
	
	public boolean inRange(SolidEntity entity, float rad){
		return WorldUtils.loopDist(entity.predictedX(), predictedX(), entity.predictedY(), predictedY(), rad);
	}
}
