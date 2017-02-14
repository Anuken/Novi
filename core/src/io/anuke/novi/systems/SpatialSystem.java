package io.anuke.novi.systems;

import java.util.Collection;
import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ObjectSet;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.modules.World;
import io.anuke.novi.utils.CollisionConsumer;
import io.anuke.novi.utils.WrappedQuadTree;

public class SpatialSystem extends IteratingSystem{
	public WrappedQuadTree quadtree = new WrappedQuadTree(4, new Rectangle(0, 0, World.worldSize, World.worldSize));
	private ObjectSet<Long> rayHit = new ObjectSet<Long>();

	@Override
	public void update(Entity entity){
		quadtree.insert(entity);
	}

	@Override
	public void update(Collection<Entity> entities){
		quadtree.clear();

		super.update(entities);
	}

	public boolean accept(Entity entity){
		return entity instanceof SolidEntity;
	}

	public void getNearby(float x, float y, float size, Consumer<Entity> cons){
		quadtree.getPossibleIntersections(cons, Rectangle.tmp.set(x - size / 2, y - size / 2, size, size));
	}
	
	public void raycastBullet(float x, float y, float x2, float y2, SolidEntity hitter){
		rayHit.clear();
		
		float lastx = hitter.x;
		float lasty = hitter.y;
		
		raycast(x, y, x2, y2, (entity, cx, cy)->{
			if(!rayHit.contains(entity.getID())){
				hitter.x = cx;
				hitter.y = cy;
				hitter.material.updateHitbox();
				
				if(entity.collides(hitter)){
					entity.collisionEvent(hitter);
					hitter.collisionEvent(entity);
				}
				
				rayHit.add(entity.getID());
			}
		});
		
		hitter.x = lastx;
		hitter.y = lasty;
	}
	
	//TODO fix inefficient algorithm
	public void raycast(float x0f, float y0f, float x1f, float y1f, CollisionConsumer cons){
		int x0 = (int) x0f;
		int y0 = (int) y0f;
		int x1 = (int) x1f;
		int y1 = (int) y1f;
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);
		
		int step = 1;

		int sx = x0 < x1 ? step : -step;
		int sy = y0 < y1 ? step : -step;

		int err = dx - dy;
		int e2;
		while(true){
			
			quadtree.getPointIntersections(cons, World.bound(x0), World.bound(y0));
			
			if(x0 == x1 && y0 == y1)
				break;

			e2 = 2 * err;
			if(e2 > -dy){
				err = err - dy;
				x0 = x0 + sx;
			}

			if(e2 < dx){
				err = err + dx;
				y0 = y0 + sy;
			}
		}
	}
	
	/*
	public void raycast(float x, float y, float x2, float y2, CollisionConsumer cons){
		float minx, miny, maxx, maxy;
		
		minx = Math.min(x,  x2);
		miny = Math.min(y,  y2);
		
		maxx = Math.max(x,  x2);
		maxy = Math.max(y,  y2);
		
		quadtree.getPossibleIntersections((entity)->{
			SolidEntity s = (SolidEntity)entity;
			
			s.material.updateHitbox();
			
			boolean intersects = s.material.intersects(x, y, x2, y2);// || s.material.intersects(wrap(x), wrap(y), wrap(x2), wrap(y2));
			
			//s.material.updateHitboxWrap();
			
			//intersects = intersects || s.material.intersects(x, y, x2, y2) || s.material.intersects(wrap(x), wrap(y), wrap(x2), wrap(y2));
			
			if(intersects){
				cons.accept(s, s.x, s.y);
			}
			
		}, Rectangle.tmp.set(minx, miny, maxx-minx, maxy-miny));
	}
	*/
}
