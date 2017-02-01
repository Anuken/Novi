package io.anuke.novi.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.base.Player;
import io.anuke.novi.modules.World;
import io.anuke.novi.server.NoviServer;
import io.anuke.ucore.util.QuadTree.QuadTreeObject;

public abstract class Entity implements QuadTreeObject{
	static private long lastid;
	static public Vector2 vector = Vector2.Zero; // Vector2 object used for calculations; is reused

	private long id;
	public float x, y;

	public void update(){}
	public void draw(){}
	public void serverUpdate(){}
	public float getLayer(){return 0f;};
	
	public void baseUpdate(){
		updateBounds();
		update();
	}

	//used to make entities not fly off the map
	public void updateBounds(){
		x = World.bound(x);
		y = World.bound(y);
	}

	//whether or not this entity is loaded (is drawn/updated on screen)
	public boolean loaded(float playerx, float playery){
		return World.loopedWithin(x, playerx, y, playery, 1300f);
	}

	//called when this entity object is recieved
	public void onRecieve(){}

	//called when this entity is removed
	public void onRemove(){}

	
	public Entity set(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}

	public void send(){
		NoviServer.instance().sendEntity(this);
	}

	public Entity add(){
		Entities.add(this);
		return this;
	}

	public void remove(){
		Entities.remove(this);
	}
	
	/**Removes the entity globally (that is, from the server as well)*/
	public void removeServer(){
		NoviServer.instance().removeEntity(this);
	}
	
	/**Only used for players. Changes the entity's ID.*/
	public void resetID(long newid){
		remove();
		this.id = newid;
		add();
		lastid = id + 1;
	}

	public long getID(){
		return id;
	}

	boolean inRange(float rad){
		return World.loopedWithin(x, y, x, y, rad);
	}
	
	public Entity(){
		id = lastid ++;
	}
	
	public Player player(){
		return (Player)this;
	}

	protected static float delta(){
		return Novi.delta();
	}

	protected static long frame(){
		return Novi.frame();
	}
	
	@Override
	public void getBoundingBox(Rectangle out){
		out.setSize(12, 12);
		out.setCenter(x, y);
	}
}
