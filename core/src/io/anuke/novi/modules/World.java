package io.anuke.novi.modules;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import io.anuke.novi.Novi;
import io.anuke.novi.world.NoviMapRenderer;
import io.anuke.ucore.modules.Module;

public class World extends Module<Novi>{
	public static final int tileSize = 14;
	public static final int worldSize = 300 * tileSize;
	public TiledMap map;

	@Override
	public void init(){
		map = new TmxMapLoader().load("maps/world1.tmx"); //load world 1 map
		//initialize renderer's tiled map renderer with this map
		getModule(Renderer.class).maprenderer = new NoviMapRenderer(map, getModule(Renderer.class).batch);
	}
	//returns world width in pixels
	public int worldWidthPixels(){
		return worldSize;
	}
	
	//returns world width in pixels
	public int worldHeightPixels(){
		return worldSize;
	}
}
