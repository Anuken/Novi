package io.anuke.novi.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.Interactable;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.items.ShipType;
import io.anuke.novi.ui.HealthBar;
import io.anuke.ucore.modules.Module;

public class UI extends Module<Novi>{
	public Stage stage;
	public VisTable hudtable;
	public VisLabel inText; //interact label text
	VisDialog classMenu;
	
	public UI(){
		loadSkin();
		stage = new Stage(new ScreenViewport());
		setup();
	}
	
	private void setup(){
		hudtable = new VisTable();
		hudtable.setFillParent(true);
		stage.addActor(hudtable);
		
		VisUI.getSkin().getFont("default-font").setUseIntegerPositions(false);
		VisUI.getSkin().getFont("default-font").getData().setScale(1f/Renderer.GUIscale);
		
		setupHUD();
		setupMenus();
	}
	
	private void setupMenus(){
		classMenu = new VisDialog("Classes");
		classMenu.addCloseButton();
		classMenu.setResizable(false);
		classMenu.setMovable(false);
		
		Table table = classMenu.getContentTable();
		
		table.pad(10);
		
		//table.add("Classes");
		//table.row();
		
		for(ShipType ship : ShipType.values()){
			VisImageButton button = new VisImageButton(new TextureRegionDrawable(Draw.region(ship.name())));
			button.addListener(new ClickListener(){
				public void clicked(InputEvent event, float x, float y){
					getModule(Input.class).switchClass(ship);
					classMenu.hide();
				}
			});
			
			button.getImageCell().size(100);
			
			table.add(button);
			
			table.add(ship.getDescription());
			
			table.row().padTop(8);
		}
		
		//Table buttons = classMenu.getButtonsTable();
		
		VisTextButton cancel = new VisTextButton("Cancel");
		cancel.pad(10f);
	
		classMenu.button(cancel, false);
	}
	
	private void setupHUD(){
		HealthBar bar = new HealthBar();
		hudtable.left().bottom().add(bar);
		

		inText = new VisLabel();
		inText.setFillParent(true);
		inText.setColor(1, 1, 1, 0);
		inText.setAlignment(Align.center);
		stage.addActor(inText);
	}
	
	private void loadSkin(){
		VisUI.load(Gdx.files.internal("ui/uiskin.json"));
	}
	
	private void updateUIVisibility(){
		
	}
	
	private void updateInteractions(){
		Player player = Novi.module(ClientData.class).player;
		
		Interactable i = null;
		
		for(Entity e : Entities.list()){
			if(e instanceof Interactable){
				e.getBoundingBox(Rectangle.tmp);
				if(Rectangle.tmp.contains(player.x, player.y)){
					i = (Interactable)e;
				}
			}
		}
		
		if(i != null){
			i.onInteracting();
			inText.getColor().a += 0.008f;
			
			//TODO less hacky way to offset text
			inText.setText(i.message() + "\n\n\n\n\n\n");
		}else{
			inText.getColor().a -= 0.008f;
		}
		
		inText.getColor().clamp();
	}
	
	public void openClassMenu(){
		classMenu.show(stage);
	}
	
	public float interactAlpha(){
		return inText.getColor().a;
	}
	
	public boolean dialogOpen(){
		return stage.getKeyboardFocus() != null;
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
