package io.anuke.novi.entities.effects;

import java.util.HashMap;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.*;

import io.anuke.novi.sprites.*;
import io.anuke.novi.sprites.Layer.LayerType;
import io.anuke.novi.world.Material;

public class BreakEffect extends Effect{
	public static final int cachedchunks = 10;
	private static HashMap<String, Chunk[][]> loadedchunks = new HashMap<String, Chunk[][]>();
	public static final float jaggedness = 2f;
	public int chunkamount = 7;
	private String regionName;
	private transient boolean init;
	private transient ChunkParticle[] chunks;
	private float velocityscl = 4f;
	private Vector2 offset = new Vector2(0, 0);

	{
		lifetime = 220 + MathUtils.random(500);
	}

	public static void createChunks(){
		for(Material mat : Material.values())
			loadChunkType(mat.name());
	}

	private static void loadChunkType(String name){
		Chunk[][] chunklist = new Chunk[cachedchunks][0];
		for(int chunkarraynum = 0;chunkarraynum < cachedchunks;chunkarraynum ++){
			int chunkamount = 3 + MathUtils.random(4);
			Chunk[] chunks = new Chunk[chunkamount];
			TextureRegion region = renderer.atlas.findRegion(name);
			Pixmap regionpixmap = renderer.atlas.getPixmapOf(region);
			chunks = new Chunk[chunkamount];
			float[] angles = new float[chunkamount];
			float last = 0f;
			for(int i = 0;i < chunkamount;i ++){
				last = last + MathUtils.random(360f / chunkamount / 2) + 360f / chunkamount / 2f;
				angles[i] = last;
			}
			for(int i = 0;i < chunkamount;i ++){
				Pixmap pixmap = new Pixmap(region.getRegionWidth(), region.getRegionHeight(), Format.RGBA8888);

				float angle = i == chunkamount - 1 ? 360f : angles[i];
				float lastangle = i == 0 ? 0 : angles[i - 1];
				for(int x = region.getRegionX();x < region.getRegionX() + region.getRegionWidth();x ++){
					for(int y = region.getRegionY();y < region.getRegionY() + region.getRegionHeight();y ++){
						angle += MathUtils.random( -jaggedness, jaggedness);
						lastangle += MathUtils.random( -jaggedness, jaggedness);

						float relx = x - region.getRegionX(), rely = y - region.getRegionY();
						float rawangle = MathUtils.radDeg * MathUtils.atan2(rely - region.getRegionHeight() / 2f, relx - region.getRegionWidth() / 2f);
						float pixangle = rawangle < 0 ? rawangle + 360f : rawangle;

						if( !((pixangle >= lastangle && pixangle <= angle))) continue;

						Color color = new Color(regionpixmap.getPixel(x, y));
						if(Math.random() < 0.1) color.mul(1f, 0.9f, 0.9f, 1f);
						if(Math.random() < 0.01) color.mul(0.6f, 0.6f, 0.6f, 1f);
						pixmap.drawPixel(x - region.getRegionX(), y - region.getRegionY(), Color.rgba8888(color));
					}
				}
				chunks[i] = new Chunk(pixmap, (angle + lastangle) / 2f);
			}
			chunklist[chunkarraynum] = chunks;
		}
		loadedchunks.put(name, chunklist);
	}
	
	static class ChunkParticle{
		private Chunk chunk;
		public Vector2 velocity = new Vector2();
		float x, y, drag = 0.03f, rotation;
		float rotatevelocity = MathUtils.random( -5f, 5f), rotatedrag = 0.99f;
		
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

	public BreakEffect(String region, float velocity){
		this.regionName = region;
		velocityscl = velocity;
	}

	public BreakEffect(String region, Vector2 offset, float velocity){
		this.regionName = region;
		velocityscl = velocity;
		this.offset = offset;
	}

	public BreakEffect(){
	};

	@Override
	public void Draw(){
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
			chunks[i] = new ChunkParticle(chunktex[i], new Vector2(MathUtils.random(velocityscl) + offset.x, MathUtils.random(velocityscl) + offset.y).setAngle(MathUtils.random(360f)));
		}
	}
}
