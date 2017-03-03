package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.Array;

import io.anuke.novi.Novi;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.modules.ClientData;
import io.anuke.novi.modules.World;
import io.anuke.ucore.UCore;

public class MapScreen extends Group{
	private Array<MapObject> objects = new Array<MapObject>();
	MapObject mplayer = new MapObject(new Marker(Landmark.player, 0, 0));
	float zoom = 1f;
	float zoomx = 00, zoomy = 00;
	float scl = 4;
	
	public MapScreen(){
		addListener(new InputListener(){
			float lastx, lasty;
			
			public boolean scrolled (InputEvent event, float x, float y, int amount) {
				zoom -= amount/10f;
				zoom = UCore.clamp(zoom, 1f, 10f);
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
				
				zoomx += (x-lastx)/zoom;
				zoomy += (y-lasty)/zoom;
				
				lastx = x;
				lasty = y;
			}
		});
	}
	
	public void act(float delta){
		super.act(delta);
		
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
		
		
		dx = dx % Math.max(w*zoom, w);
		if(dx < 0) dx += Math.max(w*zoom, w);
		
		return dx;
	}
	
	public float transY(float worldy){
		float h = getHeight();
		
		float dy = worldy/World.size*h;
		
		dy += zoomy;
		
		dy -= 300;
		dy *= zoom;
		dy += 300;
		
		dy = dy % Math.max(h*zoom, h);
		if(dy < 0) dy += Math.max(h*zoom, h);
		
		return dy;
	}
	
	public void draw(Batch batch, float alpha){
		
		batch.end();
		batch.begin();
		this.clipBegin(getX(), getY()+26, getWidth()-2, getHeight()-2);
		
		super.draw(batch, alpha);
		
		applyTransform(batch, computeTransform());
		
		drawChildren(batch, alpha);
		
		float px = Novi.module(ClientData.class).player.x;
		float py = Novi.module(ClientData.class).player.y;
		
		mplayer.setPosition(transX(px), transY(py));
		
		mplayer.draw(batch, alpha);
		
		resetTransform(batch);
		
		this.clipEnd();
		batch.end();
		batch.begin();
		
	}
	
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
			
			float x = getX();
			float y = getY() + 25;
			float s = 8*scl;
			
			float w = MapScreen.this.getWidth()*zoom;
			float h = MapScreen.this.getHeight()*zoom;
			
			if(x + s > w) draw(batch, x - w, y);
			if(x - s < 0) draw(batch, x + w, y);
			
			if(y + s > h) draw(batch, x, y - h);
			if(y - s < 0) draw(batch, x, y + h);
			
			draw(batch, x, y);
		}
		
		void draw(Batch batch, float x, float y){
			batch.draw(Draw.region("landmark-"+marker.mark.texture()), x - 4*scl, y - 4*scl, 8*scl, 8*scl);
		}
	}
}
