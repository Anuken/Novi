package io.anuke.novi.entities;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.Novi;
import io.anuke.novi.modules.Renderer;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.systems.EmptySystem;
import io.anuke.novi.systems.EntitySystem;
import io.anuke.novi.utils.WorldUtils;

public abstract class Entity{
	static public Novi novi;
	static public NoviServer server;
	static private long lastid;
	static public Vector2 vector = Vector2.Zero; // Vector2 object used for calculations; is reused
	public static ConcurrentHashMap<Long, Entity> entities = new ConcurrentHashMap<Long, Entity>();
	private static ArrayList<EntitySystem> systems = new ArrayList<EntitySystem>();
	private static EntitySystem basesystem = new EmptySystem();
	public static Renderer renderer; // renderer reference for drawing things
	private long id;
	public float x, y;

	abstract public void update();

	abstract public void Draw();
	
	public void baseUpdate(){
		updateBounds();
		update();
	}

	//used to make entities not fly off the map
	public void updateBounds(){
		x = WorldUtils.bound(x);
		y = WorldUtils.bound(y);
	}

	//whether or not this entity is loaded (is drawn/updated on screen)
	//AAAAAAAggggg
	public boolean loaded(float playerx, float playery){
		return WorldUtils.loopDist(x, playerx, y, playery, 1300f);
	}

	//called when this entity object is recieved
	public void onRecieve(){

	}

	//called when this entity is removed
	public void removeEvent(){

	}

	//guess what this does
	public Entity setPosition(float x, float y){
		this.x = x;
		this.y = y;
		return this;
	}

	public void SendSelf(){
		server.server.sendToAllTCP(this);
	}

	public Entity AddSelf(){
		entities.put(id, this);
		return this;
	}

	public void RemoveSelf(){
		entities.remove(this.id);
	}

	public void resetID(long newid){
		RemoveSelf();
		this.id = newid;
		AddSelf();
		lastid = id + 1;
	}

	public long GetID(){
		return id;
	}

	public boolean inRange(Entity entity, float rad){
		return WorldUtils.loopDist(entity.x, entity.y, x, y, rad);
	}

	public static Entity getEntity(long id){
		return entities.get(id);
	}

	public static boolean entityExists(long id){
		return entities.get(id) != null;
	}

	public void serverUpdate(){
		//do nothing
	}

	public static float delta(){
		return server == null ? (Gdx.graphics.getDeltaTime() * 60f) : server.delta();
	}

	public static long frame(){
		return server == null ? Gdx.graphics.getFrameId() : server.updater.frameID();
	}

	public static Iterable<EntitySystem> getSystems(){
		return systems;
	}

	public static void addSystem(EntitySystem system){
		systems.add(system);
	}
	
	public static void setBaseSystem(EntitySystem system){
		basesystem = system;
	}

	public Entity(){
		id = lastid ++;
	}

	public static void updateAll(){
		for(Entity entity : Entity.entities.values()){
			if(!basesystem.accept(entity)) continue;
			entity.baseUpdate();
			if(NoviServer.active){
				entity.serverUpdate();
			}else{
				entity.Draw();
			}
			for(EntitySystem system : systems){
				if(system.accept(entity)) system.update(entity);
			}
		}
	}
}
