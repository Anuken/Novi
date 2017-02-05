package io.anuke.novi.entities.effects;

import com.esotericsoftware.kryonet.Server;

import io.anuke.novi.network.packets.EffectPacket;
import io.anuke.novi.server.NoviServer;

/**static utility class for making flashy effects
NOTE: do NOT use clientside - bad things will happen! (entity ID collisions, ghost entities)*/
public class Effects{
	public static void shake(float duration, float intensity, float x, float y){
		server().sendToAllTCP(new EffectPacket().shake(duration, intensity, x, y));
	}
	
	public static void effect(EffectType type, float x, float y){
		effect(type, x, y, 0);
	}
	
	public static void effect(EffectType type, float x, float y, float delay){
		new Effect(type).set(x, y).send();
	}

	private static Server server(){
		return NoviServer.instance().server;
	}
}
