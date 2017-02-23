package io.anuke.novi.world;

import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.effects.EffectType;
import io.anuke.novi.effects.Effects;
import io.anuke.novi.entities.base.Base;
import io.anuke.novi.graphics.Draw;

public enum Material{
	air, 
	ironblock{
		public void draw(Block block, Base base, int x, int y){
			defaultDraw("ironblock", block, base, x, y);
			
			if(!exists(base, x+1, y))
				defaultDraw("ironblockedge", block, base, x, y, 0);
			if(!exists(base, x, y+1))
				defaultDraw("ironblockedge", block, base, x, y, 90);
			
			Draw.colorl(0.67f);
			
			if(!exists(base, x-1, y))
				defaultDraw("ironblockedge", block, base, x, y, 180);
			if(!exists(base, x, y-1))
				defaultDraw("ironblockedge", block, base, x, y, 270);
			
			Draw.color();
		}
		
		public boolean solid(){
			return false;
		}
	},
	frame{
		public boolean solid(){
			return false;
		}
	}, 
	entity{
		public void draw(Block block, Base base, int x, int y){}
	};

	static public final int blocksize = 16;

	public void destroyEvent(Base base, int x, int y){
		float wx = worldx(base, x, y), wy = worldy(base, x, y);
		
		Effects.effect(EffectType.shockwave, wx, wy);
		Effects.effect(EffectType.explosion, wx, wy);
		Effects.blockbreak(name(), wx, wy, 2.5f, base.velocity);
		
		base.blocks[x][y].setMaterial(Material.frame);
		
		Effects.shake(30f, 10f, worldx(base, x, y), worldy(base, x, y));
	}
	
	public void draw(Block block, Base base, int x, int y){
		
		Draw.colorl(block.healthfrac()/2f + 0.5f);
		
		defaultDraw(name(), block, base, x, y);
		
		Draw.color();
	}
	
	public boolean exists(Base base, int x, int y){
		if(x >= base.size || y >= base.size || x < 0 || y < 0 ) return false;
		return base.blocks[x][y].getMaterial() == Material.ironblock || base.blocks[x][y].getMaterial() == Material.entity || base.blocks[x][y].getMaterial().solid();
	}

	public void defaultDraw(String region, Block block, Base base, int x, int y){
		Draw.rect(region, worldx(base, x, y), worldy(base, x, y), base.rotation);
	}
	
	public void defaultDraw(String region, Block block, Base base, int x, int y, float rotation){
		Draw.rect(region, worldx(base, x, y), worldy(base, x, y), rotation);
	}

	public void defaultDraw(String region, Block block, Base base, int x, int y, float offsetx, float offsety){
		Draw.rect(region, worldx(base, x, y) + offsetx, worldy(base, x, y) + offsety, base.rotation);
	}

	public void defaultDraw(String region, Block block, Base base, int x, int y, boolean damage){
		
		if(damage) Draw.color(block.healthfrac() + 0.3f, block.healthfrac() + 0.3f, block.healthfrac() + 0.3f);
		defaultDraw(region, block, base, x, y);
		if(damage) Draw.color();
	}

	float worldx(Base base, int x, int y){
		Vector2 v = base.world(x, y);
		return v.x;
	}

	float worldy(Base base, int x, int y){
		Vector2 v = base.world(x, y);
		return v.y;
	}

	public boolean solid(){
		return false;
	}
	
	public boolean drawTop(){
		return solid();
	}

	public int health(){
		return 10;
	}
}
