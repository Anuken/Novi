package io.anuke.novi.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;

import io.anuke.novi.Novi;
import io.anuke.novi.ui.HealthBar;
import io.anuke.ucore.modules.Module;

public class UI extends Module<Novi>{
	Stage stage;
	
	public UI(){
		loadSkin();
		stage = new Stage(new ScreenViewport());
		setup();
	}
	
	public void setup(){
		VisTable hudtable = new VisTable();
		hudtable.setFillParent(true);
		stage.addActor(hudtable);
		
		HealthBar bar = new HealthBar();
		hudtable.left().bottom().add(bar);
		
		VisUI.getSkin().getFont("default-font").setUseIntegerPositions(false);
		VisUI.getSkin().getFont("default-font").getData().setScale(1f/Renderer.GUIscale);
	}
	
	public void loadSkin(){
		VisUI.load(Gdx.files.internal("ui/uiskin.json"));
	}
	
	public void updateUIVisibility(){
		
	}
	
	@Override
	public void update() {
		updateUIVisibility();
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width/Renderer.GUIscale, height/Renderer.GUIscale, true);
	}
}
