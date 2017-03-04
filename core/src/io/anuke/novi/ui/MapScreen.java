package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.novi.Novi;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.modules.ClientData;
import io.anuke.novi.modules.World;
import io.anuke.ucore.UCore;
import io.anuke.ucore.graphics.Hue;

public class MapScreen extends Group{
	private Array<MapObject> objects = new Array<MapObject>();
	MapObject mplayer = new MapObject(new Marker(MarkerType.player, 0, 0));
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
		float mscl = 1f;
		ClickListener click;
		
		public MapObject(Marker marker){
			this.marker = marker;
			addListener(click = new ClickListener());
		}
		
		@Override
		public void setPosition(float x, float y){
			super.setPosition(x - getWidth()/2, y - getHeight()/2+25);
		}
		
		@Override
		public void draw(Batch batch, float alpha){
			if(click.isOver()){
				mscl += 0.01f;
				toFront();
			}else{
				mscl -= 0.01f;
			}
			
			mscl = UCore.clamp(mscl, 1f, 2f);
			
			setColor(Hue.mix(marker.type.color(), Color.CORAL, mscl-1f));
			
			batch.setColor(getColor().r, getColor().g, getColor().b, alpha);
			
			float scl = 4*mscl;
			
			float x = getX();
			float y = getY();
			float s = 8*scl;
			
			setSize(s, s);
			
			float w = MapScreen.this.getWidth()*zoom;
			float h = MapScreen.this.getHeight()*zoom;
			
			if(x + s > w) draw(batch, x - w, y);
			if(x - s < 0) draw(batch, x + w, y);
			
			if(y + s > h) draw(batch, x, y - h);
			if(y - s < 0) draw(batch, x, y + h);
			
			draw(batch, x, y);
			
			BitmapFont font = VisUI.getSkin().getFont("default-font");
			
			font.setColor(1,1,1,(mscl-1f));
			
			GlyphLayout lay = Pools.obtain(GlyphLayout.class);
			
			String text = marker.type.description();
			
			lay.setText(font, text);
			
			font.draw(batch, text, getX() + getWidth()/2 + lay.width/2f, getY(), Align.top, 0, false);
		
			Pools.free(lay);
			
			font.setColor(Color.WHITE);
		}
		
		void draw(Batch batch, float x, float y){
			batch.draw(Draw.region("marker-" + marker.type.texture()), x, y, getWidth(), getHeight());
		}
	}
}
