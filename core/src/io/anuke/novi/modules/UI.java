package io.anuke.novi.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.entities.player.RepairBase;
import io.anuke.novi.ui.HealthBar;
import io.anuke.ucore.modules.Module;

public class UI extends Module<Novi>{
	Stage stage;
	VisTable hudtable;
	VisLabel classText;
	
	public UI(){
		loadSkin();
		stage = new Stage(new ScreenViewport());
		setup();
	}
	
	public void setup(){
		hudtable = new VisTable();
		hudtable.setFillParent(true);
		stage.addActor(hudtable);
		
		VisUI.getSkin().getFont("default-font").setUseIntegerPositions(false);
		VisUI.getSkin().getFont("default-font").getData().setScale(1f/Renderer.GUIscale);
		
		setupHUD();
	}
	
	public void setupHUD(){
		HealthBar bar = new HealthBar();
		hudtable.left().bottom().add(bar);
		
		classText = new VisLabel("press K to change class");
		classText.setFillParent(true);
		classText.setColor(1, 1, 1, 0);
		classText.setAlignment(Align.center);
		stage.addActor(classText);
	}
	
	public void loadSkin(){
		VisUI.load(Gdx.files.internal("ui/uiskin.json"));
	}
	
	public void updateUIVisibility(){
		
	}
	
	public void updateInteractions(){
		Player player = Novi.module(ClientData.class).player;
		
		boolean nearClass = false;
		for(Entity e : Entities.list()){
			if(e instanceof RepairBase && Math.abs(e.x - player.x) < 100 && Math.abs(e.y - player.y) < 100){
				nearClass = true;
				break;
			}
		}
		
		if(nearClass){
			classText.setColor(1, 1, 1, classText.getColor().a + 0.006f);
		}else{
			classText.setColor(1, 1, 1, classText.getColor().a - 0.006f);
		}
	}
	
	//returns screen width / scale
	public float ghwidth(){
		return Gdx.graphics.getWidth() / Renderer.GUIscale;
	}

	//returns screen height / scale
	public float ghheight(){
		return Gdx.graphics.getHeight() / Renderer.GUIscale;
	}
	
	@Override
	public void update() {
		updateUIVisibility();
		updateInteractions();
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width/Renderer.GUIscale, height/Renderer.GUIscale, true);
	}
}
