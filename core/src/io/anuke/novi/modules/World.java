package io.anuke.novi.modules;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Disposable;

import io.anuke.novi.Novi;
import io.anuke.ucore.graphics.Hue;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.noise.Noise;
import io.anuke.ucore.noise.VoronoiNoise;
import io.anuke.ucore.util.GridMap;

public class World extends Module<Novi>{
	public static final int tileSize = 256;
	public static final int worldSize = 128 * tileSize;
	public static final int genRange = 3;
	private GridMap<MapTile> tiles = new GridMap<MapTile>();
	private Renderer renderer;
	private Color color = new Color();
	private VoronoiNoise noise = new VoronoiNoise(0, (short)0);
	
	public class MapTile implements Disposable{
		public Texture texture;
		public Pixmap pixmap;
		
		public MapTile(Texture texture, Pixmap pixmap){
			this.texture = texture;
			this.pixmap = pixmap;
		}
		
		public void dispose(){
			texture.dispose();
			pixmap.dispose();
		}
	}
	
	public MapTile getTile(int x, int y){
		return tiles.get(x, y);
	}

	@Override
	public void init(){
		
		renderer = getModule(Renderer.class);
		noise.setUseDistance(true);
		//Pixmap pixmap = new Pixmap(worldSize/2, worldSize/2, Format.RGB888);
	}
	
	@Override
	public void update(){
		
		int range = genRange;
		
		int camx = (int)(renderer.camera.position.x/tileSize);
		int camy = (int)(renderer.camera.position.y/tileSize);
		
		for(int rx = -range; rx <= range; rx ++){
			for(int ry = -range; ry <= range; ry ++){
				int x = camx+rx;
				int y = camy+ry;
				
				x = (int)(World.bound(x*tileSize)/tileSize);
				y = (int)(World.bound(y*tileSize)/tileSize);
				
				if(getTile(x, y) == null){
					generateTile(x,y);
				}
			}
		}
	}
	
	private void generateTile(int x, int y){
		Pixmap pixmap = new Pixmap(tileSize, tileSize, Format.RGBA8888);
		
		for(int px = 0; px < tileSize; px ++){
			for(int py = 0; py < tileSize; py ++){
				
				getColor(x*tileSize+px, y*tileSize+py);
				
				pixmap.drawPixel(px, tileSize -1 -py, Color.rgba8888(color));
			}
		}
		
		Texture texture = new Texture(pixmap);
		
		tiles.put(x, y, new MapTile(texture, pixmap));
	}
	
	public void getColor(int x, int y){
		double noise = 
				Noise.nnoise(x, y, 500f, 1f)+
				Noise.nnoise(x, y, 150f,0.5f)
				+0.35f;
		
		noise = (int)(noise/0.2f)*0.2f;
				
		Hue.mix(Color.FOREST, Color.WHITE, (float)noise, color);
	}

	public static int worldWidthPixels(){
		return worldSize;
	}

	public static int worldHeightPixels(){
		return worldSize;
	}

	//square distance with wrapping
	public static boolean loopedWithin(float x1, float x2, float y1, float y2, float rad){
		return(Math.abs(relative3(x1, x2)) < rad && Math.abs(relative3(y1, y2)) < rad);
	}

	//wrapped normal distance
	public static float wrappedDist(float x1, float y1, float x2, float y2){
		float ydst = relative3(y1, y2);
		float xdst = relative3(x1, x2);
		return (float) Math.sqrt(ydst * ydst + xdst * xdst);
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
		if(i > World.worldSize / 2)
			return World.worldSize - i;
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
