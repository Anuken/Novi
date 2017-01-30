package io.anuke.novi.entities.enemies;


import static io.anuke.novi.utils.WorldUtils.*;

import com.badlogic.gdx.math.MathUtils;

import io.anuke.novi.entities.*;
import io.anuke.novi.entities.effects.ExplosionEffect;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.network.EnemySyncData;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.utils.InterpolationData;
import io.anuke.novi.utils.WorldUtils;

public abstract class Enemy extends DestructibleEntity implements Syncable{
	private static final float targettime = 40;
	private float targetcount = 0;
	public Player target;
	public int targetrange = 500;
	transient InterpolationData data = new InterpolationData();

	public void targetPlayers(int range){
		Player nearest = null;
		float neardist = Float.MAX_VALUE;
		for(Entity entity : entities.values()){
			if(entity instanceof Player && ((Player)entity).isVisible()){
				float dist = WorldUtils.wrappedDist(x, y, entity.x, entity.y);
				if(dist < neardist){
					neardist = dist;
					nearest = (Player)entity;
				}
			}
		}
		if(neardist > targetrange) nearest = null;
		target = nearest;
	}

	public void tryRetarget(){
		if(targetcount > 0){
			targetcount -= delta();
		}else{
			targetPlayers(targetrange);
			targetcount = targettime;
		}
	}

	public boolean collides(SolidEntity other){
		return !(other instanceof Enemy) && super.collides(other) && !((other instanceof Bullet && ((Bullet)other).shooter instanceof Enemy));
	}

	public void deathEvent(){
		int radius = 20;
		for(int i = 0;i < 10;i ++){
			new ExplosionEffect().setPosition(x + MathUtils.random( -radius, radius), y + MathUtils.random( -radius, radius)).sendSelf();
		}
	}

	public void shoot(ProjectileType type, float angle){
		Bullet bullet = new Bullet(type, angle);
		bullet.setPosition(x, y);
		bullet.setShooter(this);
		bullet.addSelf().sendSelf();
	}

	public Bullet getShoot(ProjectileType type, float angle){
		Bullet bullet = new Bullet(type, angle);
		bullet.setPosition(x, y);
		bullet.setShooter(this);
		return bullet;
	}

	public float targetAngle(float x, float y){
		return predictTargetAngle(x, y, 0f);
	}

	public float targetAngle(){
		return predictTargetAngle(x, y, 0f);
	}

	public float predictTargetAngle(float x, float y, float amount){
		if(target == null) return 0f;
		bound(1f);
		wrap(1f);
		vector.set(relative3((x), (bound(target.x + target.velocity.x * amount))),relative3((y), (bound(target.y + target.velocity.y * amount))));
		return vector.angle();
	}

	public float autoPredictTargetAngle(float x, float y, float speed){
		if(target == null) return 0f;
		float dist = WorldUtils.wrappedDist(target.x, target.y, x, y);
		return predictTargetAngle(x, y, dist / speed);
	}

	public void update(){
		updateVelocity();
		if(!NoviServer.active)data.update(this);
	}

	@Override
	public void serverUpdate(){
		tryRetarget();
		behaviorUpdate();
	}

	@Override
	public SyncData writeSync(){
		return new EnemySyncData(GetID(), x, y, velocity);
	}

	@Override
	public void readSync(SyncData buffer){
		EnemySyncData sync = (EnemySyncData)buffer;
		velocity = sync.velocity;
		data.push(this, sync.x, sync.y, 0);
	}

	abstract public void behaviorUpdate();
}
