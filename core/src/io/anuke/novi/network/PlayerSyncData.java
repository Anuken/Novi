package io.anuke.novi.network;

import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.entities.basic.Player.ShipState;

public class PlayerSyncData extends SyncData{
	public Vector2 velocity;
	public float rotation, ping;
	public ShipState state;

	public PlayerSyncData(long id, float x, float y, ShipState state, float rotation, float ping, Vector2 velocity){
		super(id, x, y);
		this.velocity = velocity;
		this.rotation = rotation;
		this.state = state;
		this.ping = ping;
	}
	
	public PlayerSyncData(){}
}
