package io.anuke.novi.entities.effects;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryonet.Server;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.network.packets.EffectPacket;

//static utility class for making flashy effects
//NOTE: do NOT use clientside - bad things will happen!
public class Effects{
	public static void shake(float duration, float intensity, float x, float y){
		server().sendToAllTCP(new EffectPacket().shake(duration, intensity, x, y));
	}

	public static void explosion(float x, float y){
		new ExplosionEffect().setPosition(x, y).sendSelf();
	}

	public static void explosionCluster(float x, float y, int amount, float radius){
		for(int i = 0;i < amount;i ++)
			explosion(x + MathUtils.random( -radius, radius), y + MathUtils.random( -radius, radius));
	}

	private static Server server(){
		return Entity.server.server;
	}
}
