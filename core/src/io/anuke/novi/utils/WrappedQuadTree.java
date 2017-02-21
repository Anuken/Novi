package io.anuke.novi.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.modules.World;


/**
 * A basic quad tree.
 * <p>
 * This class represents any node, but you will likely only interact with the root node.
 *
 * @param <Entity> The type of object this quad tree should contain. An object only requires some way of getting rough bounds.
 * @author xSke
 * @author Anuke
 */
public class WrappedQuadTree {
    private int maxObjectsPerNode;

    private int level;
    private Rectangle bounds;
    private ArrayList<Entity> objects;

    private static Rectangle tmp = new Rectangle();

    private boolean leaf;
    private WrappedQuadTree bottomLeftChild;
    private WrappedQuadTree bottomRightChild;
    private WrappedQuadTree topLeftChild;
    private WrappedQuadTree topRightChild;

    /**
     * Constructs a new quad tree.
     *
     * @param maxObjectsPerNode How many objects may be in a node before it will split.
     *                          Should be around 3-5 for optimal results, depending on your use case.
     * @param bounds            The total bounds of this root node
     */
    public WrappedQuadTree(int maxObjectsPerNode, Rectangle bounds) {
        this(maxObjectsPerNode, 0, bounds);
    }

    private WrappedQuadTree(int maxObjectsPerNode, int level, Rectangle bounds) {
        this.level = level;
        this.bounds = bounds;
        this.maxObjectsPerNode = maxObjectsPerNode;
        objects = new ArrayList<Entity>();
        leaf = true;
    }

