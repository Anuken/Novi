package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Array;

import io.anuke.novi.Novi;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.modules.ClientData;
import io.anuke.novi.modules.World;

public class MapScreen extends Group{
	private Array<MapObject> objects = new Array<MapObject>();
	float zoom = 1f;
	float zoomx = 300, zoomy = 300;
	float scl = 4;
	
	public MapScreen(){
		addListener(new InputListener(){
			float lastx, lasty;
			
			public boolean scrolled (InputEvent event, float x, float y, int amount) {
				zoom -= amount/10f;
				//zoom = UCore.clamp(zoom, 0.01f, 10f);
				return false;
			}
			
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
				y -= 25;
				
				lastx = x;
				lasty = y;
				
				return true;
			}
			
			public void touchDragged (InputEvent event, float x, float y, int pointer) {
				y -= 25;
				
				zoomx -= (x-lastx);
				zoomy -= (y-lasty);
				//if(zoomx < 0) zoomx += getWidth();
				//if(zoomy < 0) zoomy += getHeight();
				//zoomx = zoomx & getWidth();
				
				lastx = x;
				lasty = y;
			}
		});
	}
	
	public void act(float delta){
		super.act(delta);
		
		//float w = getWidth();
		//float h = getHeight();
		//float x = getX();
		//float y = getY();
		//float px = Novi.module(ClientData.class).player.x;
		//float py = Novi.module(ClientData.class).player.y;
		
		for(MapObject object : objects){
			object.setPosition(transX(object.marker.x), transY(object.marker.y));
		}
	}
	
	public float transX(float worldx){
		float w = getWidth();
		
		float dx = worldx/World.size*w;
		
		dx += zoomx;
		
		dx -= 300;
		dx *= zoom;
		dx += 300;
		
		//if(dx < 0) dx += w*zoom;
		//dx = dx % w*zoom;
		
		return dx;
	}
	
	public float transY(float worldy){
		float h = getHeight();
		
		float dy = worldy/World.size*h;
		
		dy += zoomy;
		
		dy -= 300;
		dy *= zoom;
		dy += 300;
		
		//if(dy < 0) dy += h*zoom;
		//dy = dy % h*zoom;
		
		return dy;
	}
	
	public void draw(Batch batch, float alpha){
		/*
		batch.end();
		batch.begin();
		this.clipBegin(getX(), getY()+26, getWidth()-2, getHeight());
		*/
		super.draw(batch, alpha);
		/*
		this.clipEnd();
		batch.end();
		batch.begin();
		*/
		/*
		float w = getWidth();
		float h = getHeight();
		float x = getX();
		float y = getY();
		*/
		float px = Novi.module(ClientData.class).player.x;
		float py = Novi.module(ClientData.class).player.y;
		
		batch.setColor(Color.GREEN);
		batch.draw(Draw.region("landmark-player"),transX(px)-4*scl + getX(), transY(py)-4*scl + getY()+25, 8*scl, 8*scl);
	}
	
	/*
	public void draw(Batch batch, float alpha){
		Array<Marker> markers = Novi.module(ClientData.class).map;
		float w = getWidth();
		float h = getHeight();
		float x = getX();
		float y = getY();
		float px = Novi.module(ClientData.class).player.x;
		float py = Novi.module(ClientData.class).player.y;
		float scl = 4;
		
		//batch.setColor(Color.SKY.r, Color.SKY.g, Color.SKY.b, alpha);
		//batch.draw(Draw.region("blank"), x, y, w, h);
		
		for(Marker m : markers){
			float sx = m.x / World.size * w, sy = m.y / World.size * h;
			batch.setColor(m.mark.color().r, m.mark.color().g, m.mark.color().b, alpha);
			batch.draw(Draw.region("landmark-"+m.mark.texture()), x+sx - 4*scl, y+sy - 4*scl, 8*scl, 8*scl);
		}
		
		batch.setColor(0, 1, 0, alpha);
		batch.draw(Draw.region("landmark-player"), x+px/World.size*w - 4*scl, y+py/World.size*h - 4*scl, 8*scl, 8*scl);
	}
	*/
	
	public void updateMap(){
		clearChildren();
		objects.clear();
		
		Array<Marker> markers = Novi.module(ClientData.class).map;
		
		for(Marker marker : markers){
			MapObject object = new MapObject(marker);
			addActor(object);
			objects.add(object);
		}
	}
	
	class MapObject extends Actor{
		Marker marker;
		
		public MapObject(Marker marker){
			this.marker = marker;
		}
		
		public void draw(Batch batch, float alpha){
			batch.setColor(marker.mark.color().r, marker.mark.color().g, marker.mark.color().b, alpha);
			
			batch.draw(Draw.region("landmark-"+marker.mark.texture()), getX() - 4*scl, 25+getY() - 4*scl, 8*scl, 8*scl);
		}
	}
}
