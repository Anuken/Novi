package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Align;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.modules.ClientData;
import io.anuke.novi.modules.Renderer;
import io.anuke.scene.Element;
import io.anuke.scene.style.Styles;

public class HealthBar extends Element{
	
	public void draw(Batch batch, float alpha){
		batch.setColor(Color.WHITE);
		
		float scale = 5f/Renderer.GUIscale;
		
		Player player = Novi.module(ClientData.class).player;
		
		float fract = (float)player.health / player.getShip().getMaxhealth();
		
		batch.draw(Draw.region("healthbarcontainer"), 0, 0, Draw.region("healthbarcontainer").getRegionWidth()*scale, Draw.region("healthbarcontainer").getRegionHeight()*scale);
		
		TextureAtlas.AtlasRegion region = Draw.region("healthbar");
		region.setRegionWidth((int)(region.getRotatedPackedWidth() * fract));
		
		batch.draw(region, scale, scale, fract*region.getRotatedPackedWidth()*scale, region.getRotatedPackedHeight()*scale);
		
		
		Styles.styles.getFont("default-font").draw(batch, (int)player.health + "/" + player.getShip().getMaxhealth(), getX() + getWidth()/2*scale, getHeight()*scale - 1.2f*scale, 0, Align.center, false);
	}
	
	public float getPrefWidth(){
		return Draw.region("healthbarcontainer").getRegionWidth();
	}
	
	public float getPrefHeight(){
		return Draw.region("healthbarcontainer").getRegionHeight();
	}
}
