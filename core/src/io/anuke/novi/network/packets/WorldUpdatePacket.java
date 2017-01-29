package io.anuke.novi.network.packets;

import java.util.HashMap;

import io.anuke.novi.network.SyncData;

public class WorldUpdatePacket{
	public int health;
	public HashMap<Long, SyncData> updates = new HashMap<Long, SyncData>();
}
