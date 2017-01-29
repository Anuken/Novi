package io.anuke.novi.network.packets;

import java.util.concurrent.ConcurrentHashMap;

import io.anuke.novi.entities.Entity;

public class DataPacket{
	public long playerid;
	public ConcurrentHashMap<Long, Entity> entities;
}
