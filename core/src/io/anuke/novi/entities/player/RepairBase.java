package io.anuke.novi.entities.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.*;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.graphics.Shaders;
import io.anuke.novi.modules.ClientData;
import io.anuke.novi.modules.UI;
import io.anuke.novi.network.SyncData;
import io.anuke.novi.network.Syncable;
import io.anuke.novi.tween.Actions;
import io.anuke.novi.ui.Landmark;
import io.anuke.novi.utils.Physics;
import io.anuke.novi.utils.Timers;

public class RepairBase extends DestructibleEntity implements Syncable, Interactable, Markable{
	public static float range = 200;
	transient boolean interacting;
	
	public RepairBase(){
		this.material.set(100);
	}
	
	@Override
	public void draw(){
		
		if(interacting)
		Draw.shader(Shaders.outline, 0.6f, 0.6f, 1f, Novi.module(UI.class).interactAlpha());
		
		Draw.rect("playerbase", x, y);
		
		if(interacting)
		Draw.shader();
		
		for(Entity entity : Entities.list()){
			if(entity instanceof Player && Vector2.dst(entity.x, entity.y, x, y) < range && ((Player)entity).isVisible()){
				Draw.colorl(0.85f + MathUtils.random(0.05f) + Math.abs(MathUtils.sin(Timers.time()/3f)/6f));
				Draw.laser("healbeam", "healbeamend", x, y, entity.x, entity.y);
				Draw.color();
			}
		}
		
		if(Novi.module(UI.class).interactAlpha() <= 0f)
		interacting = false;
	}
	
	@Override
	public void serverUpdate(){
		Physics.rectCast(x, y, range, e->{
			if(!(e instanceof Player)) return;
			Player p = (Player)e;
			if(Vector2.dst(p.x, p.y, x, y) < range){
				p.heal(0.3f);
			}
		});
	}
	
	//never collide with anything
	@Override
	public boolean collides(SolidEntity other){
		return false;
	}

	@Override
	public SyncData writeSync(){
		return new SyncData(getID());
	}

	@Override
	public void readSync(SyncData in){
		
	}

	@Override
	public String message(){
		return "press Q to switch classes";
	}

	@Override
	public void onInteracting(){
		interacting = true;
		
		if(Gdx.input.isKeyJustPressed(Keys.Q) && !Novi.module(UI.class).dialogOpen()){
			Player player = Novi.module(ClientData.class).player;
			Actions.moveTo(player, x, y, 4.5f);
			Novi.module(UI.class).openClassMenu();
		}
		
	}

	@Override
	public Landmark getLandmark(){
		return Landmark.base;
	}
}
