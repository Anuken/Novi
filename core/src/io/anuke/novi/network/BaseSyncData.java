package io.anuke.novi.network;

import java.util.ArrayList;

import io.anuke.novi.entities.enemies.Base.BlockUpdate;


public class BaseSyncData extends SyncData{
	public ArrayList<BlockUpdate> updates;
	public float rotation;
	
	public BaseSyncData(){
		
	}
	
	public BaseSyncData(ArrayList<BlockUpdate> updates, float rotation, float x, float y){
		this.updates = updates;
		this.rotation = rotation;
		this.x = x;
		this.y = y;
	}
}
