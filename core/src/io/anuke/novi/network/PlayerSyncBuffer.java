package io.anuke.novi.network;

import com.badlogic.gdx.math.Vector2;

public class PlayerSyncBuffer extends SyncBuffer{
	public Vector2 velocity;
	public float rotation, respawntime, ping;

	public PlayerSyncBuffer(long id, float x, float y, float rotation, float respawntime, float ping, Vector2 velocity){
		super(id, x, y);
		this.velocity = velocity;
		this.rotation = rotation;
		this.respawntime = respawntime;
		this.ping = ping;
	}
	
	public PlayerSyncBuffer(){
		//empty constructor
	}
}
