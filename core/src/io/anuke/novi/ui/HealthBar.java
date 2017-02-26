package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.modules.ClientData;
import io.anuke.novi.modules.Renderer;

public class HealthBar extends Widget{
	
	public void draw(Batch batch, float alpha){
		batch.setColor(Color.WHITE);
		
		float scale = 5f/Renderer.GUIscale;
		
		Player player = Novi.module(ClientData.class).player;
		
		float fract = (float)player.health / player.getShip().getMaxhealth();
		
		batch.draw(Draw.region("healthbarcontainer"), 0, 0, Draw.region("healthbarcontainer").getRegionWidth()*scale, Draw.region("healthbarcontainer").getRegionHeight()*scale);
		
		TextureAtlas.AtlasRegion region = Draw.region("healthbar");
		region.setRegionWidth((int)(region.getRotatedPackedWidth() * fract));
		
		batch.draw(region, scale, scale, region.getRotatedPackedWidth()*scale, region.getRotatedPackedHeight()*scale);
		
		
		VisUI.getSkin().getFont("default-font").draw(batch, (int)player.health + "/" + player.getShip().getMaxhealth(), getX() + getWidth()/2*scale, getHeight()*scale - 1.2f*scale, 0, Align.center, false);
	}
	
	public float getPrefWidth(){
		return Draw.region("healthbarcontainer").getRegionWidth();
	}
	
	public float getPrefHeight(){
		return Draw.region("healthbarcontainer").getRegionHeight();
	}
}
