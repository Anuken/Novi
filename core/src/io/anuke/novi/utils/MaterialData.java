package io.anuke.novi.utils;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.modules.World;

//stores mass, hitbox size, calculating collisions..
public class MaterialData{
	public float mass = 1f;
	public float drag = 0.08f;
	public float maxvelocity = -1;
	public boolean collide = true;
	private SolidEntity entity;
	public Rectangle rect;
	
	public boolean intersects(float x1, float y1, float x2, float y2){
		return Intersector.intersectLines(x1, y1, x2, y2, rect.x, rect.y, rect.x + rect.width, rect.y, null);
	}
	
	public boolean collides(MaterialData other){
		return collide && rect.overlaps(other.rect);
	}
	
	public boolean doubleCheckCollision(MaterialData other){
		return collides(other.updateHitbox()) || collides(other.updateHitboxWrap());
	}
	
	public void set(float size){
		rect.setSize(size);
	}
	
	public void init(float size, float drag){
		rect.setSize(size);
		this.drag = drag;
	}
	
	public Rectangle getRectangle(){
		return rect;
	}
	
	public MaterialData(SolidEntity entity, int hitwidth, int hitheight){
		this.entity = entity;
		rect = new Rectangle(0, 0, hitwidth, hitheight);
	}
	
	public MaterialData updateHitbox(){
		rect.setCenter(entity.predictedX(), entity.predictedY());
		return this;
	}
	
	public MaterialData updateHitboxWrap(){
		rect.setCenter(World.wrap(entity.predictedX()), World.wrap(entity.predictedY()));
		return this;
	}
}
