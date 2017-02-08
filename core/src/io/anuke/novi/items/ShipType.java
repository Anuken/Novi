package io.anuke.novi.items;

import io.anuke.novi.entities.base.Player;
import io.anuke.novi.server.InputHandler;

public enum ShipType{
	arrowhead{
		final float reload2 = 3f;
		
		{
			speed = 0.2f;
			turnspeed = 12f;
			maxvelocity = 5f;
			shootspeed = 4;
			kiteDebuffMultiplier = 0.5f;
			maxHealth = 300;
		}
		
		public void handleInput(Player player, InputHandler input){
			player.shooting = input.leftMouseDown();
			if(input.leftMouseDown() && player.reload <= 0){
				player.bullet(ProjectileType.plasmabullet);
				player.reload = player.getShip().getShootspeed();
			}
			
			if(input.rightMouseDown() && player.altreload <= 0){
				player.bullet(ProjectileType.mine);
				player.altreload = reload2;
			}
		}
	};

	protected boolean spin;
	protected float speed;
	protected float turnspeed;
	protected float maxvelocity;
	protected float shootspeed;
	protected float kiteDebuffMultiplier;
	protected int maxHealth = 100;
	
	//boost stuff
	//TODO set these up in a constructor
	protected int boostSpeed = 10;
	protected int boostChargeTime = 20;
	protected int boostLength = 60;

	public void handleInput(Player player, InputHandler input){

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
