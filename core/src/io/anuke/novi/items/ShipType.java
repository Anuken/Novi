package io.anuke.novi.items;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novi.entities.basic.Player;
import io.anuke.novi.server.InputHandler;
import io.anuke.novi.utils.Draw;

public enum ShipType{
	striker{
		final float reload2 = 3f;
		
		{
			speed = 0.2f;
			turnspeed = 12f;
			maxvelocity = 5f;
			shootspeed = 4;
			kiteDebuffMultiplier = 0.5f;
			maxHealth = 300;
			trailColor = Color.valueOf("7cd99bff");
		}
		
		public void handleInput(Player player, InputHandler input){
			player.shooting = input.leftMouseDown();
			if(input.leftMouseDown() && player.reload <= 0){
				player.shootBullet(ProjectileType.plasmabullet);
				player.reload = player.getShip().getShootspeed();
			}
			
			if(input.rightMouseDown() && player.altreload <= 0){
				player.shootBullet(ProjectileType.mine);
				player.altreload = reload2;
			}
		}
	},
	lancer{
		{
			speed = 0.2f;
			turnspeed = 4f;
			maxvelocity = 5f;
			shootspeed = 4;
			kiteDebuffMultiplier = 0.5f;
			maxHealth = 300;
			trailColor = Color.valueOf("e05f27");
			shootingMoveSpeedMultiplier = 0.5f;
		}
		
		public void handleInput(Player player, InputHandler input){
			player.shooting = input.leftMouseDown();
			if(player.shooting){
				if(input.laser == null){
					input.laser = player.shootBullet(ProjectileType.laser);
				}
			}else{
				if(input.laser != null){
					input.laser.removeServer();
					input.laser = null;
				}
			}
		}
	};

	protected boolean spin;
	protected float speed;
	protected float turnspeed;
	protected float maxvelocity;
	protected float shootspeed;
	protected float kiteDebuffMultiplier;
	protected float shootingMoveSpeedMultiplier = 1f;
	protected int maxHealth = 100;
	
	protected Color trailColor = Color.WHITE;
	
	//boost stuff
	//TODO set these up in a constructor
	protected int boostSpeed = 50;
	protected int boostChargeTime = 60;
	protected int boostLength = 15;

	public void handleInput(Player player, InputHandler input){

	}
	
	public Color getTrailColor(){
		return trailColor;
	}
	
	public void draw(Player player){
		Draw.rect(name(), player.x, player.y, player.getDrawRotation());
	}
	
	public int getBoostSpeed(){
		return boostSpeed;
	}
	
	public int getBoostChargeTime(){
		return boostChargeTime;
	}
	
	public int getBoostLength(){
		return boostLength;
	}

	public boolean getSpin(){
		return spin;
	}

	public float getSpeed(){
		return speed;
	}

	public float getTurnspeed(){
		return turnspeed;
	}
	
	public float getShootingMoveSpeedMultiplier(){
		return shootingMoveSpeedMultiplier;
	}

	public float getMaxVelocity(){
		return maxvelocity;
	}

	public float getShootspeed(){
		return shootspeed;
	}

	public int getMaxhealth(){
		return maxHealth;
	}

	public float getKiteDebuffMultiplier(){
		return kiteDebuffMultiplier;
	}
}
