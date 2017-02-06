package io.anuke.novi.entities.effects;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

import io.anuke.novi.utils.Draw;
import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.Hue;

public enum EffectType{
	explosion(7){
		public int lifetime(){
			return 20;
		}
	},
	shockwave{

	},
	smoke(5){
		public void draw(Effect e){
			rand.setSeed(e.getID());

			float f = 0.8f - (e.life / e.type.lifetime()) / 1.4f;
			float a = 1f - e.fract();

			a = a < 0.3f ? a / 0.3f : 1f;

			Draw.colorl(f, a);

			for(int i = 0; i < 5; i++){
				float x = rand.nextFloat() - 0.5f;
				float y = rand.nextFloat() - 0.5f;
				float len = 60f + rand.nextFloat() * 50f - e.fract() * 15f;

				Draw.rect(frame(e.life), e.x + x * e.fract() * len, e.y + y * e.fract() * len);

			}

			Draw.color();
		}

		public int lifetime(){
			return 40;
		}
	},
	singlesmoke(5){
		public void draw(Effect e){
			rand.setSeed(e.getID());

			//float f = 0.5f - (e.life / e.type.lifetime()) / 1.4f;
			float a = 1f - e.fract();

			a = a < 0.3f ? a / 0.3f : 1f;
			
			Hue.mix(Color.valueOf("7cd99bff"), color.set(0.4f, 0.4f, 0.4f, 1f), e.fract(), color);
			color.a = a;

			Draw.color(color);

			Draw.rect("smoke" + (frameid(e.life)+3), e.x, e.y);

			Draw.color();
		}

		public int lifetime(){
			return 30;
		}

		public String drawName(){
			return "smoke";
		}
	},
	hit(5){
		public void draw(Effect e){
			Draw.color("82f4a8ff");
			super.draw(e);
			Draw.color();
		}

		public int lifetime(){
			return 20;
		}
	};
	protected static Random rand = new Random();
	protected String[] frames;
	protected Color color = new Color();

	private EffectType(String... frames) {
		this.frames = frames;
	}

	private EffectType(int frameamount) {
		frames = new String[frameamount];

		for(int i = 0; i < frames.length; i++)
			frames[i] = drawName() + (i + 1);
	}

	public String drawName(){
		return name();
	}

	public int lifetime(){
		return 100;
	}
	
	int frameid(float life){
		return UCore.clamp((int) (UCore.clamp(life / lifetime()) * frames.length), 0, frames.length - 1);
	}

	public String frame(float life){
		int i = UCore.clamp((int) (UCore.clamp(life / lifetime()) * frames.length), 0, frames.length - 1);
		return frames[i];
	}

	public void draw(Effect e){
		if(frames != null && frames.length > 0){
			Draw.rect(frame(e.life), e.x, e.y);
		}else{
			Draw.rect("error", e.x, e.y);
		}
	}
}
