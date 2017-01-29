package io.anuke.novi.network;

import java.util.ArrayList;

import io.anuke.novi.world.BlockUpdate;

public class BaseSyncBuffer extends SyncBuffer{
	public ArrayList<BlockUpdate> updates;
	public float rotation;
	
	public BaseSyncBuffer(){
		
	}
	
	public BaseSyncBuffer(ArrayList<BlockUpdate> updates, float rotation, float x, float y){
		this.updates = updates;
		this.rotation = rotation;
		this.x = x;
		this.y = y;
	}
}
