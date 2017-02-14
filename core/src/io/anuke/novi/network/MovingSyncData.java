package io.anuke.novi.network;

import com.badlogic.gdx.math.Vector2;

public class MovingSyncData extends SyncData{
	public Vector2 velocity;

	public MovingSyncData(long id, float x, float y, Vector2 velocity){
		super(id, x, y);
		this.velocity = velocity;
	}

	public MovingSyncData(){

	}
}