    private void split() {
        if (!leaf) return;

        float subW = bounds.width / 2;
        float subH = bounds.height / 2;

        leaf = false;
        bottomLeftChild = new WrappedQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x, bounds.y, subW, subH));
        bottomRightChild = new WrappedQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x + subW, bounds.y, subW, subH));
        topLeftChild = new WrappedQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x, bounds.y + subH, subW, subH));
        topRightChild = new WrappedQuadTree(maxObjectsPerNode, level + 1, new Rectangle(bounds.x + subW, bounds.y + subH, subW, subH));

        // Transfer objects to children if they fit entirely in one
        for (Iterator<Entity> iterator = objects.iterator(); iterator.hasNext(); ) {
            Entity obj = iterator.next();
            obj.getBoundingBox(tmp);
            WrappedQuadTree child = getFittingChild(tmp);
            if (child != null) {
                child.insert(obj);
                iterator.remove();
            }
        }
    }

    private void unsplit() {
        if (leaf) return;
        leaf = true;

        objects.addAll(bottomLeftChild.objects);
        objects.addAll(bottomRightChild.objects);
        objects.addAll(topLeftChild.objects);
        objects.addAll(topRightChild.objects);
        bottomLeftChild = bottomRightChild = topLeftChild = topRightChild = null;
    }

    /**
     * Inserts an object into this node or its child nodes. This will split a leaf node if it exceeds the object limit.
     */
    public void insert(Entity obj) {
        obj.getBoundingBox(tmp);
        if (!bounds.overlaps(tmp)) {
            // New object not in quad tree, ignoring
            // throw an exception?
            return;
        }

        if (leaf && (objects.size() + 1) > maxObjectsPerNode) split();

        if (leaf) {
            // Leaf, so no need to add to children, just add to root
            objects.add(obj);
        } else {
            obj.getBoundingBox(tmp);
            // Add to relevant child, or root if can't fit completely in a child
            WrappedQuadTree child = getFittingChild(tmp);
            if (child != null) {
                child.insert(obj);
            } else {
                objects.add(obj);
            }
        }
    }

    /**
     * Removes an object from this node or its child nodes.
     */
    public void remove(Entity obj) {
        if (leaf) {
            // Leaf, no children, remove from root
            objects.remove(obj);
        } else {
            // Remove from relevant child
            obj.getBoundingBox(tmp);
            WrappedQuadTree child = getFittingChild(tmp);

            if (child != null) {
                child.remove(obj);
            } else {
                // Or root if object doesn't fit in a child
                objects.remove(obj);
            }

            if (getTotalObjectCount() <= maxObjectsPerNode) unsplit();
        }
    }
    
    /**Removes all objects.*/
    public void clear(){
    	objects.clear();
    	if(bottomLeftChild!=null)bottomLeftChild.clear();
    	if(bottomRightChild!=null)bottomRightChild.clear();
    	if(topLeftChild!=null)topLeftChild.clear();
    	if(topRightChild!=null)topRightChild.clear();
    }

    private WrappedQuadTree getFittingChild(Rectangle boundingBox) {
        float verticalMidpoint = bounds.x + (bounds.width / 2);
        float horizontalMidpoint = bounds.y + (bounds.height / 2);

        // Object can completely fit within the top quadrants
        boolean topQuadrant = boundingBox.y > horizontalMidpoint;
        // Object can completely fit within the bottom quadrants
        boolean bottomQuadrant = boundingBox.y < horizontalMidpoint && (boundingBox.y + boundingBox.height) < horizontalMidpoint;

        // Object can completely fit within the left quadrants
        if (boundingBox.x < verticalMidpoint && boundingBox.x + boundingBox.width < verticalMidpoint) {
            if (topQuadrant) {
                return topLeftChild;
            } else if (bottomQuadrant) {
                return bottomLeftChild;
            }
        }
        // Object can completely fit within the right quadrants
        else if (boundingBox.x > verticalMidpoint) {
            if (topQuadrant) {
                return topRightChild;
            } else if (bottomQuadrant) {
                return bottomRightChild;
            }
        }

        // Else, object needs to be in parent cause it can't fit completely in a quadrant
        return null;
    }

    /**
     * Returns the leaf node directly at the given coordinates, or null if the coordinates are outside this node's bounds.
     */
    public WrappedQuadTree getNodeAt(float x, float y) {
        if (!bounds.contains(x, y)) return null;
        if (leaf) return this;

        if (topLeftChild.bounds.contains(x, y)) return topLeftChild.getNodeAt(x, y);
        if (topRightChild.bounds.contains(x, y)) return topRightChild.getNodeAt(x, y);
        if (bottomLeftChild.bounds.contains(x, y)) return bottomLeftChild.getNodeAt(x, y);
        if (bottomRightChild.bounds.contains(x, y)) return bottomRightChild.getNodeAt(x, y);

        // This should never happen
        return null;
    }
    
    /**
     * Fills the out parameter with any objects that may intersect the given rectangle.
     * <p>
     * This will result in false positives, but never a false negative.
     * </p>
     * */
    public void getPossibleIntersections(Consumer<Entity> cons, Rectangle rect) {
        if (!leaf) {
        	
        	getChildIntersections(cons, rect);
            
            float topx = rect.x + rect.getWidth();
            float topy = rect.y + rect.getHeight();
            float x = rect.x;
            float y = rect.y;
            float size = World.size;
            
            if(topx > size && y < 0){
            	rect.x -= size;
            	rect.y += size;
            	getChildIntersections(cons, rect);
            	rect.x += size;
            	rect.y -= size;
            }
            
            if(x < 0 && topy > size){
            	rect.x += size;
            	rect.y -= size;
            	getChildIntersections(cons, rect);
            	rect.x -= size;
            	rect.y += size;
            }
            
            if(y < 0){
            	rect.y += size;
            	getChildIntersections(cons, rect);
            	rect.y -= size;
            }
            
            if(x < 0){
            	rect.x += size;
            	getChildIntersections(cons, rect);
            	rect.x -= size;
            }
            
            if(x < 0 && y < 0){
            	rect.x += size;
            	rect.y += size;
            	getChildIntersections(cons, rect);
            	rect.x -= size;
            	rect.y -= size;
            }
            
            
            if(topy > size){
            	rect.y -= size;
            	getChildIntersections(cons, rect);
            	rect.y += size;
            }
            
            if(topx > size){
            	rect.x -= size;
            	getChildIntersections(cons, rect);
            	rect.x += size;
            }
            
            if(topy > size && topx > size){
            	rect.x -= size;
            	rect.y -= size;
            	getChildIntersections(cons, rect);
            	rect.x += size;
            	rect.y += size;
            }
        }
        
        for(Entity object : objects)
        	cons.accept(object);
    }
    
    /**Gets the intersections for the children, not the object itself*/
    private void getChildIntersections(Consumer<Entity> cons, Rectangle rect){
        if (topLeftChild.bounds.overlaps(rect)) topLeftChild.getIntersecting(cons, rect);
        if (topRightChild.bounds.overlaps(rect)) topRightChild.getIntersecting(cons, rect);
        if (bottomLeftChild.bounds.overlaps(rect)) bottomLeftChild.getIntersecting(cons, rect);
        if (bottomRightChild.bounds.overlaps(rect)) bottomRightChild.getIntersecting(cons, rect);
    }
    
    public void getPointIntersections(CollisionConsumer cons, float x, float y){
    	if (!leaf) {
    		if (topLeftChild.bounds.contains(x, y)) topLeftChild.getPointIntersections(cons, x, y);
    		else if (topRightChild.bounds.contains(x, y)) topRightChild.getPointIntersections(cons, x, y);
    		else if (bottomLeftChild.bounds.contains(x, y)) bottomLeftChild.getPointIntersections(cons, x, y);
    		else if (bottomRightChild.bounds.contains(x, y)) bottomRightChild.getPointIntersections(cons, x, y);
    	}
        
        for(Entity object : objects){
        	if(object instanceof SolidEntity){
        		SolidEntity s = (SolidEntity)object;
        		s.material.updateHitbox();
        		
            	if(s.material.rect.contains(x, y) /*|| s.material.rect.contains(World.wrap(x), World.wrap(y))*/){
            		cons.accept(s, x, y);
            		continue;
            	}
            	
            	//s.material.updateHitboxWrap();
            	
            	//if(s.material.rect.contains(World.wrap(x), World.wrap(y)) || s.material.rect.contains(x, y)){
            	//	cons.accept(s, x, y);
            	//	continue;
            	//}
        	}
        }
    }
    
    /**Internally used for children.*/
    private void getIntersecting(Consumer<Entity> cons, Rectangle rect){
    	if (!leaf) {
    		getChildIntersections(cons, rect);
        }
        
        for(Entity object : objects)
        	cons.accept(object);
    }

    /**
     * Returns whether this node is a leaf node (has no child nodes)
     */
    public boolean isLeaf() {
        return leaf;
    }

    /**
     * Returns the bottom left child node, or null if this node is a leaf node.
     */
    public WrappedQuadTree getBottomLeftChild() {
        return bottomLeftChild;
    }

    /**
     * Returns the bottom right child node, or null if this node is a leaf node.
     */
    public WrappedQuadTree getBottomRightChild() {
        return bottomRightChild;
    }

    /**
     * Returns the top left child node, or null if this node is a leaf node.
     */
    public WrappedQuadTree getTopLeftChild() {
        return topLeftChild;
    }

    /**
     * Returns the top right child node, or null if this node is a leaf node.
     */
    public WrappedQuadTree getTopRightChild() {
        return topRightChild;
    }

    /**
     * Returns the entire bounds of this node.
     */
    public Rectangle getBounds() {
        return bounds;
    }

    /**
     * Returns the objects in this node only.
     * <p>
     * If this node isn't a leaf node, it will only return the objects that don't fit perfectly into a specific child node (lie on a border).
     */
    public ArrayList<Entity> getObjects() {
        return objects;
    }

    /**
     * Returns the total number of objects in this node and all child nodes, recursively
     */
    public int getTotalObjectCount() {
        int count = objects.size();
        if (!leaf) {
            count += topLeftChild.getTotalObjectCount();
            count += topRightChild.getTotalObjectCount();
            count += bottomLeftChild.getTotalObjectCount();
            count += bottomRightChild.getTotalObjectCount();
        }
        return count;
    }

    /**
     * Fills the out ArrayList with all objects in this node and all child nodes, recursively.
     */
    public void getAllChildren(ArrayList<Entity> out) {
        out.addAll(objects);

        if (!leaf) {
            topLeftChild.getAllChildren(out);
            topRightChild.getAllChildren(out);
            bottomLeftChild.getAllChildren(out);
            bottomRightChild.getAllChildren(out);
        }
    }

    /**
     * Represents an object in a QuadTree.
     */
    public interface QuadTreeObject {
        /**
         * Fills the out parameter with this element's rough bounding box. This should never be smaller than the actual object, but may be larger.
         */
        void getBoundingBox(Rectangle out);
    }
}
