package io.anuke.novi.utils;

import java.util.function.Consumer;

import io.anuke.novi.entities.Entities;
import io.anuke.novi.entities.Entity;

public class Physics{
	
	public static void rectCast(float x, float y, float size, Consumer<Entity> cons){
		Entities.spatial().getNearby(x, y, size, cons);
	}
}
