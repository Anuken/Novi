package io.anuke.novi.entities.effects;

import io.anuke.novi.entities.Entity;
import io.anuke.novi.server.NoviServer;

public class Effect extends Entity{
	public transient float life;
	public float delay = 0;
	public EffectType type;
	
	public Effect(){
		
	}
	
	public Effect(EffectType type){
		this.type = type;
	}
	
	public Effect delay(float time){
		this.delay = time;
		return this;
	}

	@Override
	public void update(){
		if(delay > 0){
			delay -= delta();
		}else{
			life += delta();
			if(life > type.lifetime()){
				onRemove();
				remove();
			}
		}
	}

	@Override
	public void draw(){
		if(delay <= 0)
		type.draw(this);
	}

	@Override
	public float getLayer(){
		return 1f;
	}
	
	public float fract(){
		return life/type.lifetime();
	}

	public void onRemove(){

	}

	public final Entity add(){
		if(NoviServer.active())
			throw new RuntimeException("Effects should not be added serverside!");
		return super.add();
	}
}
