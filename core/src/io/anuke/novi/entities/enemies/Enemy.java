package io.anuke.novi.entities.enemies;


import static io.anuke.novi.modules.World.*;

import io.anuke.novi.effects.EffectType;
import io.anuke.novi.effects.Effects;
import io.anuke.novi.entities.DestructibleEntity;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.entities.basic.Bullet;
import io.anuke.novi.entities.basic.Player;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.utils.InterpolationData;

public abstract class Enemy extends DestructibleEntity implements Syncable{
	private static final float targettime = 40;
	private transient float targetcount = 0; //retarget timer
	public transient Player target;
	public transient int targetrange = 500;
	transient InterpolationData data = new InterpolationData();
	
	private transient float neardist;

	public void targetPlayers(int range){
		neardist = Float.MAX_VALUE;
		
		if(target != null && target.isRemoved()){
			target = null;
		}
		
		Entities.spatial().getNearby(x, y, targetrange, (entity)->{
			if(entity instanceof Player && ((Player)entity).isVisible()){
				float dist = wrappedDist(x, y, entity.x, entity.y);
				if(dist < neardist && dist < targetrange){
					neardist = dist;
					target = (Player)entity;
				}
			}
		});
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
		return !(other instanceof Enemy) && super.collides(other) && !((other instanceof Bullet && ((Bullet)other).shooter() instanceof Enemy));
	}

	public void onDeath(){
		//TODO enemy death explosion
		Effects.effect(EffectType.smoke, x, y);
		Effects.effect(EffectType.explosion, x, y);
		Effects.shake(8f, 8f, x, y);
	}

	public void shoot(ProjectileType type, float angle){
		Bullet bullet = new Bullet(type, angle);
		bullet.set(x, y);
		bullet.setShooter(this);
		bullet.add().send();
	}

	public Bullet getShoot(ProjectileType type, float angle){
		Bullet bullet = new Bullet(type, angle);
		bullet.set(x, y);
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
		float dist = wrappedDist(target.x, target.y, x, y);
		return predictTargetAngle(x, y, dist / speed);
	}

	public void update(){
		updateVelocity();
		if(!NoviServer.active()) data.update(this);
	}

	@Override
	public void serverUpdate(){
		tryRetarget();
		behaviorUpdate();
	}

	@Override
	public SyncData writeSync(){
		return new SyncData(getID(), x, y, velocity.x, velocity.y);
	}

	@Override
	public void readSync(SyncData in){
		velocity.set(in.get(2), in.get(3));
		data.push(this, in.get(0), in.get(1), 0);
	}

	abstract public void behaviorUpdate();
}
