package io.anuke.novi.network;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.server.NoviServer;

public interface Syncable{
	public SyncData writeSync();
	public void readSync(SyncData buffer);
	public default void handleEvent(Object[] data){}
	public default boolean sync(){return true;};
	public default void onFinishSync(){};
	
	public default void sendEvent(Entity entity, Object... data){
		NoviServer.instance().sendNear(new EntityEvent(entity.getID(), data), entity.x, entity.y);
	}
}
