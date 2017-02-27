package io.anuke.novi.entities.base;

import static io.anuke.novi.modules.World.relative3;

import java.util.ArrayList;
import java.util.function.Consumer;

import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.LongArray;

import io.anuke.novi.Novi;
import io.anuke.novi.effects.BreakEffect;
import io.anuke.novi.effects.EffectType;
import io.anuke.novi.effects.Effects;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Markable;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.entities.enemies.Enemy;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.modules.Network;
import io.anuke.novi.modules.World;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.ui.Landmark;
import io.anuke.novi.utils.InterpolationData;
import io.anuke.novi.world.Block;
import io.anuke.novi.world.Material;
import io.anuke.ucore.util.Angles;

public abstract class Base extends Enemy implements Syncable, Markable{
	private static ArrayList<BlockUpdate> updates = new ArrayList<BlockUpdate>();
	private static GridPoint2 point = new GridPoint2();
	private static Rectangle rectangle = new Rectangle(0, 0, Material.blocksize, Material.blocksize);
	
	public boolean enemy = true;
	public transient int size = 7;
	public transient float rotation;
	public Block[][] blocks;
	public transient int spawned;
	protected LongArray blockentities = new LongArray();
	protected transient String texture = null;
	private transient InterpolationData data = new InterpolationData();
	private transient boolean collided = false;

	public Base(){
		material.set(size * (Material.blocksize + 1));
		material.updateHitbox();
		
		if(NoviServer.active()){
			health = Integer.MAX_VALUE;
			blocks = new Block[size][size];
			for(int x = 0; x < size; x++){
				for(int y = 0; y < size; y++){
					blocks[x][y] = new Block(x, y, Material.air);
				}
			}
			generateBlocks();
		}
	}
	
	@Override
	public Base set(float x, float y){
		this.x = x;
		this.y = y;
		material.updateHitbox();
		
		if(material.rect.x < 0)
			this.x -= (material.rect.x - Material.blocksize*2);
		
		if(material.rect.y < 0)
			this.y -= (material.rect.y - Material.blocksize*2);
		
		if(material.rect.x + material.rect.width > World.size)
			this.x -= (material.rect.x + material.rect.width - World.size) - Material.blocksize;
		
		if(material.rect.y + material.rect.height > World.size)
			this.y -= (material.rect.y + material.rect.height - World.size) - Material.blocksize;
		
		return this;
	}

	@Override
	public boolean collides(SolidEntity other){
		return false;
	}
	
