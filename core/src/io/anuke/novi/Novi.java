package io.anuke.novi;

import com.badlogic.gdx.Gdx;

import io.anuke.novi.entities.Entities;
import io.anuke.novi.modules.*;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.systems.EntityLoadedSystem;
import io.anuke.ucore.modules.ModuleController;

public class Novi extends ModuleController<Novi>{
	private static final boolean logtrace = false;

	@Override
	public void init(){
		addModule(World.class);
		addModule(Renderer.class);
		addModule(Input.class);
		addModule(Network.class);
		addModule(ClientData.class);
		
		
		Entities.setBaseSystem(new EntityLoadedSystem(getModule(ClientData.class).player));
		//Entities.addSystem(new SpatialSystem());
	}

	@Override
	public void render(){
		Entities.updateAll();
		Entities.checkUnload(getModule(ClientData.class).player.x, getModule(ClientData.class).player.y);
		
		super.render();
	}
	
	public static float delta(){
		return NoviServer.active() ? NoviServer.instance().delta() : Gdx.graphics.getDeltaTime()*60f;
	}
	
	public static long frame(){
		return NoviServer.active() ? NoviServer.instance().updater.frameID() : Gdx.graphics.getFrameId();
	}

	public static void log(Object o){
		if(o instanceof Exception){
			((Exception)o).printStackTrace();
			return;
		}
		if( !logtrace){
			System.out.println(o);
		}else{
			logtrace(o);
		}

	}

	public static void logtrace(Object o){
		StackTraceElement element = Thread.currentThread().getStackTrace()[2];
		System.out.println("[" + element.getMethodName() + "() @ " + element.getFileName().replace(".java", "") + "]: " + o);
	}
	
	public static void logtrace(Object o, int i){
		StackTraceElement element = Thread.currentThread().getStackTrace()[i];
		System.out.println("[" + element.getMethodName() + "() @ " + element.getFileName().replace(".java", "") + "]: " + o);
	}
}
