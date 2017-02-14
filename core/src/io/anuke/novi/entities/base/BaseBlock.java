package io.anuke.novi.entities.base;

import io.anuke.novi.entities.enemies.Enemy;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.world.Material;

public abstract class BaseBlock extends Enemy implements Syncable{
	protected Material material;
	public transient float reload;
	public final transient Base base;
	public transient int blockx, blocky;
	public float rotation;
	
	public BaseBlock(Base base, Material material, int blockx, int blocky){
		this.material = material;
		this.health = material.health();
		this.material = material;
		this.blockx = blockx;
		this.blocky = blocky;
		this.base = base;
	}
	
	private BaseBlock(){
		this.base = null;
	}
	
	@Override
	public boolean sync(){
		return false;
	}
}
