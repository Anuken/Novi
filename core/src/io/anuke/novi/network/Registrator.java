package io.anuke.novi.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;

import io.anuke.novi.effects.BreakEffect;
import io.anuke.novi.effects.Effect;
import io.anuke.novi.effects.EffectType;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.FlyingEntity;
import io.anuke.novi.entities.base.Player;
import io.anuke.novi.entities.base.Player.ShipState;
import io.anuke.novi.entities.combat.Bullet;
import io.anuke.novi.entities.combat.DamageArea;
import io.anuke.novi.entities.enemies.*;
import io.anuke.novi.entities.enemies.Base.BlockUpdate;
import io.anuke.novi.items.Item;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.items.ShipType;
import io.anuke.novi.network.packets.*;
import io.anuke.novi.network.packets.EffectPacket.ScreenEffectType;
import io.anuke.novi.utils.InputType;
import io.anuke.novi.world.Block;
import io.anuke.novi.world.Material;

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
		k.register(EntityRequestPacket.class);
		
		k.register(ShipState.class);
		
		k.register(SyncData.class);
		k.register(BaseSyncData.class);
		k.register(PlayerSyncData.class);
		k.register(EnemySyncData.class);
		k.register(ShipType.class);
		k.register(Item.class);
		k.register(ProjectileType.class);
		k.register(InputType.class);
		
		k.register(Effect.class);
		k.register(Entity.class);
		k.register(FlyingEntity.class);
		k.register(Base.class);
		k.register(Bullet.class);
		k.register(DamageArea.class);
		k.register(BreakEffect.class);
		k.register(Drone.class);
		k.register(Player.class);
		k.register(ShipBase.class);
		k.register(GunBase.class);
		
		k.register(Material.class);
		k.register(ScreenEffectType.class);
		k.register(EffectType.class);
		k.register(BlockUpdate.class);
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
