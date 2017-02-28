package io.anuke.novi.modules;

import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

import io.anuke.gif.GifRecorder;
import io.anuke.novi.Novi;
import io.anuke.novi.effects.BreakEffect;
import io.anuke.novi.effects.Effect;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.graphics.Shaders;
import io.anuke.novi.modules.World.MapTile;
import io.anuke.novi.modules.World.TileCache;
import io.anuke.novi.utils.WrappedQuadTree;
import io.anuke.ucore.graphics.Atlas;
import io.anuke.ucore.graphics.PixmapUtils;
import io.anuke.ucore.graphics.ShapeUtils;
import io.anuke.ucore.modules.Module;

public class Renderer extends Module<Novi>{
	public static final int GUIscale = 1;
	public static final int scale = 5; //camera zoom/scale
	public float cameraShakeDuration, cameraShakeIntensity, cameraDrag;
	public Network network;
	public SpriteBatch batch; //novi's batch
	public BitmapFont font; //a font for displaying text
	public Matrix4 matrix; // matrix used for rendering gui and other things
	public GlyphLayout layout; // used for getting font bounds
	public OrthographicCamera camera; //a camera, seems self explanatory
	public Atlas atlas; //texture atlas
	public Player player; //player object from ClientData module
	public World world; // world module
	public GifRecorder recorder;
	public CopyOnWriteArrayList<Effect> effects = new CopyOnWriteArrayList<Effect>();
	public boolean debug = true;

	public Renderer(){
		ShapeUtils.region = PixmapUtils.blankTextureRegion();
		matrix = new Matrix4();
		batch = new SpriteBatch();
		atlas = new Atlas(Gdx.files.internal("sprites/Novi.pack"));
		font = new BitmapFont(Gdx.files.internal("fonts/font.fnt"));
		font.setUseIntegerPositions(false);
		layout = new GlyphLayout();
		recorder = new GifRecorder(batch);
		//recorder.setSpeedMultiplier(3f);
		Draw.init(this);
		Shaders.loadAll();
		
		BreakEffect.createChunks();
	}
	
	@Override
	public void init(){
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / scale, Gdx.graphics.getHeight() / scale);
		player = getModule(ClientData.class).player;
		world = getModule(World.class);
		network = getModule(Network.class);
		
		Texture texture = new Texture("cursors/cursor.png");
		texture.getTextureData().prepare();
		
		Cursor cursor = Gdx.graphics.newCursor(texture.getTextureData().consumePixmap(), texture.getWidth()/2, texture.getHeight()/2);
		Gdx.graphics.setCursor(cursor);
	}

	@Override
	public void update(){
		updateCamera();
		batch.setProjectionMatrix(camera.combined); //make the batch use the camera projection
		clearScreen();
		doRender();
		updateCamera();
	}

	void doRender(){
		clearScreen();
		
		renderMap();
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		
		//renderQuadTree(Entities.getSystem(SpatialSystem.class).quadtree);
		Entities.drawAll(player.x, player.y);
		drawEffects();
		/*
		Vector3 v = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
		
		ShapeUtils.line(batch, camera.position.x, camera.position.y, v.x, v.y);
		
		
		Entities.spatial().raycast(camera.position.x, camera.position.y, v.x, v.y, (entity, x, y)->{
			Draw.color(Color.PURPLE);
			Draw.rect("blank", x, y, 4, 4, 0);
			Draw.color(Color.RED);
			Draw.crect("rect", entity.material.rect.x, entity.material.rect.y, entity.material.rect.width, entity.material.rect.height);
		});
		
		Draw.color();
		*/
		batch.end();
		batch.setProjectionMatrix(matrix);
		batch.begin();
		drawDebug();
		batch.end();
		batch.setColor(Color.WHITE);
	}
	
	void drawEffects(){
		for(Effect effect : effects){
			if(effect.update()){
				effects.remove(effect);
			}else{
				effect.draw();
			}
		}
	}
	
	void renderQuadTree(WrappedQuadTree tree){
		if(tree == null) return;
		
		Draw.crect("border", tree.getBounds().x, tree.getBounds().y, tree.getBounds().width, tree.getBounds().height);
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

	public void drawDebug(){
		color(Color.WHITE);
		font.getData().setScale(1f / GUIscale);
		
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
			color(Color.WHITE);
			drawFont(network.initialconnect() ? "Connecting..." : "Failed to connect to server.", ghwidth() / 2, ghheight() / 2);
		}
		
		//recorder.update();

	}

	public void clearScreen(){
		Color clear = Color.SKY.cpy().sub(0.1f, 0.1f, 0.1f, 0f);
		Gdx.gl.glClearColor(clear.r, clear.g, clear.b, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	}

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

	public void resize(int width, int height){
		matrix.setToOrtho2D(0, 0, width / GUIscale, height / GUIscale);
		camera.setToOrtho(false, width / scale, height / scale); //resize camera
	}

	public void shakeCamera(float duration, float intensity){
		if(cameraShakeDuration > 0 && cameraShakeIntensity > intensity) return;
		cameraShakeIntensity = intensity;
		cameraShakeDuration = duration;
		cameraDrag = cameraShakeIntensity / cameraShakeDuration;
	}


	public void zoom(float amount){
		if(camera.zoom + amount < 0 || (camera.zoom + amount) * camera.viewportWidth > World.worldWidthPixels()) return;
		if(camera.zoom < 3 || amount < 0) camera.zoom += amount;
	}

	public GlyphLayout getBounds(String text){
		layout.setText(font, text);
		return layout;
	}

	public void drawFont(String text, float x, float y){
		layout.setText(font, text);
		font.draw(batch, text, x - layout.width / 2, y + layout.height / 2);
	}

	//returns screen width / scale
	public float ghwidth(){
		return Gdx.graphics.getWidth() / GUIscale;
	}

	//returns screen height / scale
	public float ghheight(){
		return Gdx.graphics.getHeight() / GUIscale;
	}

	public void color(Color color){
		batch.setColor(color);
	}

	public void color(float r, float g, float b, float a){
		batch.setColor(new Color(r, g, b, a));
	}

	//utility/shortcut draw method
	public void draw(String region, float x, float y){
		batch.draw(atlas.findRegion(region), x - atlas.regionWidth(region) / 2, y - atlas.regionHeight(region) / 2);
	}

	public void drawc(String region, float x, float y){
		batch.draw(atlas.findRegion(region), x, y);
	}
}
