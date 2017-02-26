package io.anuke.novi.tween;

import com.badlogic.gdx.utils.SnapshotArray;

import io.anuke.novi.entities.FlyingEntity;

public class Actions{
	private static SnapshotArray<Action> actions = new SnapshotArray<Action>();
	
	public static void update(){
		Object[] o = actions.begin();
		
		for(Object ob : o){
			if(ob != null && ((Action)ob).update()){
				actions.removeValue((Action)ob, true);
			}
		}
		
		actions.end();
	}
	
	public static void clear(Object object){
		Object[] o = actions.begin();
		
		for(Object ob : o){
			if(ob != null && ((Action)ob).getTarget() == object){
				actions.removeValue((Action)ob, true);
			}
		}
		
		actions.end();
	}
	
	public static void moveTo(FlyingEntity entity, float x, float y, float speed){
		new MoveToAction(entity, x, y, speed).add();
	}
	
	public static void add(Action a){
		actions.add(a);
	}
}
