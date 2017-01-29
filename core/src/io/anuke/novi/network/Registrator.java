package io.anuke.novi.network;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;

import io.anuke.novi.entities.*;
import io.anuke.novi.entities.effects.*;
import io.anuke.novi.entities.enemies.Drone;
import io.anuke.novi.items.*;
import io.anuke.novi.network.packets.*;
import io.anuke.novi.network.packets.EffectPacket.EffectType;
import io.anuke.novi.utils.InputType;
import io.anuke.novi.world.*;

public class Registrator{
	public static void register(Kryo k){
		k.register(ConnectPacket.class);
		k.register(DataPacket.class);
		k.register(PositionPacket.class);
		k.register(WorldUpdatePacket.class);
		k.register(EntityRemovePacket.class);
		k.register(EffectPacket.class);
		k.register(InputPacket.class);
		k.register(DeathPacket.class);
		k.register(SyncData.class);
		k.register(BaseSyncData.class);
		k.register(PlayerSyncData.class);
		k.register(EnemySyncData.class);
		k.register(Ship.class);
		k.register(Item.class);
		k.register(ProjectileType.class);
		k.register(InputType.class);
		k.register(Entity.class);
		k.register(FlyingEntity.class);
		k.register(Base.class);
		k.register(Bullet.class);
		k.register(ExplosionEffect.class);
		k.register(ExplosionEmitter.class);
		k.register(DamageArea.class);
		k.register(Shockwave.class);
		k.register(BreakEffect.class);
		k.register(Target.class);
		k.register(Drone.class);
		k.register(Player.class);
		k.register(Material.class);
		k.register(EffectType.class);
		k.register(BlockUpdate.class);
		k.register(BlockFrameUpdate.class);
		k.register(Block.class);
		k.register(Block[].class);
		k.register(Block[][].class);
		k.register(Vector2.class);
		k.register(HashMap.class);
		k.register(ConcurrentHashMap.class);
		k.register(ArrayList.class);
		k.register(Long.class);
		k.register(float[].class);
		k.register(boolean[].class);
		k.register(boolean[][].class);
	}
}
