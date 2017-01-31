package io.anuke.novi.server;

import java.util.HashSet;

import io.anuke.novi.Novi;
import io.anuke.novi.entities.Entities;

public class NoviUpdater{
	NoviServer server;
	private boolean isRunning = true;
	final int maxfps = 60;
	long frameid;
	float delta = 1f;
	long lastFpsTime;
	HashSet<Long> collided = new HashSet<Long>(); //used for storing collisions each frame so entities don't collide twice

	void Loop(){
		try{
			Entities.updateAll();
		}catch(Exception e){
			e.printStackTrace();
			Novi.log("Entity update loop error!");
		}
	}
	
	public long frameID(){
		return frameid;
	}

	public float delta(){
		return delta;
	}

	public void run(){
		int fpsmillis = 1000 / maxfps;
		while(isRunning){
			long start = System.currentTimeMillis();
			Loop();
			frameid ++;
			//if(frame % 60 == 0)Novi.log(delta + " | " + Entity.entities.size());
			long end = System.currentTimeMillis();

			try{
				if(end - start <= fpsmillis) Thread.sleep(fpsmillis - (end - start));
			}catch(Exception e){
				e.printStackTrace();
			}
			long sleepend = System.currentTimeMillis();
			delta = (sleepend - start) / 1000f * 60f;
		}

	}

	public NoviUpdater(NoviServer server){
		this.server = server;
	}
}
