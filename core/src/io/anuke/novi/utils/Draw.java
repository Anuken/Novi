package io.anuke.novi.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

import io.anuke.novi.modules.Renderer;
import io.anuke.ucore.graphics.Atlas;

public class Draw{
	private static Renderer rend;
	
	public static void init(Renderer renderer){
		rend = renderer;
	}
	
	public static void rect(String name, float x, float y){
		TextureRegion reg = region(name);
		rend.batch.draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight());
	}
	
	public static void rect(String name, float x, float y, float rotation){
		TextureRegion reg = region(name);
		rend.batch.draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth()/2, reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight(), 1f, 1f, rotation);
	}
	
	public static void rect(String name, float x, float y, float scalex, float scaley, float rotation){
		TextureRegion reg = region(name);
		rend.batch.draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth()/2, reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight(), scalex, scaley, rotation);
	}
	
	public static void rect(TextureRegion reg, float x, float y, float rotation){
		rend.batch.draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth()/2, reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight(), 1f, 1f, rotation);
	}
	
	public static void rect(Texture reg, float x, float y, float rotation){
		rend.batch.draw(reg, x - reg.getWidth()/2, y - reg.getHeight()/2, 
				reg.getWidth()/2, reg.getHeight()/2, 
				reg.getWidth(), reg.getHeight(), 
				1f, 1f, rotation, 
				0, 0, reg.getWidth(), reg.getHeight(), 
				false, false);
	}
	
	public static void text(String text, float x, float y){
		rend.font.draw(rend.batch, text, x, y, 0, Align.center, false);
	}
	
	public static void text(String text, float x, float y, int align){
		rend.font.draw(rend.batch, text, x, y, 0, align, false);
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
	public static void color(){
		rend.batch.setColor(Color.WHITE);
	}
	
	public static void tcolor(Color color){
		rend.font.setColor(color);
	}
	
	public static void tcolor(){
		rend.font.setColor(Color.WHITE);
	}
	
	public static TextureRegion region(String name){
		return rend.atlas.findRegion(name);
	}
	
	public static Atlas atlas(){
		return rend.atlas;
	}
}
