package io.anuke.novi.ui;

public class Marker{
	MarkerType type;
	float x, y;
	
	public Marker(){}
	
	public Marker(MarkerType mark, float x, float y){
		this.x = x;
		this.y = y;
		this.type = mark;
	}
}
