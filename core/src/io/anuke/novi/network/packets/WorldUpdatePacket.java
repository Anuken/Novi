package io.anuke.novi.network.packets;

import java.util.HashMap;

import io.anuke.novi.network.SyncBuffer;

public class WorldUpdatePacket{
	public int health;
	public HashMap<Long, SyncBuffer> updates = new HashMap<Long, SyncBuffer>();
}
