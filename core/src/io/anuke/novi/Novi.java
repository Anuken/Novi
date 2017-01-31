package io.anuke.novi;

import com.badlogic.gdx.Gdx;

import io.anuke.novi.entities.Entities;
import io.anuke.novi.modules.*;
import io.anuke.novi.server.NoviServer;
import io.anuke.novi.systems.EntityLoadedSystem;
import io.anuke.ucore.modules.ModuleController;

public class Novi extends ModuleController<Novi>{
	static final boolean logtrace = false;
	private static LogModule logger;

	@Override
	public void init(){
		addModule(Renderer.class);
		addModule(Input.class);
		addModule(Network.class);
		addModule(ClientData.class);
		addModule(World.class);
		addModule(LogModule.class);
		logger = getModule(LogModule.class);
		
		Entities.setBaseSystem(new EntityLoadedSystem(getModule(ClientData.class).player));
	}

	@Override
	public void render(){
		//update all entities
		Entities.updateAll();
		
		super.render();
	}
	
	public static float delta(){
		return NoviServer.active() ? NoviServer.instance().delta() : Gdx.graphics.getDeltaTime()*60f;
	}
	
	public static long frame(){
		return NoviServer.active() ? NoviServer.instance().updater.frameID() : Gdx.graphics.getFrameId();
	}

	public static void log(Object o){
		if(logger != null) logger.logged(o);
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
}
