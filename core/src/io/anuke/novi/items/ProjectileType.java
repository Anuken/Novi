package io.anuke.novi.items;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novi.entities.combat.Bullet;
import io.anuke.novi.entities.combat.DamageArea;
import io.anuke.novi.entities.effects.Effects;
import io.anuke.novi.entities.effects.ExplosionEffect;
import io.anuke.novi.entities.effects.Shockwave;
import io.anuke.novi.modules.Renderer;
import io.anuke.ucore.graphics.Hue;

public enum ProjectileType{
	yellowbullet{
		
		public String drawName(){
			return "bullet";
		}
		
		public int getLifetime(){
			return 30;
		}
		
		public float getSpeed(){
			return 7;
		}
		
		public int damage(){
			return 20;
		}
	},
	redbullet{
		
		public String drawName(){
			return "dronebullet";
		}
		
		public int getLifetime(){
			return 60;
		}
		
		public float getSpeed(){
			return 3;
		}
		
		public int damage(){
			return 4;
		}
	},
	explosivebullet{
		
		public int getLifetime(){
			return 100;
		}
		
		public float getSpeed(){
			return 3;
		}
		
		public void destroyEvent(Bullet bullet){
			new Shockwave(8f, 0.001f, 0.02f).set(bullet.x, bullet.y).send();
			new ExplosionEffect().set(bullet.x, bullet.y).send();
			new DamageArea(30f, 16f).set(bullet.x, bullet.y).add();
			Effects.shake(20f, 10f, bullet.x, bullet.y);
		}
		
		public void setup(Bullet bullet){
			bullet.material.getRectangle().setSize(5f);
		}
		
		public int damage(){
			return 15;
		}
	},
	mine{
		public void draw(Bullet bullet, Renderer renderer){
			defaultDraw(bullet, renderer);
			renderer.layer("minecenter",bullet.x,bullet.y).setLayer(0.51f).setRotation(bullet.velocity.angle() - 90)
			.setColor(Hue.blend(Color.GOLD, Color.RED, bullet.life()/getLifetime()));
		}
		
		public int getLifetime(){
			return 200;
		}
		
		public float getSpeed(){
			return 0;
		}
		
		public void destroyEvent(Bullet bullet){
			//new Shockwave(8f, 0.001f, 0.02f).setPosition(bullet.x, bullet.y).SendSelf();
			//new ExplosionEffect().setPosition(bullet.x, bullet.y).SendSelf();
			new DamageArea(30f, 16f).setDamage(15).set(bullet.x, bullet.y).add();
			Effects.explosionCluster(bullet.x, bullet.y, 4, 5f);
			Effects.shake(20f, 10f, bullet.x, bullet.y);
		}
		
		public void setup(Bullet bullet){
			bullet.material.getRectangle().setSize(10f);
		}
		
		public int damage(){
			return 5;
		}
		
		public boolean collideWithBases(){
			return false;
		}
	};
	
	public void setup(Bullet bullet){
		
	}
	
	public float getSpeed(){
		return 4;
	}
	
	public int getLifetime(){
		return 100;
	}
	
	public int damage(){
		return 1;
	}
	
	public boolean collideWithOtherProjectiles(){
		return false;
	}
	
	public boolean collide(){
		return true;
	}
	
	public boolean collideWithBases(){
		return true;
	}
	
	public String drawName(){
		return name();
	}
	
	public void destroyEvent(Bullet bullet){
		
	}
	
	public Layer defaultDraw(Bullet bullet, Renderer renderer){
		return renderer.layer(drawName(), bullet.x, bullet.y).setLayer(0.5f).setRotation(bullet.velocity.angle() - 90).addShadow();
	}
	
	public void draw(Bullet bullet, Renderer renderer){
		renderer.layer(drawName(), bullet.x, bullet.y).setLayer(0.5f).setRotation(bullet.velocity.angle() - 90).addShadow();
	}
}
