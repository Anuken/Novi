package io.anuke.novi.network.packets;

import java.util.HashMap;

import io.anuke.novi.network.SyncData;

public class WorldUpdatePacket{
	public float health;
	public HashMap<Long, SyncData> updates = new HashMap<Long, SyncData>();
}
