package io.anuke.novi.tween;

import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.entities.FlyingEntity;

public class MoveToAction implements Action{
	FlyingEntity entity;
	float speed, x, y, dst;
	
	public MoveToAction(FlyingEntity e, float x, float y, float speed){
		entity = e;
		this.speed = speed;
		this.x = x;
		this.y = y;
		this.dst = Vector2.dst(e.x, e.y, x, y);
	}
	
	public boolean update(){
		if(Vector2.dst(x, y, entity.x, entity.y) < 3.5f){
			entity.velocity.set(0, 0.01f);
			return true;
		}else{
			entity.velocity.set(x - entity.x, y - entity.y).setLength(speed*(Vector2.dst(entity.x, entity.y, x, y)/dst));
		}
		return false;
	}

	@Override
	public Object getTarget(){
		return entity;
	}
}
