package io.anuke.novi.modules;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;

import io.anuke.SceneModule;
import io.anuke.novi.Novi;
import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Entity;
import io.anuke.novi.entities.Interactable;
import io.anuke.novi.entities.player.Player;
import io.anuke.novi.graphics.Draw;
import io.anuke.novi.items.ShipType;
import io.anuke.novi.tween.Actions;
import io.anuke.novi.ui.HealthBar;
import io.anuke.novi.ui.MapScreen;
import io.anuke.novi.ui.UIUtils;
import io.anuke.scene.style.Styles;
import io.anuke.scene.style.TextureRegionDrawable;
import io.anuke.scene.ui.*;
import io.anuke.scene.ui.layout.Table;
import io.anuke.scene.utils.CursorManager;
import io.anuke.ucore.core.UInput;

public class UI extends SceneModule<Novi>{
	private boolean mapRefresh = false;
	public Table hudtable;
	public Label inText; //interact label text
	public MapScreen map;
	Dialog classMenu;
	Table mapMenu;
	
	public UI(){
		loadSkin();
		setup();
	}
	
	private void setup(){
		hudtable = fill();
		
		Styles.styles.getFont("default-font").setUseIntegerPositions(false);
		Styles.styles.getFont("default-font").getData().setScale(1f/Renderer.GUIscale);
		
		setupHUD();
		setupMenus();
		
		UInput.flipProcessors();
	}
	
	public void init(){
		Cursor cur = getModule(Renderer.class).cursor;
		CursorManager.arrow = cur;
		CursorManager.hand = cur;
		CursorManager.ibeam = cur;
	}
	
	private void setupMenus(){
		Table center = fill();
		
		mapMenu = new Table();
		mapMenu.background("window");
		mapMenu.add((map=new MapScreen())).size(600);
		
		center.add(mapMenu).expand().size(600);
		mapMenu.setVisible(false);
		
		classMenu = new Dialog("Classes"){
			public void result(Object object){
				Actions.clear(getModule(ClientData.class).player);
			}
		};
		UIUtils.addCloseButton(classMenu);
		classMenu.setResizable(false);
		classMenu.setMovable(false);
		classMenu.getTitleTable().getCells().first().padLeft(10);
		classMenu.getTitleLabel().setColor(Color.YELLOW);
		
		Table table = classMenu.getContentTable();
		
		table.pad(10);
		
		for(ShipType ship : ShipType.values()){
			ImageButton button = new ImageButton(new TextureRegionDrawable(Draw.region(ship.name())));
			button.clicked(()->{
				getModule(Input.class).switchClass(ship);
				Actions.clear(getModule(ClientData.class).player);
				classMenu.hide();
			});
			
			button.getImageCell().size(100);
			
			table.add(ship.name()).align(Align.left);
			table.add().row();
			
			table.add(button);
			
			table.add(ship.getDescription());
			
			table.row().padTop(8);
		}
		
		TextButton cancel = new TextButton("Cancel");
		cancel.pad(10f);
	
		classMenu.button(cancel, false);
	}
	
	private void setupHUD(){
		HealthBar bar = new HealthBar();
		hudtable.left().bottom().add(bar);
		
		inText = new Label("");
		inText.setFillParent(true);
		inText.setColor(1, 1, 1, 0);
		inText.setAlignment(Align.center);
		scene.add(inText);
	}
	
	private void loadSkin(){
		Styles.load(new Styles(Gdx.files.internal("ui/uiskin.json")));
	}
	
	private void updateUIibility(){
		if(mapRefresh){
			map.updateMap();
			mapRefresh = false;
		}
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
	
	public void updateMap(){
		mapRefresh = true;
	}
	
	public void toggleMap(){
		if(mapMenu.isVisible()){
			mapMenu.setVisible(false);
			//stage.setKeyboardFocus(null);
			scene.setScrollFocus(null);
		}else{
			getModule(Network.class).requestMap();
			mapMenu.setVisible(true);
			//stage.setKeyboardFocus(map);
			scene.setScrollFocus(map);
		}
	}
	
	public void openClassMenu(){
		classMenu.show(scene);
	}
	
	public float interactAlpha(){
		return inText.getColor().a;
	}
	
	public boolean dialogOpen(){
		return scene.getKeyboardFocus() != null;
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
		updateUIibility();
		updateInteractions();
		
		scene.act();
		scene.draw();
		
		getModule(Renderer.class).recorder.update();
	}
}
