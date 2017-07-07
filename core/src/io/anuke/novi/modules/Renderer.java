package io.anuke.novi.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.DelayedRemovalArray;

import io.anuke.gif.GifRecorder;
import io.anuke.novi.Novi;
import io.anuke.novi.effects.BreakEffect;
import io.anuke.novi.effects.Effect;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.graphics.Shaders;
import io.anuke.novi.graphics.Wrap;
import io.anuke.novi.modules.World.MapTile;
import io.anuke.novi.modules.World.TileCache;
import io.anuke.novi.utils.WrappedQuadTree;
import io.anuke.ucore.core.Draw;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.modules.RendererModule;

public class Renderer extends RendererModule<Novi>{
	public float cameraShakeDuration, cameraShakeIntensity, cameraDrag;
	public Network network;
	public Player player; //player object from ClientData module
	public World world; // world module
	public GifRecorder recorder;
	public DelayedRemovalArray<Effect> effects = new DelayedRemovalArray<Effect>();
	public boolean debug = true;
	public Cursor cursor;

	public Renderer(){
		cameraScale = 2;
		clearColor = Color.SKY.cpy().sub(0.1f, 0.1f, 0.1f, 0f);
		
		atlas = new Atlas("Novi.pack");
		recorder = new GifRecorder(batch);
		Shaders.loadAll();
		
		Texture texture = new Texture("cursors/cursor.png");
		texture.getTextureData().prepare();
		
		cursor = Gdx.graphics.newCursor(texture.getTextureData().consumePixmap(), texture.getWidth()/2, texture.getHeight()/2);
		Gdx.graphics.setCursor(cursor);
	}
	
	@Override
	public void init(){
		BreakEffect.createChunks();
		
		player = getModule(ClientData.class).player;
		world = getModule(World.class);
		network = getModule(Network.class);
	}

	@Override
	public void update(){
		updateCamera();
		
		clearScreen();
		
		renderMap();
		
		Draw.beginCam();
		
		Entities.drawAll(player.x, player.y);
		drawEffects();
		
		Draw.end();
		
		updateCamera();
	}
	
	void drawEffects(){
		effects.begin();
		for(Effect effect : effects){
			if(effect.update()){
				effects.removeValue(effect, true);
			}else{
				effect.draw();
			}
		}
		effects.end();
	}
	
	void renderQuadTree(WrappedQuadTree tree){
		if(tree == null) return;
		
		Wrap.crect("border", tree.getBounds().x, tree.getBounds().y, tree.getBounds().width, tree.getBounds().height);
		renderQuadTree(tree.getBottomLeftChild());
		renderQuadTree(tree.getBottomRightChild());
		renderQuadTree(tree.getTopLeftChild());
		renderQuadTree(tree.getTopRightChild());
	}

	public void renderMap(){
		int range = World.genRange-1;
		
		int camx = (int)(camera.position.x/World.tileSize);
		int camy = (int)(camera.position.y/World.tileSize);
		
		for(int ry = range; ry >= -range; ry --){
			for(int rx = -range; rx <= range; rx ++){
			
				int basex = camx+rx;
				int basey = camy+ry;
				
				int x = (int)(World.bound(basex*World.tileSize)/World.tileSize);
				int y = (int)(World.bound(basey*World.tileSize)/World.tileSize);
				
				if(world.hasCache(x, y)){
					MapTile tile = world.getCache(x, y);
					TileCache cache = tile.getCache();
					
					cache.setProjectionMatrix(camera.combined);
					cache.getTransformMatrix().setToTranslation(
							basex*World.tileSize, 
							basey*World.tileSize, 0);
					cache.begin();
					cache.draw(tile.id);
					cache.end();
				}
			}
		}
	}
	
	//TODO move to UI
	/*
	public void drawDebug(){
		color(Color.WHITE);
		font.getData().setScale(1f / GUIscale);
		
		if(Gdx.input.isKeyJustPressed(Keys.F3)) debug = !debug;
		
		if(debug){
			font.setColor(Color.ORANGE);
			font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond() + "\n" + 
			"Ping: " + (network.client.getReturnTripTime() + Network.ping * 2) + "\n" + 
			"xy: " + (int)player.x + ", " + (int)player.y + "\n" + 
			"Draws: " + batch.totalRenderCalls + "\n" + 
			"Entities: " + Entities.list().size() + "\n" + 
			"Log: " + Novi.getLastMessage()
			, 0, ghheight());
			batch.totalRenderCalls = 0;
		}

		if( !network.connected() || !network.initialconnect()){
			color(0, 0, 0, 0.5f);
			batch.draw(atlas.findRegion("blank"), 0, 0, ghwidth(), ghheight());
			color(Color.WHITE);h
			drawFont(network.initialconnect() ? "Connecting..." : "Failed to connect to server.", ghwidth() / 2, ghheight() / 2);
		}
	}
*/

	void updateCamera(){
		//this is needed to reset the UI viewport.
		HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		camera.position.set(player.x, player.y, 0f);
		shakeCamera();
		camera.update();
	}

	void shakeCamera(){
		if(cameraShakeDuration > 0){
			cameraShakeDuration -= Novi.delta();
			camera.position.x += MathUtils.random( -cameraShakeIntensity, cameraShakeIntensity);
			camera.position.y += MathUtils.random( -cameraShakeIntensity, cameraShakeIntensity);
			cameraShakeIntensity -= cameraDrag * Novi.delta();
		}
	}

	public void shakeCamera(float duration, float intensity){
		if(cameraShakeDuration > 0 && cameraShakeIntensity > intensity) return;
		cameraShakeIntensity = intensity;
		cameraShakeDuration = duration;
		cameraDrag = cameraShakeIntensity / cameraShakeDuration;
	}

	public void zoom(float amount){
		if(camera.zoom + amount < 0) return;
		if(camera.zoom < 2 || amount < 0) camera.zoom += amount;
	}
}
