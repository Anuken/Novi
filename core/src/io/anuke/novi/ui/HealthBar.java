package io.anuke.novi.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.VisUI;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.modules.ClientData;

public class HealthBar extends Widget{
	
	public void draw(Batch batch, float alpha){
		Player player = Novi.module(ClientData.class).player;
		
		float fract = (float)player.health / player.getShip().getMaxhealth();
		
		batch.draw(Draw.region("healthbarcontainer"), 0, 0);
		
		TextureAtlas.AtlasRegion region = Draw.region("healthbar");
		region.setRegionWidth((int)(region.getRotatedPackedWidth() * fract));
		
		batch.draw(region, 1, 1);
		
		
		VisUI.getSkin().getFont("default-font").draw(batch, (int)player.health + "/" + player.getShip().getMaxhealth(), getX() + getWidth()/2, getHeight() - 1 - 0.3f, 0, Align.center, false);
	}
	
	public float getPrefWidth(){
		return Draw.region("healthbarcontainer").getRegionWidth();
	}
	
	public float getPrefHeight(){
		return Draw.region("healthbarcontainer").getRegionHeight();
	}
}
