package io.anuke.novi.entities.enemies;

import static io.anuke.novi.modules.World.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.function.Consumer;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.entities.base.Player;
import io.anuke.novi.entities.combat.Bullet;
import io.anuke.novi.entities.combat.Damager;
import io.anuke.novi.entities.effects.*;
import io.anuke.novi.network.BaseSyncData;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.utils.Draw;
import io.anuke.novi.utils.InterpolationData;
import io.anuke.novi.world.Block;
import io.anuke.novi.world.Material;
import io.anuke.ucore.util.Angles;

public abstract class Base extends Enemy implements Syncable{
	public transient int size = 10;
	private transient Rectangle rectangle = new Rectangle(0, 0, Material.blocksize, Material.blocksize);
	public transient float rotation;
	public Block[][] blocks;
	public transient int spawned;
	protected transient String texture = null;
	private transient InterpolationData data = new InterpolationData();
	private transient boolean collided = false;

	public Base() {
		if(NoviServer.active()){
			health = Integer.MAX_VALUE;
			blocks = new Block[size][size];
			for(int x = 0; x < size; x++){
				for(int y = 0; y < size; y++){
					blocks[x][y] = new Block(x, y, Material.air);
				}
			}
			generateBlocks();
			material.getRectangle().setSize(size * (Material.blocksize + 1), size * (Material.blocksize + 1));
		}
	}

	abstract void generateBlocks();

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

	@Override
	public boolean collides(SolidEntity other){
		if(!(other instanceof Damager) || (other instanceof Bullet && !(((Bullet) other).shooter instanceof Player)))
			return false;
		Point point = blockPosition(other.x, other.y);
		
		collided = false;
		
		radiusBlocks(point.x, point.y, (block)->{
			Vector2 vector = world(block.x, block.y);
			rectangle.setCenter(bound(vector.x), bound(vector.y));

			if(other.collides(rectangle)){
				block.health -= ((Damager) other).damage();
				checkHealth(block, vector);
				updateBlock(block.x, block.y);
				Effects.explosion(vector.x, vector.y);
				collided = true;
			}
		});
		
		if(collided)
			updateHealth();
		return collided;
	}

	public void checkHealth(Block block, Vector2 pos){
		if(block.health < 0){
			block.getMaterial().destroyEvent(this, block.x, block.y);
			new ExplosionEmitter(10f, 1f, 14f).set(pos.x, pos.y).add();
			explosion(block.x, block.y);
		}
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

	public Point blockPosition(float x, float y){
		x = relative3(x, this.x);
		y = relative3(y, this.y);
		Vector2 v = Angles.rotate(x, y, -rotation);
		float relx = (v.x - Material.blocksize / 2f + unitSize() / 2f) / Material.blocksize;
		float rely = (v.y - Material.blocksize / 2f + unitSize() / 2f) / Material.blocksize;
		return new Point((int) relx, (int) rely);
	}

	public void onDeath(){
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				if(!blocks[x][y].empty()){
					Vector2 v = world(x, y);
					new BreakEffect(blocks[x][y].material.name()).set(v.x, v.y).send();
				}
			}
		}
		
		if(this.removeOnDeath()) removeServer();

		if(texture != null)
			new BreakEffect(texture, 2f, this.rotation).set(x, y).send();
		new ExplosionEmitter(120, 1.1f, size * Material.blocksize / 2f).set(x, y).add();
		new Shockwave().set(x, y).send();
		Effects.shake(80f, 40f, x, y);
	}
	
	@Override
	public void behaviorUpdate(){
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				Block block = blocks[x][y];
				block.updated = false;
				
				if(block.empty())
					continue;
				block.getMaterial().update(block, this);
			}
		}
	}

	@Override
	public void draw(){
		data.update(this);

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

		if(texture != null)
			Draw.rect(texture, x, y, rotation);
	}

	@Override
	public SyncData writeSync(){
		ArrayList<BlockUpdate> updates = new ArrayList<BlockUpdate>();
		for(int x = 0; x < size; x++){
			for(int y = 0; y < size; y++){
				Block block = blocks[x][y];
				if(!block.updated) continue;
				
				updates.add(new BlockUpdate(block));
			}
		}
		return new BaseSyncData(updates, rotation, x, y);
	}

	@Override
	public void readSync(SyncData buffer){
		this.rotation = ((BaseSyncData) buffer).rotation;
		data.push(this, buffer.x, buffer.y, 0f);
		for(BlockUpdate update : ((BaseSyncData) buffer).updates){
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
		float relx = (x * Material.blocksize - size / 2f * Material.blocksize);
		float rely = (y * Material.blocksize - size / 2f * Material.blocksize);
		Vector2 v = Angles.rotate(relx, rely, rotation);
		v.add(this.x, this.y);
		return v;
	}
	
	public static class BlockUpdate{
		float rotation, reload;
		int x, y, health;
		Material material;
		
		private BlockUpdate(){}
		
		public BlockUpdate(Block block){
			this.rotation = block.rotation;
			this.reload = block.reload;
			this.x = block.x;
			this.y = block.y;
			this.health = block.health;
			this.material = block.getMaterial();
		}
		
		public void apply(Block[][] blocks){
			Block b = blocks[x][y];
			b.reload = this.reload;
			b.health = this.health;
			b.rotation = this.rotation;
			b.material = this.material;
		}
	}
}
