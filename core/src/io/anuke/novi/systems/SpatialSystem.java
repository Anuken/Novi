package io.anuke.novi.systems;

import java.util.Collection;
import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.modules.World;
import io.anuke.novi.utils.WrappedQuadTree;

public class SpatialSystem extends IteratingSystem{
	public WrappedQuadTree<Entity> quadtree = new WrappedQuadTree<Entity>(4, new Rectangle(0, 0, World.worldSize, World.worldSize));
	
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
		quadtree.getPossibleIntersections(cons, Rectangle.tmp.set(x-size/2, y-size/2, size, size));
	}
}
