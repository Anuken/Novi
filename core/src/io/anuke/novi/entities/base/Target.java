package io.anuke.novi.entities.base;

import com.badlogic.gdx.math.MathUtils;

import io.anuke.novi.entities.DestructibleEntity;
import io.anuke.novi.entities.combat.Bullet;
import io.anuke.novi.entities.effects.ExplosionEffect;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.utils.Draw;
import io.anuke.novi.utils.InterpolationData;

public class Target extends DestructibleEntity implements Syncable{
	transient InterpolationData data = new InterpolationData();
	{
		x = 80;
		y = x;
	}

	public Target(){
		material.getRectangle().setSize(20);
	}

	@Override
	public void update(){
		data.update(this);
	}

	@Override
	public void serverUpdate(){
		x += Math.sin(y / 10f + 2) * delta();
		y += Math.sin(x / 10f + 2) * delta();
		updateBounds();
		Bullet b = new Bullet(MathUtils.random(0,360f));
		b.x = x;
		b.y = y;
		b.setShooter(this);
		b.add().send();
	}

	public void onDeath(){
		int radius = 30;
		for(int i = 0;i < 30;i ++){
			new ExplosionEffect().set(x + MathUtils.random( -radius, radius), y + MathUtils.random( -radius, radius)).send();
		}
		new Target().set(x + MathUtils.random( 0, radius*2), y + MathUtils.random(0, radius*2)).add().send();
	}

	@Override
	public void draw(){
		Draw.rect("tile", x, y);
	}

	@Override
	public SyncData writeSync(){
		return new SyncData(getID(), x, y);
	}

	@Override
	public void readSync(SyncData buffer){
		data.push(this, buffer.x, buffer.y, 0);
	}

}
