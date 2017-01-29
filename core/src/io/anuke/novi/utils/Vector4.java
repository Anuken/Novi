package io.anuke.novi.utils;

public class Vector4{
	float x, y, xvelocity, yvelocity;

	public Vector4(){

	}

	public Vector4(float x, float y, float xvelocity, float yvelocity){
		this.x = x;
		this.y = y;
		this.xvelocity = xvelocity;
		this.yvelocity = yvelocity;
	}

	public boolean zero(){
		return x == 0 && y == 0 && xvelocity == 0 && yvelocity == 0;
	}

	public void set(Vector4 other){
		this.x = other.x;
		this.y = other.y;
		this.xvelocity = other.xvelocity;
		this.yvelocity = other.yvelocity;
	}

	public void set(float x, float y, float xvelocity, float yvelocity){
		this.x = x;
		this.y = y;
		this.xvelocity = xvelocity;
		this.yvelocity = yvelocity;
	}

	public String toString(){
		return "(" + x + ", " + y + "), (" + xvelocity + ", " + yvelocity + ")";
	}
}
