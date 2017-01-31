package io.anuke.novi.systems;

import static io.anuke.ucore.UCore.scl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import com.badlogic.gdx.utils.Predicate;

import io.anuke.novi.entities.Entity;
import io.anuke.ucore.util.GridMap;

public class SpatialSystem extends IteratingSystem{
	public static final float cellsize = 70;
	public static final int maxCells = 3000;

	private GridMap<ArrayList<Entity>> map = new GridMap<ArrayList<Entity>>();

	@Override
	public void update(Entity entity){
		int x = (int) (entity.x / cellsize), y = (int) (entity.y / cellsize);

		ArrayList<Entity> set = map.get(x, y);

		if(set == null){
			map.put(x, y, (set = new ArrayList<Entity>()));
		}

		set.add(entity);
	}
	
	public synchronized void getNearbyEntities(float cx, float cy, float range, Predicate<Entity> pred,
			Consumer<Entity> con){

		if(range < 1 || range < 1)
			throw new IllegalArgumentException("rangex and rangey cannot be negative.");

		int maxx = scl(cx + range, cellsize), maxy = scl(cy + range, cellsize), minx = scl(cx - range, cellsize),
				miny = scl(cy - range, cellsize);

		for(int x = minx; x < maxx + 1; x++){
			for(int y = miny; y < maxy + 1; y++){
				ArrayList<Entity> set = map.get(x, y);
				if(set != null){
					for(Entity e : set){
						if(pred.evaluate(e) && Math.abs(e.x - cx) < range && Math.abs(e.y - cy) < range){
							con.accept(e);
						}
					}
				}
			}
		}
	}
	
	public ArrayList<Entity> getEntitiesIn(float cx, float cy){
		int x = (int) (cx / cellsize), y = (int) (cy / cellsize);
		return map.get(x, y);
	}

	@Override
	public void update(Collection<Entity> entities){
		/*
		
		// clear cells just in case
		if(map.size() > maxCells){
			Novi.log("Too many mapper cells (" + map.size() + "). Clearing and calling System#gc().");
			map.clear();
			// might as well try to clean up
			System.gc();
		}

		for(ArrayList<Entity> set : map.values()){
			set.clear();
		}

		super.update(entities);
		*/
	}

}
