package io.anuke.novi.utils;

import io.anuke.novi.modules.World;

//class full of arcane magic BS
//don't change anything or collisions will break for no reason
public class WorldUtils{
	
	//square distance with wrapping
	public static boolean loopDist(float x1, float x2, float y1, float y2, float rad){
		return (Math.abs(relative3(x1, x2)) < rad && Math.abs(relative3(y1, y2)) < rad);
	}
	
	//wrapped normal distance
	public static float wrappedDist(float x1, float y1, float x2, float y2){
		float ydst = relative3(y1,y2);
		float xdst = relative3(x1,x2);
		return (float)Math.sqrt(ydst * ydst + xdst * xdst);
	}
	
	//returns relative shortest distance, wrapped
	public static float relative3(float a, float b){
		float ndst = a - b;
		float wdst = owrapdst(a, b);
		return Math.abs(ndst) < Math.abs(wdst) ? ndst : wdst;
	}
	
	//returns the wrapped distance from a to b
	public static float owrapdst(float a, float b){
		if(a > b){
			return a - (World.worldSize + b);
		}else{
			return -(b - (World.worldSize + a));
		}
	}

	//returns the unsigned wrapped distance from a to b
	public static float uowrapdst(float a, float b){
		return a - (World.worldSize + b);
	}

	//minimum distance from a to b with wrapping
	public static float wdist(float a, float b){
		float ndst = Math.abs(a - b);
		float wdst = Math.abs(uowrapdst(a, b));
		return Math.min(ndst, wdst);
	}

	//wraps crap
	public static float wrap(float i){
		if(i > World.worldSize / 2) return World.worldSize - i;
		return i;
	}
	
	//corrects coord bounds
	public static float bound(float i){
		if(i < 0){
			return World.worldSize + i;
		}else{
			return i % World.worldSize;
		}
	}
}
