package io.anuke.novi.systems;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.base.Player;
import io.anuke.novi.modules.Network;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.network.packets.WorldUpdatePacket;
import io.anuke.novi.server.NoviServer;

public class SyncSystem extends IteratingSystem{

	@Override
	public void update(Entity entity){
		if(Novi.frame() % Network.synctime != 0) return;
		Player player = (Player)entity;
		WorldUpdatePacket worldupdate = new WorldUpdatePacket();
		worldupdate.health = player.health;
		
		Entities.spatial().getNearby(player.x, player.y, Entities.loadRange, (other)->{
			if(other.equals(player) 
					|| !(other instanceof Syncable && !((Syncable)other).sync()) ) return;
			Syncable sync = (Syncable)other;
			worldupdate.updates.put(other.getID(), sync.writeSync());
		});
		
		NoviServer.instance().server.sendToUDP(player.connectionID(), worldupdate);
	}
	
	public boolean accept(Entity entity){
		return entity instanceof Player;
	}

}
