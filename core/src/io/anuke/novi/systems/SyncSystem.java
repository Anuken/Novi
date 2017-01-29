package io.anuke.novi.systems;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.Player;
import io.anuke.novi.modules.Network;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.network.TimedSyncable;
import io.anuke.novi.network.Syncable.GlobalSyncable;
import io.anuke.novi.network.packets.WorldUpdatePacket;

public class SyncSystem extends EntitySystem{

	@Override
	public void update(Entity entity){
		if(Entity.server.updater.frameID() % Network.synctime != 0) return;
		Player player = (Player)entity;
		WorldUpdatePacket worldupdate = new WorldUpdatePacket();
		worldupdate.health = player.health;
		for(Entity other : Entity.entities.values()){
			if(other.equals(player) || !(other instanceof Syncable) || (other instanceof TimedSyncable && !((TimedSyncable)other).sync()) || (!other.getClass().isAnnotationPresent(GlobalSyncable.class) && !other.loaded(player.x, player.y))) continue;
			Syncable sync = (Syncable)other;
			worldupdate.updates.put(other.GetID(), sync.writeSync());
		}
		Entity.server.server.sendToUDP(player.connectionID(), worldupdate);
	}
	
	public boolean accept(Entity entity){
		return entity instanceof Player;
	}

}
