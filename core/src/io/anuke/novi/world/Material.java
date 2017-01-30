package io.anuke.novi.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.effects.*;
import io.anuke.novi.entities.enemies.Base;
import io.anuke.novi.entities.enemies.Drone;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.sprites.Layer;
import io.anuke.novi.utils.Angles;

public enum Material{
	air, ironblock{
		public boolean solid(){
			return false;
		}
	},
	dronemaker{
		static final float buildtime = 60;
		static final int maxspawn = 20;

		public boolean solid(){
			return true;
		}

		public void draw(Block block, Base base, int x, int y){
			defaultDraw("dronemaker", block, base, x, y, false);
			defaultDraw("drone", block, base, x, y, false).setColor(new Color(1, 1, 1, block.reload / buildtime)).rotate(180f).setLayer( -0.45f).translate(0, -1);
			Layer bar = defaultDraw("dronemakerbar", block, base, x, y, false).setLayer( -0.4f);
			Vector2 v = Angles.translation(base.rotation + 90f, (int)(Math.sin(Gdx.graphics.getFrameId() / 10f) * 6f - 1f));
			bar.translate(v.x, v.y);
			//bar.translate(bar.x, bar.y + (int)(Math.sin(Gdx.graphics.getFrameId() / 10f) * 6f - 1f));
		}

		public void update(Block block, Base base){
			if(base.target == null || base.spawned > maxspawn) return;
			block.reload += Entity.delta();
			base.update(block.x, block.y);
			if(block.reload >= buildtime){
				Drone drone = (Drone)new Drone().setPosition(worldx(base, block.x, block.y), worldy(base, block.x, block.y));
				drone.velocity.y = -3;
				drone.addSelf().sendSelf();
				drone.base = base;
				block.reload = 0;
				base.spawned ++;
			}
		}

		public int health(){
			return 60;
		}
	},
	turret{
		static final float reloadtime = 40;

		public boolean solid(){
			return true;
		}

		public void update(Block block, Base base){
			if(base.target != null){
				block.rotation = base.autoPredictTargetAngle(worldx(base, block.x, block.y), worldy(base, block.x, block.y), 4f) + 90;
				base.update(block.x, block.y);
				block.reload += Entity.delta();
				if(block.reload >= reloadtime){
					base.getShoot(ProjectileType.redbullet, block.rotation + 90).setPosition(worldx(base, block.x, block.y), worldy(base, block.x, block.y)).translate(3, 5).addSelf().sendSelf();;
					base.getShoot(ProjectileType.redbullet, block.rotation + 90).setPosition(worldx(base, block.x, block.y), worldy(base, block.x, block.y)).translate( -3, 5).addSelf().sendSelf();;

					block.reload = 0;
				}
			}
		}

		public void draw(Block block, Base base, int x, int y){
			defaultDraw("ironblock", block, base, x, y, false);
			defaultDraw("turret", block, base, x, y, false).setRotation(block.rotation).setLayer( -0.5f);
		}

		public int health(){
			return 40;
		}
	},
	bigturret{
		static final float reloadtime = 100;

		public boolean solid(){
			return true;
		}

		public void update(Block block, Base base){
			if(base.target != null){
				block.rotation = MathUtils.lerpAngleDeg(block.rotation, base.autoPredictTargetAngle(worldx(base, block.x, block.y), worldy(base, block.x, block.y), 3f) + 90, 0.02f);
				base.update(block.x, block.y);
				block.reload += Entity.delta();
				if(block.reload >= reloadtime){
					base.getShoot(ProjectileType.explosivebullet, block.rotation + 90).setPosition(worldx(base, block.x, block.y), worldy(base, block.x, block.y)).translate(0, 7).addSelf().sendSelf();;
					block.reload = 0;
				}
			}
		}

		public void draw(Block block, Base base, int x, int y){
			defaultDraw("ironblock", block, base, x, y, false);
			Vector2 vector = Angles.translation(block.rotation - 90, (1f - block.reload / reloadtime) * 4f);
			defaultDraw("bigturret", block, base, x, y, vector.x, vector.y).setRotation(block.rotation).setLayer( -0.5f);
		}

		public int health(){
			return 100;
		}
	};

	public void update(Block block, Base base){

	}

	static public final int blocksize = 14;

	public void destroyEvent(Base base, int x, int y){
		new Shockwave(8f, 0.001f, 0.02f).setPosition(worldx(base, x, y), worldy(base, x, y)).sendSelf();
		new ExplosionEffect().setPosition(worldx(base, x, y), worldy(base, x, y)).sendSelf();
		new BreakEffect(name(), base.velocity, 2.5f).setPosition(worldx(base, x, y), worldy(base, x, y)).sendSelf();
		base.blocks[x][y].setMaterial(Material.ironblock);
		Effects.shake(40f, 15f, worldx(base, x, y), worldy(base, x, y));
	}

	public void draw(Block block, Base base, int x, int y){
		defaultDraw(name(), block, base, x, y);
	}

	public Layer defaultDraw(String region, Block block, Base base, int x, int y){
		return Entity.renderer.layer(region, worldx(base, x, y), worldy(base, x, y)).setRotation(base.rotation).setLayer( -1f).setColor(new Color(block.healthfrac() + 0.3f, block.healthfrac() + 0.3f, block.healthfrac() + 0.3f, 1f)).addShadow();
	}

	public Layer defaultDraw(String region, Block block, Base base, int x, int y, float offsetx, float offsety){
		return defaultDraw(region, block, base, x, y, false).translate(offsetx, offsety);
	}

	public Layer defaultDraw(String region, Block block, Base base, int x, int y, boolean damage){
		Layer layer = defaultDraw(region, block, base, x, y);
		if(damage){
			layer.setColor(new Color(block.healthfrac() + 0.3f, block.healthfrac() + 0.3f, block.healthfrac() + 0.3f, 1f));
		}else{
			layer.setColor(Color.WHITE);
		}
		return layer;
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

	public int health(){
		return 10;
	}
}
