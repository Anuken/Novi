package io.anuke.novi.entities.base;

import io.anuke.novi.entities.DestructibleEntity;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;

public class Turret extends DestructibleEntity implements Syncable{
	public int health;
	
	@Override
	public SyncData writeSync(){
		return new SyncData(getID(), health);
	}

	@Override
	public void readSync(SyncData in){
		health = in.get(0);
	}
}
