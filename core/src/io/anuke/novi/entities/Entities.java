package io.anuke.novi.entities;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.gdx.utils.Array;

import io.anuke.novi.server.NoviServer;
import io.anuke.novi.systems.EmptySystem;
import io.anuke.novi.systems.EntitySystem;
import io.anuke.novi.systems.IteratingSystem;

public class Entities{
	private static ConcurrentHashMap<Long, Entity> entities = new ConcurrentHashMap<Long, Entity>();
	private static Array<EntitySystem> systems = new Array<EntitySystem>();
	private static IteratingSystem basesystem = new EmptySystem();
	
	public static void add(Entity entity){
		//TODO
	}
	
	public static void remove(Entity entity){
		remove(entity.getID());
	}
	
	public static void remove(long id){
		//TODO
	}
	
	public static boolean has(long id){
		//TODO
		return false;
	}
	
	public static Entity get(long id){
		//TODO
		return null;
	}
	
	public static Collection<Entity> list(){
		//TODO
		return null;
	}
	
	public static void loadEntities(Collection<Entity> entities){
		//TODO
	}

	public static Iterable<EntitySystem> getSystems(){
		return systems;
	}

	public static void addSystem(EntitySystem system){
		systems.add(system);
	}
	
	public static void setBaseSystem(IteratingSystem system){
		basesystem = system;
	}

	public static void updateAll(){
		Collection<Entity> entities = Entities.entities.values();
		
		for(EntitySystem system : systems){
			system.update(entities);
		}
		
		for(Entity entity : entities){
			if(!basesystem.accept(entity)) continue;
			
			entity.baseUpdate();
			if(NoviServer.active()){
				entity.serverUpdate();
			}else{
				entity.draw();
			}
		}
	}
}
