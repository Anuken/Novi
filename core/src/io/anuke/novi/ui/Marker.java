package io.anuke.novi.ui;

public class Marker{
	Landmark mark;
	float x, y;
	
	public Marker(){}
	
	public Marker(Landmark mark, float x, float y){
		this.x = x;
		this.y = y;
		this.mark = mark;
	}
}
