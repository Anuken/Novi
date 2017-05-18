package io.anuke.novi.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.items.ShipType;
import io.anuke.novi.network.packets.ClassSwitchPacket;
import io.anuke.novi.network.packets.InputPacket;
import io.anuke.novi.utils.InputType;
import io.anuke.ucore.core.Inputs;
import io.anuke.ucore.modules.Module;

public class Input extends Module<Novi>{
	Player player;

	public void init(){
		player = getModule(ClientData.class).player;
		
		Inputs.addProcessor(this);
	}

	@Override
	public void update(){
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)){
			Gdx.app.exit();
		}
		
		if(Gdx.input.isKeyJustPressed(Keys.M)){
			getModule(UI.class).toggleMap();
		}

		if(player.isDead() || getModule(UI.class).dialogOpen())
			return;

		float angle = -9; //why is it -9?
		
		if(up())
			angle = 90;
		if(left())
			angle = 180;
		if(down())
			angle = 270;
		if(right())
			angle = 0;
		if(up() && right())
			angle = 45;
		if(up() && left())
			angle = 135;
		if(down() && right())
			angle = 315;
		if(down() && left())
			angle = 225;

		if(angle > -1){
			player.moving = true;
			
			float lastx = player.x, lasty = player.y;
			
			player.move(angle);
			
			float dx = player.x - lastx, dy = player.y - lasty;
			
			getModule(World.class).mapX += dx/World.mapSpeed;
			getModule(World.class).mapY += dy/World.mapSpeed;
			
		}else{
			player.moving = false;
		}

		if(boost())
			player.boost();

		if(Gdx.input.isButtonPressed(Buttons.LEFT) && !Gdx.input.isKeyPressed(Keys.SHIFT_LEFT)){
			player.shooting = true;
		}else{
			player.shooting = false;
		}
	}

	boolean left(){
		return Gdx.input.isKeyPressed(Keys.A);
	}

	boolean right(){
		return Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.E);
	}

	boolean up(){
		return Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.COMMA);
	}

	boolean down(){
		return Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.O);
	}

	boolean boost(){
		return Gdx.input.isKeyPressed(Keys.SPACE);
	}
	
	public void switchClass(ShipType type){
		ClassSwitchPacket p = new ClassSwitchPacket();
		p.type = type;
		getModule(Network.class).client.sendTCP(p);
	}

	void sendInput(InputType type){
		InputPacket input = new InputPacket();
		input.input = type;
		getModule(Network.class).client.sendTCP(input);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button){
		if(getModule(UI.class).dialogOpen()) return false;
		//new Effect(EffectType.hit).set(player.x + 30, player.y + 30).add();
		player.rotation = player.velocity.angle();
		player.valigned = false;
		sendInput(button == Buttons.LEFT ? InputType.LEFT_CLICK_DOWN : InputType.RIGHT_CLICK_DOWN);
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button){
		sendInput(button == Buttons.LEFT ? InputType.LEFT_CLICK_UP : InputType.RIGHT_CLICK_UP);
		if(player.velocity.isZero(0.01f)){
			player.velocity.x = 0.01f;
			player.velocity.setAngle(player.rotation);
		}
		return false;
	}

	@Override
	public boolean scrolled(int amount){
		if(!getModule(UI.class).dialogOpen())
		getModule(Renderer.class).zoom(amount / 10f);
		return false;
	}
}
