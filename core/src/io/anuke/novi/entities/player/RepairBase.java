package io.anuke.novi.entities.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.entities.*;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.utils.Draw;
import io.anuke.novi.utils.Physics;
import io.anuke.novi.utils.Timers;

public class RepairBase extends DestructibleEntity implements Syncable{
	public static float range = 200;
	
	public RepairBase(){
		this.material.set(100);
	}
	
	@Override
	public void draw(){
		Draw.rect("playerbase", x, y);
		
		for(Entity entity : Entities.list()){
			if(Vector2.dst(entity.x, entity.y, x, y) < range){
				Draw.colorl(0.8f + MathUtils.random(0.1f) + Math.abs(MathUtils.sin(Timers.time()/3f)/5f));
				Draw.laser("healbeam", "healbeamend", x, y, entity.x, entity.y);
				Draw.color();
			}
		}
	}
	
	@Override
	public void serverUpdate(){
		//if(Timers.get(this, 10))
		Physics.rectCast(x, y, range, e->{
			if(!(e instanceof Player)) return;
			Player p = (Player)e;
			if(Vector2.dst(p.x, p.y, x, y) < range){
				p.heal(0.3f);
			}
		});
	}
	
	//never collide with anything
	@Override
	public boolean collides(SolidEntity other){
		return false;
	}

	@Override
	public SyncData writeSync(){
		return new SyncData(getID());
	}

	@Override
	public void readSync(SyncData in){
		
	}
}
