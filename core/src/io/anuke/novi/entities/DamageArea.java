package io.anuke.novi.entities;

import java.util.HashSet;

public class DamageArea extends SolidEntity implements Damager{
	transient HashSet<Long> collided = new HashSet<Long>();
	float lifetime =  100f, life;
	int damage = 20;
	
	public DamageArea(){
		
	}
	
	public DamageArea setDamage(int damage){
		this.damage = damage;
		return this;
	}
	
	public DamageArea(float lifetime, float size){
		setSize(size);
		this.lifetime = lifetime;
	}
	
	public DamageArea setSize(float width, float height){
		material.getRectangle().setSize(width, height);
		return this;
	}
	
	public DamageArea setSize(float size){
		return setSize(size,size);
	}
	
	@Override
	public void update(){
		life += delta();
		if(life > lifetime)RemoveSelf();
	}

	@Override
	public void Draw(){
		
	}
	
	public boolean collides(SolidEntity other){
		return !(other instanceof Damager) && !collided.contains(other.GetID()) && super.collides(other);
	}
	
	@Override
	public void collisionEvent(SolidEntity entity){
		collided.add(entity.GetID());
	}

	@Override
	public int damage(){
		return damage;
	}
}
