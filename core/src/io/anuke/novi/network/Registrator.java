package io.anuke.novi.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongArray;
import com.esotericsoftware.kryo.Kryo;

import io.anuke.novi.effects.BreakEffect;
import io.anuke.novi.effects.Effect;
import io.anuke.novi.effects.EffectType;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.FlyingEntity;
import io.anuke.novi.entities.base.*;
import io.anuke.novi.entities.base.Base.BlockUpdate;
import io.anuke.novi.entities.basic.Bullet;
import io.anuke.novi.entities.basic.DamageArea;
import io.anuke.novi.entities.enemies.Drone;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.entities.player.Player.ShipState;
import io.anuke.novi.entities.player.RepairBase;
import io.anuke.novi.items.Item;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.items.ShipType;
import io.anuke.novi.network.packets.*;
import io.anuke.novi.network.packets.EffectPacket.ScreenEffectType;
import io.anuke.novi.ui.MarkerType;
import io.anuke.novi.ui.Marker;
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
		k.register(ClassSwitchPacket.class);
		k.register(MapRequestPacket.class);
		k.register(MapPacket.class);
		
		k.register(ShipState.class);
		
		k.register(SyncData.class);
		k.register(ShipType.class);
		k.register(Item.class);
		k.register(ProjectileType.class);
		k.register(InputType.class);
		k.register(Marker.class);
		k.register(MarkerType.class);
		
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
		k.register(BaseTurret.class);
		k.register(BaseFactory.class);
		k.register(RepairBase.class);
		
		
		k.register(Object[].class);
		k.register(Array.class);
		k.register(LongArray.class);
		k.register(long[].class);
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
