package io.anuke.novi.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.badlogic.gdx.utils.Array;

import io.anuke.novi.server.NoviServer;
import io.anuke.novi.systems.EmptySystem;
import io.anuke.novi.systems.EntitySystem;
import io.anuke.novi.systems.IteratingSystem;

public class Entities{
	private static ArrayList<Entity> list = new ArrayList<Entity>();
	private static HashMap<Long, Entity> map = new HashMap<Long, Entity>();

	private static ArrayList<Entity> toRemove = new ArrayList<Entity>();
	private static ArrayList<Entity> toAdd = new ArrayList<Entity>();

	private static Array<EntitySystem> systems = new Array<EntitySystem>();
	private static IteratingSystem basesystem = new EmptySystem();

	public static synchronized void add(Entity entity){
		toAdd.add(entity);
	}

	public static synchronized void remove(Entity entity){
		toRemove.add(entity);
	}

	public static synchronized void remove(long id){
		remove(get(id));
	}

	public static boolean has(long id){
		return map.containsKey(id);
	}

	public static Entity get(long id){
		return map.get(id);
	}

	public static Collection<Entity> list(){
		return list;
	}

	public static synchronized void loadEntities(Collection<Entity> entities){
		list.clear();
		map.clear();

		for(Entity entity : entities){
			entity.add();
		}
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

	public static synchronized void updateAll(){
		Collection<Entity> entities = list();

		for(EntitySystem system : systems){
			system.update(entities);
		}

		for(Entity entity : entities){
			if(!basesystem.accept(entity))
				continue;

			entity.baseUpdate();

			if(NoviServer.active()){
				entity.serverUpdate();
			}
		}

		for(Entity entity : toAdd){
			if(entity == null)
				continue;

			map.put(entity.getID(), entity);

			list.add(entity);
		}

		for(Entity entity : toRemove){
			if(entity == null)
				continue;

			map.remove(entity.getID());
			
			if(NoviServer.active()){
				binaryRemove(entity);
			}else{
				list.remove(entity);
			}
		}

		if(toAdd.size() != 0 && !NoviServer.active()){
			list.sort(Entities::compare);
		}

		toAdd.clear();
		toRemove.clear();
	}

	public static synchronized void drawAll(float playerx, float playery){
		for(Entity entity : list()){
			if(entity.loaded(playerx, playery))
				entity.draw();
		}
	}

	private static void binaryRemove(Entity entity){
		int id = binarySearch(entity.getID());
		
		if(id != -1){
			list.remove(id);
		}
	}

	public static int binarySearch(long key){
		
		int low = 0;
		int high = list.size() - 1;

		while(high >= low){
			int middle = (low + high) / 2;
			long id = list.get(middle).getID();
			
			if(id == key){
				return middle;
			}else if(id < key){
				low = middle + 1;
			}else if(id > key){
				high = middle - 1;
			}
		}
		
		return -1;
	}

	private static int compareID(Entity a, Entity b){
		return a.getID() > b.getID() ? 1 : -1;
	}

	private static int compare(Entity a, Entity b){
		if(a.getLayer() == b.getLayer())
			return 0;
		return a.getLayer() > b.getLayer() ? 1 : -1;
	}
}
