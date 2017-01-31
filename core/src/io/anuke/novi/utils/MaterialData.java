package io.anuke.novi.utils;

import com.badlogic.gdx.math.Rectangle;

import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.modules.World;

//stores mass, hitbox size, calculating collisions..
public class MaterialData{
	public float mass = 1f;
	public float drag = 0.08f;
	public float maxvelocity = -1;
	private SolidEntity entity;
	private Rectangle rectangle;
	
	public boolean collides(MaterialData other){
		return rectangle.overlaps(other.rectangle);
	}
	
	public boolean doubleCheckCollision(MaterialData other){
		return collides(other.updateHitbox()) || collides(other.updateHitboxWrap());
	}
	
	public Rectangle getRectangle(){
		return rectangle;
	}
	
	public MaterialData(SolidEntity entity, int hitwidth, int hitheight){
		this.entity = entity;
		rectangle = new Rectangle(0, 0, hitwidth, hitheight);
	}
	
	public MaterialData updateHitbox(){
		rectangle.setCenter(entity.predictedX(), entity.predictedY());
		return this;
	}
	
	public MaterialData updateHitboxWrap(){
		rectangle.setCenter(World.wrap(entity.predictedX()), World.wrap(entity.predictedY()));
		return this;
	}
}
