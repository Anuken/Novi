package io.anuke.novi.modules;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteCache;
import com.badlogic.gdx.utils.Array;

import io.anuke.novi.Novi;
import io.anuke.novi.utils.Draw;
import io.anuke.ucore.UCore;
import io.anuke.ucore.modules.Module;
import io.anuke.ucore.noise.Noise;
import io.anuke.ucore.noise.RidgedPerlin;
import io.anuke.ucore.noise.VoronoiNoise;
import io.anuke.ucore.util.GridMap;

public class World extends Module<Novi>{
	public static final int tileSize = 512;
	public static final int worldScale = 8;
	public static final int size = 128 * tileSize;
	public static final int genRange = 3;
	public static final float mapSpeed = 5f;
	//TODO make map position sync
	public float mapX, mapY;
	private GridMap<MapTile> tiles = new GridMap<MapTile>();
	private Renderer renderer;
	private Color color = new Color();
	private VoronoiNoise noise = new VoronoiNoise(0, (short)0);
	private RidgedPerlin ridge = new RidgedPerlin(0, 1, 0.1f);
	private Array<TileCache> caches = new Array<TileCache>();
	
	public class MapTile{
		public final int cacheid, id;
		
		public MapTile(int cacheid, int id){this.cacheid = cacheid; this.id = id;}
		
		public TileCache getCache(){
			return caches.get(cacheid);
		}
	}
	
	public class TileCache extends SpriteCache{
		public int draws = 0;
		
		public TileCache(){
			super(8000, true);
		}
	}
	
	@Override
	public void init(){
		renderer = getModule(Renderer.class);
		noise.setUseDistance(true);
	}
	
	public MapTile getCache(int x, int y){
		return tiles.get(x, y);
	}
	
	public boolean hasCache(int x, int y){
		return tiles.get(x, y) != null;
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
				
				x = (int)(bound(x*tileSize)/tileSize);
				y = (int)(bound(y*tileSize)/tileSize);
				
				if(tiles.get(x, y) == null){
					generateCache(x,y);
				}
			}
		}
	}
	
	private void generateCache(int x, int y){
		
		if(caches.size == 0){
			caches.add(new TileCache());
		}else if(caches.get(caches.size - 1).draws > 5000){
			caches.add(new TileCache());
		}
		
		TileCache cache = caches.get(caches.size-1);
		
		cache.beginCache();
		
		for(int z = 0; z < 10; z ++)
		for(int cx = 0; cx < tileSize/16; cx ++){
			for(int cy = tileSize/16-1; cy >= 0; cy --){
				int wx = cx + x*(tileSize/16);
				int wy = cy + y*(tileSize/16);
				
				double noise = Noise.nnoise(wx, wy, 16f, 2f);
				double riv = /*.getValue(wx, wy, 0.01f); //*/-1f;
				
				int height = (int)(noise*7)+5;
				
				if(riv >= 0.01) height = 2;
				
				if(z >= height || (z < height-1 && riv < -0.035)) continue;
				
				noise = UCore.round((float)(noise - riv), 0.15f);
				
				float a = height/35.0f;
				
				cache.setColor(0.05f + a, 0.26f + a, 0.05f + a, 0f);
				//cache.setColor(Hue.mix(Color.FOREST, Color.WHITE, (float)(noise)));
				
				if(riv > 0.01) cache.setColor(Color.TAN);
				if(riv > 0.08) cache.setColor(Color.NAVY);
				
				
				cache.add(Draw.region("block"), cx*16, cy*16 + z*6, 16.1f, 26.1f);
				cache.draws += 1;
			}
		}
		
		int out = cache.endCache();
		
		MapTile tile = new MapTile(caches.size-1, out);
		
		tiles.put(x, y, tile);
	}

	public static int worldWidthPixels(){
		return size;
	}

	public static int worldHeightPixels(){
		return size;
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
			return a - (World.size + b);
		}else{
			return -(b - (World.size + a));
		}
	}

	//returns the unsigned wrapped distance from a to b
	public static float uowrapdst(float a, float b){
		return a - (World.size + b);
	}

	//minimum distance from a to b with wrapping
	public static float wdist(float a, float b){
		float ndst = Math.abs(a - b);
		float wdst = Math.abs(uowrapdst(a, b));
		return Math.min(ndst, wdst);
	}

	//wraps crap
	public static float wrap(float i){
		if(i > World.size / 2)
			return World.size - i;
		return i;
	}

	//corrects coord bounds
	public static float bound(float i){
		if(i < 0){
			return World.size + i;
		}else{
			return i % World.size;
		}
	}
}
