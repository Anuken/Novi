package io.anuke.novi.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Server;

import io.anuke.novi.network.packets.EffectPacket;
import io.anuke.novi.server.NoviServer;

/**static utility class for making effects*
 * can be used clientside now*/
public class Effects{
	public static void shake(float duration, float intensity, float x, float y){
		server().sendToAllTCP(new EffectPacket().shake(duration, intensity, x, y));
	}
	
	public static void effect(EffectType type, float x, float y){
		effect(type, x, y, 0);
	}
	
	public static void effect(EffectType type, float x, float y, Color color){
		new Effect(type).color(color).set(x, y).send();
	}
	
	public static void smoke(float x, float y){
		effect(EffectType.smoke, x, y, 0);
	}
	
	public static void effect(EffectType type, float x, float y, float delay){
		new Effect(type).delay(delay).set(x, y).send();
	}
	
	public static void blockbreak(String name, float x, float y, float offset, Vector2 velocity){
		new BreakEffect(name, velocity, offset).set(x, y).send();
	}
	
	public static void blockbreak(String name, float x, float y){
		new BreakEffect(name, 0, 0).set(x, y).send();
	}

	private static Server server(){
		return NoviServer.instance().server;
	}
}
