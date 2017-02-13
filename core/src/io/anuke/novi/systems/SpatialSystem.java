package io.anuke.novi.systems;

import java.util.Collection;
import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.modules.World;
import io.anuke.novi.utils.CollisionConsumer;
import io.anuke.novi.utils.WrappedQuadTree;

public class SpatialSystem extends IteratingSystem{
	public WrappedQuadTree quadtree = new WrappedQuadTree(4, new Rectangle(0, 0, World.worldSize, World.worldSize));

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
}
