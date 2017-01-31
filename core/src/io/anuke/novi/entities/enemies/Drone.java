package io.anuke.novi.entities.enemies;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.entities.effects.BreakEffect;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.utils.WorldUtils;

public class Drone extends Enemy{
	float speed = 0.1f;
	float turnrange = 80;
	float offset;
	float sign = 0;
	public Base base;
	
	{
		material.maxvelocity = 2f;
		material.getRectangle().setSize(10);
		health = 10;
		offset = MathUtils.random(-30,30);
		sign = MathUtils.randomSign();
		speed += MathUtils.random(0.3f);
	}
	
	@Override
	public void draw(){
		renderer.layer("drone", x, y).setRotation(velocity.angle() - 90).addShadow();
	}
	
	public void onDeath(){
		super.onDeath();
		if(base != null) base.spawned --;
		new BreakEffect("drone", velocity, 0.2f).set(x, y).send();
	}

	@Override
	public void behaviorUpdate(){
		if(target == null) return;
		Vector2 add = new Vector2(WorldUtils.relative3(target.x, x), WorldUtils.relative3(target.y, y));
		float len = add.len();
		float anglechange =  sign*(turnrange - len)*(90f/turnrange);
		if(len < turnrange) add.setAngle((add.angle() + anglechange));
		add.setAngle(add.angle() + offset);
		velocity.add(add.setLength(speed * delta()));
		
		if(MathUtils.randomBoolean(0.03f)){
			shoot(ProjectileType.redbullet, targetAngle()-180);
		}
	}

}
