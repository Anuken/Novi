package io.anuke.novi.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import io.anuke.novi.modules.Renderer;

public class Draw{
	private static Renderer rend;
	
	public static void init(Renderer renderer){
		rend = renderer;
	}
	
	public static void tex(String name, float x, float y){
		TextureRegion reg = region(name);
		rend.batch.draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight());
	}
	
	public static void tex(String name, float x, float y, float rotation){
		TextureRegion reg = region(name);
		rend.batch.draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth()/2, reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight(), 1f, 1f, rotation);
	}
	
	public static void color(Color color){
		rend.batch.setColor(color);
	}
	
	public static void color(float r, float g, float b){
		rend.batch.setColor(r, g, b, 1f);
	}
	
	public static void color(float r, float g, float b, float a){
		rend.batch.setColor(r, g, b, a);
	}
	
	public static void color(float a){
		rend.batch.setColor(1f, 1f, 1f, a);
	}
	
	/**Resets the color to white.*/
	public static void colorset(){
		rend.batch.setColor(Color.WHITE);
	}
	
	public static TextureRegion region(String name){
		return rend.atlas.findRegion(name);
	}
}
