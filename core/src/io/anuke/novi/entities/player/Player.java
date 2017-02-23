package io.anuke.novi.entities.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Connection;

import io.anuke.novi.effects.EffectType;
import io.anuke.novi.effects.Effects;
import io.anuke.novi.entities.DestructibleEntity;
import io.anuke.novi.entities.SolidEntity;
import io.anuke.novi.entities.basic.Bullet;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.graphics.Shaders;
import io.anuke.novi.items.ProjectileType;
import io.anuke.novi.items.ShipType;
import io.anuke.novi.modules.Network;
import io.anuke.novi.modules.Renderer;
import io.anuke.novi.modules.World;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.network.packets.DeathPacket;
import io.anuke.novi.server.InputHandler;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.utils.InterpolationData;
import io.anuke.novi.utils.Timers;
import io.anuke.ucore.UCore;
import io.anuke.ucore.modules.ModuleController;
import io.anuke.ucore.util.Angles;

public class Player extends DestructibleEntity implements Syncable{
	public transient Connection connection;
	public transient boolean client = false;
	public String name;
	private float respawntime;

	/** State only used for remote players */
	public transient ShipState state;
	public transient boolean valigned = true;
	public transient boolean shooting, moving; //used for aligning the rotation after you shoot and let go of the mouse
	/** note that rotation is only for remote players or shooting */
	public transient float rotation = 0;
	public transient float reload, altreload = 0, ping;
	transient InterpolationData data = new InterpolationData();
	public transient InputHandler input;

	//boost stuff
	private boolean boosting = false;
	private float boostingTime = 0;

	private ShipType ship = ShipType.lancer;

	public enum ShipState{
		shooting{
			public boolean is(Player p){
				return p.state == shooting || p.shooting;
			}
		},
		moving{
			public boolean is(Player p){
				return p.state == moving || p.moving;
			}
		},
		boosting{
			public boolean is(Player p){
				return p.state == boosting || p.boosting;
			}
		},
		dead{
			public boolean is(Player p){
				return p.state == dead || p.isDead();
			}
		},
		still{
			public boolean is(Player p){
				return p.state == still;
			}
		};

		/** Returns whether or not the player is in this state */
		public abstract boolean is(Player p);
	}

	{
		material.init(16, 0.01f);
		health = ship.getMaxhealth();
	}

	@Override
	public void update(){

		if(respawntime > 0){
			respawntime -= delta();
			health = 0;
			if(respawntime <= 0){
				x = World.size/2;
				y = World.size/2;
				if(NoviServer.active())
					Effects.effect(EffectType.shockwave, x, y);
			}
		}
		if(reload > 0)
			reload -= delta();
		if(altreload > 0)
			altreload -= delta();

		if(NoviServer.active())
			return; //don't want to do stuff like getting the mouse angle on the server, do we?

		if(!client)
			data.update(this);
		else
			updateVelocity();

		//updateBounds();
		if(!boosting)
			velocity.limit(ship.getMaxVelocity() * kiteChange() * (shooting ? ship.getShootingMoveSpeedMultiplier() : 1f));
		if(rotation > 360f && !ship.getSpin())
			rotation -= 360f;
		if(rotation < 0f && !ship.getSpin())
			rotation += 360f;

		if(shooting && !boosting){
			rotation = Angles.MoveToward(rotation, Angles.mouseAngle(ModuleController.module(Renderer.class).camera, x, y), ship.getTurnspeed() * delta());
		}else{
			//align player rotation to velocity rotation
			if(!valigned)
				rotation = Angles.MoveToward(rotation, velocity.angle(), ship.getTurnspeed());
		}

		boostUpdate();
	}

	//don't want to hit other players or other bullets
	public boolean collides(SolidEntity other){
		return respawntime <= 0 && super.collides(other) && !(other instanceof Player || (other instanceof Bullet && ((Bullet) other).shooter() instanceof Player));
	}

	public ShipType getShip(){
		return ship;
	}

	@Override
	public void serverUpdate(){
		//TODO make this FPS independant
		input.update();
		if(frame() % 30 == 0)
			connection.updateReturnTripTime();
	}

	public Player() {
		if(NoviServer.active())
			input = new InputHandler(this);
	}

	public float kiteChange(){
		if(!shooting){
			return 1f;
		}else{
			return 1f - Angles.angleDist(rotation, velocity.angle()) / (180f * 1f / ship.getKiteDebuffMultiplier());
		}
	}

	public void move(float angle){
		velocity.add(new Vector2(1f, 1f).setAngle(angle).setLength(ship.getSpeed() * delta() * (shooting ? ship.getShootingMoveSpeedMultiplier() : 1f)));
	}

