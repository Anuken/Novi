package io.anuke.novi.network;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.server.NoviServer;

public interface Syncable{
	public SyncData writeSync();
	public void readSync(SyncData in);
	
	public default void handleEvent(Object[] data){
		if(data[0] instanceof Integer){
			this.handleEvent((int)data[0]);
		}
	}
	
	public default void handleEvent(int data){
		
	}
	
	public default boolean sync(){return true;}
	
	public default void sendEvent(Entity entity, int id){
		sendEvent(entity, id);
	}
	
	public default void sendEvent(Entity entity, Object... data){
		NoviServer.instance().sendNear(new SyncData(entity.getID(), data), entity.x, entity.y);
	}
}
