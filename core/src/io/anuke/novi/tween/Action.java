package io.anuke.novi.tween;

public interface Action{
	public boolean update();
	public Object getTarget();
	
	public default void add(){
		Actions.add(this);
	}
}
