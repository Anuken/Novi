package io.anuke.novi.entities.effects;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import io.anuke.novi.sprites.Layer;
import io.anuke.novi.sprites.Layer.LayerType;
import io.anuke.novi.world.Material;
import io.anuke.ucore.noise.VoroniNoise;

public class BreakEffect extends Effect{
	public static final int cachedchunks = 10;
	private static HashMap<String, Chunk[][]> loadedchunks = new HashMap<String, Chunk[][]>();
	private static VoroniNoise noise = new VoroniNoise(1, (short)0);
	
	private String regionName;
	private transient boolean init;
	private transient ChunkParticle[] chunks;
	private float velocityscl = 2f;
	private float rotation;
	private Vector2 velocityoffset = new Vector2(0, 0);

	{
		lifetime = 420 + MathUtils.random(500);
	}

	public static void createChunks(){
		for(Material mat : Material.values())
			loadChunkType(mat.name());
		
		loadChunkType("titanship");
	}

	private static void loadChunkType(String name){
		Chunk[][] chunklist = new Chunk[cachedchunks][0];
		
		for(int chunkarraynum = 0; chunkarraynum < cachedchunks; chunkarraynum ++){
			
			ObjectMap<Double, Pixmap> pixmaps = new ObjectMap<Double, Pixmap>();
			
			TextureRegion region = renderer.atlas.findRegion(name);
			Pixmap regionpixmap = renderer.atlas.getPixmapOf(region);
			noise.setSeed(MathUtils.random(9999));
			
			for(int x = 0; x < region.getRegionWidth(); x ++){
				for(int y = 0; y < region.getRegionHeight(); y ++){
					//Pixmap pixmap = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Format.RGBA8888);
					
					if(regionpixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y) == 0){
						continue;
					}
					
					double n = noise.noise(x, y, 0.14 - region.getRegionWidth()/600f);
					
					Pixmap pixmap = null;
					
					if(pixmaps.containsKey(n)){
						pixmap = pixmaps.get(n);
					}else{
						pixmap = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Format.RGBA8888);
						pixmaps.put(n, pixmap);
					}
					
					pixmap.drawPixel(x, y, regionpixmap.getPixel(region.getRegionX() + x, region.getRegionY() + y));
				}
			}
			Array<Chunk> chunks = new Array<Chunk>();
			for(Pixmap pixmap : pixmaps.values()){
				chunks.add(new Chunk(pixmap, 0));
			}
			
			chunklist[chunkarraynum] = chunks.toArray(Chunk.class);
		}
		loadedchunks.put(name, chunklist);
	}
	
	static class ChunkParticle{
		private Chunk chunk;
		public Vector2 velocity = new Vector2();
		float x, y, drag = 0.03f, rotation;
		float rotatevelocity = MathUtils.random( -0.5f, 0.5f), rotatedrag = 0.99f;
		
		public void draw(BreakEffect effect){
			Layer layer = renderer.layer(x + effect.x, y + effect.y).setType(LayerType.TEXTURE).setLayer(1.5f).setRotation(rotation).setTexture(chunk.region);
			float scl = 6f;
			if(effect.life > effect.lifetime / scl) layer.setColor(new Color(1, 1, 1, 1f - (effect.life - effect.lifetime / 2f) / (effect.lifetime / scl)));
			layer.addShadow();
			x += velocity.x * delta();
			y += velocity.y * delta();
			velocity.scl((float)Math.pow(1f - drag, delta()));
			rotation += rotatevelocity * delta();
			rotatevelocity = rotatedrag * rotatevelocity;
		}
		
		public ChunkParticle(Chunk chunk, Vector2 velocity){
			this.chunk = chunk;
			this.velocity = velocity;
		}
	}

	static class Chunk{
		public final float initialangle;
		private Pixmap pixmap;
		private TextureRegion region;

		public Chunk(Pixmap pixmap, float angle){
			this.pixmap = pixmap;
			region = new TextureRegion(new Texture(pixmap));
			initialangle = angle;
		}

		public void dispose(){
			region.getTexture().dispose();
			pixmap.dispose();
		}
	}

	public BreakEffect(String region){
		this.regionName = region;
	}

	public BreakEffect(String region, float velocity, float rotation){
		this.regionName = region;
		velocityscl = velocity;
	}

	public BreakEffect(String region, Vector2 offset, float velocity){
		this.regionName = region;
		velocityscl = velocity;
		this.velocityoffset = offset;
	}

	public BreakEffect(){
	};

	@Override
	public void draw(){
		if( !init){
			init();
			init = true;
		}
		
		for(ChunkParticle chunk : chunks){
			chunk.draw(this);
		}
	}

	public void init(){
		if( !loadedchunks.containsKey(regionName)) loadChunkType(regionName);
		Chunk[][] chunklist = loadedchunks.get(regionName);
		Chunk[] chunktex = chunklist[MathUtils.random(chunklist.length-1)];
		chunks = new ChunkParticle[chunktex.length];
		
		for(int i = 0; i < chunks.length ; i ++){
			chunks[i] = new ChunkParticle(chunktex[i], new Vector2(MathUtils.random(velocityscl) + velocityoffset.x, MathUtils.random(velocityscl) + velocityoffset.y).setAngle(MathUtils.random(360f)));
			chunks[i].rotation = this.rotation;
		}
	}
}
