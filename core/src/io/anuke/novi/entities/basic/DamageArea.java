package io.anuke.novi.entities.basic;

import java.util.HashSet;

import io.anuke.novi.entities.SolidEntity;


//TODO remove this class and use the spatial system for AoE
public class DamageArea extends SolidEntity implements Damager{
	transient HashSet<Long> collided = new HashSet<Long>();
	transient float lifetime = 100f, life;
	transient int damage = 20;
	
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
		if(life > lifetime)remove();
	}

	@Override
	public void draw(){
		
	}
	
	public boolean collides(SolidEntity other){
		return !(other instanceof Damager) && !collided.contains(other.getID()) && super.collides(other);
	}
	
	@Override
	public void collisionEvent(SolidEntity entity){
		collided.add(entity.getID());
	}

	@Override
	public int damage(){
		return damage;
	}
}