	//TODO make boosting serverside
	public void boost(){
		if(!boosting){
			//not boosting currently, so charge boost
			boosting = true;
			boostingTime = -1 * ship.getBoostChargeTime();
			Effects.effect(EffectType.smoke, x, y);
		}
	}

	//TODO fix crude clientside effects and make them work in a proper animation state
	public void boostUpdate(){

		if(boosting){
			if(boostingTime < 0){
				boostingTime += delta();
			}else{
				if(boostingTime < ship.getBoostLength()){
					boostingTime += delta();
					velocity.setLength(ship.getMaxVelocity() + ship.getBoostSpeed() * (1.2f - boostingTime / ship.getBoostLength()));
				}else{
					boosting = false;
					boostingTime = 0;
				}
			}
		}
	}

	public float getSpriteRotation(){
		return (!shooting && valigned) ? velocity.angle() - 90 : this.rotation - 90;
	}

	public float getDrawRotation(){
		return client ? getSpriteRotation() : NoviServer.active() ? rotation - 90 : rotation;
	}

	public boolean loaded(float playerx, float playery){
		return true;
	}

	@Override
	public float getLayer(){
		return 1f;
	}

	@Override
	public void draw(){
		if(respawntime > 0)
			return;

		Draw.shader(Shaders.outline, 0.5f, 1f, 0f);
		
		ship.draw(this);
		
		Draw.shader();
		
		if(!client){
			Draw.tcolor(Color.GOLD);
			Draw.text(name, x, y + 14);
			Draw.tcolor();
		}

		Vector2 back = Angles.translation(getSpriteRotation() - 90, 12f);

		if(inState(ShipState.moving) && Timers.get(this, 4)){
			Effects.effect(EffectType.singlesmoke, x + back.x, y + back.y, ship.getTrailColor());
		}

		if(inState(ShipState.boosting) && Timers.get(this, 3)){
			Effects.effect(EffectType.singlesmoke, x + back.x + MathUtils.random(-5, 5), y + back.y + MathUtils.random(-5, 5), Color.CORAL);
		}
	}

	@Override
	public void onDeath(){
		
		if(input != null && input.laser != null)
			input.laser.removeServer();
		
		if(NoviServer.active()){
			Effects.shake(50f, 50f, x, y);
			Effects.effect(EffectType.explosion, x, y);
			Effects.effect(EffectType.shockwave, x, y);
			Effects.blockbreak(ship.name(), x, y, 2f, velocity);
			health = ship.getMaxhealth();
			connection.sendTCP(new DeathPacket());
		}
		
		velocity.set(0, 0);
		respawntime = 150;
	}

	public boolean inState(ShipState state){
		return state.is(this);
	}

	//returns whether or not enemies should target this player
	public boolean isVisible(){
		return respawntime <= 0;
	}

	public boolean isDead(){
		return respawntime > 0;
	}

	//don't want the player entity getting removed
	@Override
	public boolean removeOnDeath(){
		return false;
	}

	public Bullet shootBullet(ProjectileType type){
		Bullet b = new Bullet(type, rotation + 90);
		b.x = predictedX();
		b.y = predictedY();
		b.setShooter(this);
		b.add().send();
		return b;
	}

	public float pingInFrames(){
		if(!NoviServer.active())
			return 0;
		return ((Network.ping * 2f + connection.getReturnTripTime()) / 1000f) * delta() * 60f + 1.1f;
	}

	public float predictedX(){
		return velocity.x * pingInFrames() + x;
	}

	public float predictedY(){
		return velocity.y * pingInFrames() + y;
	}

	public int connectionID(){
		return connection.getID();
	}
	
	public boolean heal(float amount){
		if(health >= ship.getMaxhealth()) return true;
		health = UCore.clamp(health + amount, 0, ship.getMaxhealth());
		return false;
	}

	public ShipState getState(){
		if(isDead()){
			return ShipState.dead;
		}else if(boosting && boostingTime < ship.getBoostLength()){
			return ShipState.boosting;
		}else if(shooting){
			return ShipState.shooting;
		}else if(moving){
			return ShipState.moving;
		}else{
			return ShipState.still;
		}
	}

	@Override
	public SyncData writeSync(){
		return new SyncData(getID(), x, y, getState(), rotation, pingInFrames(), velocity);
	}

	@Override
	public void readSync(SyncData in){
		velocity = in.get(5);
		this.state = in.get(2);
		this.ping = in.get(4);
		data.push(this, in.get(0), in.get(1), in.get(3));
	}
}