	void updateHealth(){
		int health = 0;
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				if(blocks[x][y].solid())
					health += blocks[x][y].getMaterial().health();
			}
		}
		
		if(health <= 0){ 
			this.onDeath();
		}
	}

	public void checkHealth(Block block, Vector2 pos){
		if(block.health < 0){
			block.getMaterial().destroyEvent(this, block.x, block.y);
			Effects.effect(EffectType.explosion, pos.x, pos.y);
			explosion(block.x, block.y);
		}
	}
	
	abstract void generateBlocks();
	
	protected void addEntity(BaseBlock block, int x, int y){
		if(blocks[x][y].material == Material.entity){
			//TODO error message?
			return;
		}
		
		block.setBase(this, x, y);
		blocks[x][y].setMaterial(Material.entity);
		block.add().send();
		blockentities.add(block.getID());
	}
	
	protected void onBlockDestroyed(BaseBlock block){
		int x = block.blockx;
		int y = block.blocky;
		blockentities.removeValue(block.getID());
		blocks[x][y].setMaterial(Material.frame);
		explosion(x, y);
	}
	
	//makes an explosion
	public void explosion(int cx, int cy){
		int rad = 3;
		for(int x = -rad; x <= rad; x++){
			for(int y = -rad; y <= rad; y++){
				int relx = cx + x, rely = cy + y;
				if(relx < 0 || rely < 0 || relx >= size || rely >= size)
					continue;
				float dist = Vector2.dst(x, y, 0, 0);
				if(dist >= rad)
					continue;
				Block block = blocks[relx][rely];
				
				if(block.material == Material.air) continue;
				
				if(block.material != Material.frame)
				block.health -= (int) ((1f - dist / rad + 0.1f) * block.getMaterial().health());
				
				if(block.health < 0)
					block.getMaterial().destroyEvent(this, relx, rely);
				updateBlock(relx, rely);
			}
		}
	}

	public void radiusBlocks(int cx, int cy, Consumer<Block> cons){
		int rad = 1;
		for(int x = -rad; x <= rad; x++){
			for(int y = -rad; y <= rad; y++){
				int relx = cx + x, rely = cy + y;
				if(relx < 0 || rely < 0 || relx >= size || rely >= size)
					continue;
				if(blocks[relx][rely].solid())
					cons.accept(blocks[relx][rely]);
			}
		}
	}

	//updates a block at x,y so it gets synced
	public void updateBlock(int x, int y){
		blocks[x][y].updated = true;
	}

	public Block getBlockAt(float x, float y){
		float relativex = x - this.x + size / 2f * Material.blocksize, relativey = y - this.y + size / 2f * Material.blocksize;
		int blockx = (int) (relativex / Material.blocksize), blocky = (int) (relativey / Material.blocksize);
		if(blockx < 0 || blocky < 0 || blockx >= size || blocky >= size)
			return null;
		return blocks[blockx][blocky];
	}

	public GridPoint2 blockPosition(float x, float y){
		x = relative3(x, this.x);
		y = relative3(y, this.y);
		Vector2 v = Angles.rotate(x, y, -rotation);
		float relx = (v.x - Material.blocksize / 2f + unitSize() / 2f) / Material.blocksize;
		float rely = (v.y - Material.blocksize / 2f + unitSize() / 2f) / Material.blocksize;
		return point.set((int) relx, (int) rely);
	}

	public void onDeath(){
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				if(!blocks[x][y].empty()){
					Vector2 v = world(x, y);
					new BreakEffect(blocks[x][y].material.name()).set(v.x, v.y).add();
				}
			}
		}
		
		if(this.removeOnDeath()) removeServer();

		if(texture != null)
			new BreakEffect(texture, 2f, this.rotation).set(x, y).add();
		
		float radius = size * Material.blocksize / 1.8f;
		
		for(int i = 0; i < 70; i ++)
			Effects.effect(EffectType.explosion, x + MathUtils.random(-radius, radius), y + MathUtils.random(-radius, radius), MathUtils.random(180));
		
		Effects.effect(EffectType.shockwave, x, y);
		Effects.shake(80f, 40f, x, y);
	}
	
	@Override
	public void update(){
		super.update();
		for(int i = 0; i < blockentities.size; i ++){
			long l = blockentities.get(i);
			
			BaseBlock entity = (BaseBlock)Entities.get(l);
			
			if(entity == null){
				if(!NoviServer.active()){
					Novi.module(Network.class).requestEntity(l);
				}
				continue;
			}
			
			Vector2 vector = world(entity.blockx, entity.blocky);
			
			entity.set(vector.x, vector.y);
		}
	}
	
	@Override
	public void behaviorUpdate(){
		updates.clear();
		
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				Block block = blocks[x][y];
				
				if(block.updated){
					updates.add(new BlockUpdate(block));
					block.updated = false;
				}
				
				if(block.empty())
					continue;
			}
		}
		
		if(updates.size() != 0){
			sendEvent(this, updates.toArray());
		}
	}

	@Override
	public void draw(){
		data.update(this);
		
		if(texture != null)
			Draw.rect(texture, x, y, rotation);
		
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				Block block = blocks[x][y];
				if(block.empty())
					continue;
				
				if(!block.getMaterial().drawTop()){
					block.getMaterial().draw(block, this, x, y);
				}else{
					Material.ironblock.draw(block, this, x, y);
				}
			}
		}

		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				Block block = blocks[x][y];
				if(block.empty() || !block.getMaterial().drawTop())
					continue;
				block.getMaterial().draw(block, this, x, y);
			}
		}
		
		material.updateHitbox();
	}
	
	@Override
	public void handleEvent(Object[] data){
		for(Object o : data){
			BlockUpdate update = (BlockUpdate)o;
			update.apply(blocks);
		}
	}

	@Override
	public SyncData writeSync(){
		updates.clear();
		return new SyncData(getID(), x, y, rotation, updates);
	}

	@Override
	public void readSync(SyncData in){
		this.rotation = in.get(2);
		
		data.push(this, in.get(0), in.get(1), 0f);
		
		ArrayList<BlockUpdate> updates = in.get(3);
		
		for(BlockUpdate update : updates){
			update.apply(blocks);
		}
	}

	public void setTexture(String texture){
		this.texture = texture;
	}

	public float unitSize(){
		return Material.blocksize * size;
	}

	public Vector2 world(int x, int y){
		float relx = (x * Material.blocksize - size / 2f * Material.blocksize + Material.blocksize/2f);
		float rely = (y * Material.blocksize - size / 2f * Material.blocksize + Material.blocksize/2f);
		Vector2 v = Angles.rotate(relx, rely, rotation);
		v.add(this.x, this.y);
		return v;
	}
	
	public boolean targetPlayer(){
		return false;
	}
	
	public static class BlockUpdate{
		float rotation, reload;
		int x, y, health;
		Material material;
		
		private BlockUpdate(){}
		
		public BlockUpdate(Block block){
			this.x = block.x;
			this.y = block.y;
			this.health = block.health;
			this.material = block.getMaterial();
		}
		
		public void apply(Block[][] blocks){
			Block b = blocks[x][y];
			b.health = this.health;
			b.material = this.material;
		}
	}
	
	@Override
	public Landmark getLandmark(){
		return Landmark.enemybase;
	}
}
