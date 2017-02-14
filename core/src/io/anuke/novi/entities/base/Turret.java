package io.anuke.novi.entities.base;

import io.anuke.novi.entities.DestructibleEntity;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;

public class Turret extends DestructibleEntity implements Syncable{

	@Override
	public SyncData writeSync(){
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void readSync(SyncData buffer){
		// TODO Auto-generated method stub
		
	}
	
	static class TurretSyncData extends SyncData{
		
	}
}
