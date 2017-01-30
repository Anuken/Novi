package io.anuke.novi.entities;

import io.anuke.novi.entities.effects.ExplosionEffect;
import io.anuke.novi.entities.enemies.Base;
import io.anuke.novi.entities.enemies.Enemy;
import io.anuke.novi.items.ProjectileType;

public class Bullet extends FlyingEntity implements Damager{
	private float life;
	public Entity shooter;
	public ProjectileType type = ProjectileType.yellowbullet;

	{
		material.drag = 0;
		material.getRectangle().setSize(2);
		type.setup(this);
	}

	private Bullet(){
		
	}

	public Bullet(float rotation){
		if(server != null) initVelocity(rotation);
	}

	public Bullet(ProjectileType type, float rotation){
		this.type = type;
		if(server != null) initVelocity(rotation);
	}

	@Override
	public void update(){
		life += delta();
		if(life >= type.getLifetime()){
			removeSelf();
			if(server != null) type.destroyEvent(this);
		}
		updateVelocity();
	}

	@Override
	public void draw(){
		type.draw(this, renderer);
	}

	public Bullet setShooter(Entity entity){
		shooter = entity;
		return this;
	}

	//sets velocity to speed of projectile type
	private void initVelocity(float rotation){
		velocity.x = 1f;
		velocity.setLength(type.getSpeed());
		velocity.setAngle(rotation);
	}

	//don't want to hit players or other bullets
	public boolean collides(SolidEntity other){
		return type.collide() && super.collides(other) && !((other instanceof Base && !type.collideWithBases()) ||(other instanceof Player && shooter instanceof Player) || (other instanceof Bullet && (!type.collideWithOtherProjectiles() && !((Bullet)other).type.collideWithOtherProjectiles())) || other.equals(shooter) || (shooter instanceof Enemy && other instanceof Enemy));
	}

	@Override
	public void collisionEvent(SolidEntity other){
		//spawn explosion and dissapear
		new ExplosionEffect().setPosition(x, y).sendSelf();
		server.removeEntity(this);
		if(server != null) type.destroyEvent(this);
	}
	
	public float life(){
		return life;
	}

	@Override
	public int damage(){
		return type.damage();
	}

}
