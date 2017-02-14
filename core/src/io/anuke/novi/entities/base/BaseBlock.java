package io.anuke.novi.entities.base;

import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.enemies.Enemy;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.world.Material;

public abstract class BaseBlock extends Enemy implements Syncable{
	public transient float reload;
	public transient Base base;
	public long baseid;
	public int blockx, blocky;
	public float rotation;
	
	public BaseBlock(){
		material.set(Material.blocksize);
	}
	
	public void setBase(Base base, int x, int y){
		blockx = x;
		blocky = y;
		this.base = base;
		this.baseid = base.getID();
	}
	
	@Override
	public void onDeath(){
		super.onDeath();
		base.onBlockDestroyed(this);
	}
	
	@Override
	public boolean sync(){
		return false;
	}
	
	@Override
	public void onRecieve(){
		base = (Base)Entities.get(baseid);
	}
}
