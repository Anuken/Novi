package io.anuke.novi.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import io.anuke.novi.modules.World;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.core.DrawContext;

public class Wrap{
	private static Vector2 vector = new Vector2();
	
	private static Batch batch(){
		return Draw.batch();
	}
	
	public static void shader(){
		shader(null);
	}
	
	public static void shader(ShaderProgram shader, Object...params){
		boolean rendering = batch().isDrawing();
		
		if(rendering)
			batch().end();
		
		if(shader != null)
		Shaders.setParams(shader, params);
		
		batch().setShader(shader);
		
		if(rendering)
			batch().begin();
	}
	
	public static void rect(String name, float x, float y){
		TextureRegion reg = region(name);
		rect(reg, x, y, 0);
	}
	
	public static void rect(String name, float x, float y, float rotation){
		TextureRegion reg = region(name);
		rect(reg, x, y, rotation);
	}
	
	public static void rect(String name, float x, float y, float scalex, float scaley, float rotation){
		TextureRegion reg = region(name);
		
		x = overlapx(x, reg.getRegionWidth());
		y = overlapy(y, reg.getRegionHeight());
		
		batch().draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth()/2, reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight(), scalex, scaley, rotation);
	}
	
	public static void crect(String name, float x, float y, float width, float height){
		TextureRegion reg = region(name);

		batch().draw(reg, x, y,width, height);
	}
	
	public static void rect(TextureRegion reg, float x, float y, float rotation){
		
		x = overlapx(x, reg.getRegionWidth());
		y = overlapy(y, reg.getRegionHeight());
		
		batch().draw(reg, x - reg.getRegionWidth()/2, y - reg.getRegionHeight()/2, reg.getRegionWidth()/2, reg.getRegionHeight()/2, reg.getRegionWidth(), reg.getRegionHeight(), 1f, 1f, rotation);
	}
	
	public static void laser(String line, String edge, float x, float y, float x2, float y2){
		laser(edge, line, x, y, x2, y2, vector.set(x2 - x, y2 -y).angle());
	}
	
	public static void laser(String line, String edge, float x, float y, float x2, float y2, float rotation){
		
		Wrap.line(line,x, y, x2, y2, 12f);
		
		Wrap.rect(edge, x, y, rotation + 180);
		
		Wrap.rect(edge, x2, y2, rotation);
	}
	
	public static void line(String tex, float x, float y, float x2, float y2, float thickness){
		
		Draw.thick(thickness);
		
		//yes that's a lot of draw calls
		//TODO fix this mess
		for(int cx = -1; cx <= 1; cx ++){
			for(int cy = -1; cy <= 1; cy ++){
				Draw.line(region(tex), x + cx*World.size, y + cy*World.size, x2 + cx*World.size, y2 + cy*World.size);
			}
		}
	}
	
	public static void text(String text, float x, float y){
		text(text, x, y, Align.center);
	}
	
	public static void text(String text, float x, float y, int align){
		x = overlapx(x, 80);
		y = overlapy(y, 30);
		
		DrawContext.font.getData().setScale(1f);
		DrawContext.font.draw(batch(), text, x, y, 0, align, false);
	}
	
	private static TextureAtlas.AtlasRegion region(String name){
		return (AtlasRegion)Draw.region(name);
	}
	
	private static OrthographicCamera cam(){
		return DrawContext.camera;
	}
	
	public static Vector2 overlapxPair(float x1, float x2){
		
		float i = (x1 + x2)/2f;
		float r = Math.abs(x1 - x2);
		
		vector.set(x1, x2);
		
		if(MathUtils.isEqual(i, cam().position.x, (cam().viewportWidth * cam().zoom) / 2f + 10f + r)){
			return vector;
		}else{
			if(i < World.size / 2f){
				vector.set(x1 + World.size, x2 + World.size);
			}else{
				vector.set(x1 - World.size, x2 - World.size);
			}
			return vector;
		}
	}

	public static Vector2 overlapyPair(float y1, float y2){
		
		float i = (y1 + y2)/2f;
		float r = Math.abs(y1 - y2);
		
		vector.set(y1, y2);
		
		if(MathUtils.isEqual(i, cam().position.y, (cam().viewportHeight * cam().zoom) / 2f + 10f + r)){
			return vector;
		}else{
			if(i < World.size / 2f){
				vector.set(y1 + World.size, y2 + World.size);
			}else{
				vector.set(y1 - World.size, y2 - World.size);
			}
			return vector;
		}
	}
	
	public static float overlapx(float i, float r){
		if(MathUtils.isEqual(i, cam().position.x, (cam().viewportWidth * cam().zoom) / 2f + 10f + r)){
			return i;
		}else{
			return i < World.size / 2f ? i + World.size : i - World.size;
		}
	}

	public static float overlapy(float i, float r){
		if(MathUtils.isEqual(i, cam().position.y, (cam().viewportHeight * cam().zoom) / 2f + 10 + r)){
			return i;
		}else{
			return i < World.size / 2f ? i + World.size : i - World.size;
		}
	}
}
